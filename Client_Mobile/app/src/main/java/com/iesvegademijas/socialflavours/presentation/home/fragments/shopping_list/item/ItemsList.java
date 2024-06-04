package com.iesvegademijas.socialflavours.presentation.home.fragments.shopping_list.item;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.adapter.RecipeAdapter;
import com.iesvegademijas.socialflavours.data.adapter.ShoppingListItemAdapter;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Item;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemsList extends AppCompatActivity implements ShoppingListItemAdapter.ShoppingListItemAdapterCallBack {
    private long id_shoppingList;
    private static final int MAX_RETRIES = 5;
    private ListView listView;
    private ArrayList<Item> itemModels;
    private ShoppingListItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_items_list);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id_shoppingList")) {
            String value = intent.getStringExtra("id_shoppingList");
            id_shoppingList = Long.parseLong(value);
        }

        setUpItems();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setUpItems()
    {
        ProgressBar pbItemList = findViewById(R.id.pb_shopping_list_items);
        pbItemList.setVisibility(View.VISIBLE);
        String url = R.string.main_url + "itemapi/getAllItemsFromShoppingList" + id_shoppingList;
        if (isNetworkAvailable()) {
            for (int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
                try {
                    getListTask(url);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (retryCount < MAX_RETRIES) {
                        showError(e.getMessage());
                    } else {
                        showError("error.connection");
                    }
                }
            }
        } else {
            showError("error.IOException");
        }
    }

    private void getListTask(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiOperator apiOperator= ApiOperator.getInstance();
                    String result = apiOperator.getString(url);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(result.equalsIgnoreCase("error.IOException")||
                                    result.equals("error.OKHttp")) {
                                showError(result);
                            }
                            else if(result.equalsIgnoreCase("null")){
                                showError("error.Unknown");
                            } else{
                                resetList(result);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showError(e.getMessage());
                        }
                    });
                }

            }
        });
    }

    private void resetList(String result)
    {
        try
        {
            JSONArray itemList = new JSONArray(result);

            if (itemModels == null)
            {
                itemModels = new ArrayList<>();
            }
            else
            {
                itemModels.clear();
            }

            for (int i = 0; i < itemList.length(); i++)
            {
                JSONObject recipeObject = itemList.getJSONObject(i);
                Item item = new Item();
                item.fromJSON(recipeObject);
                itemModels.add(item);
            }

            if (itemAdapter==null)
            {
                itemAdapter = new ShoppingListItemAdapter(this, itemModels);
                itemAdapter.setCallback(this);
                listView.setAdapter(itemAdapter);
            }
            else
            {
                itemAdapter.notifyDataSetChanged();
            }

            ProgressBar pbItems = findViewById(R.id.pb_shopping_list_items);
            pbItems.setVisibility(View.GONE);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editItem(int position) {
        if (itemModels!=null)
        {
            if (itemModels.size() > 0)
            {
                Item item = itemModels.get(position);
                Intent myIntent = new Intent().setClass(this, ModifyItem.class);
                myIntent.putExtra("id_item", item.getId_item());
                myIntent.putExtra("id_shoppingList", id_shoppingList);
                startActivity(myIntent);
            }
        }
    }

    public void goNewShoppingList()
    {
        Intent myIntent = new Intent().setClass(this, NewItem.class);
        myIntent.putExtra("id_shoppingList", id_shoppingList);
        startActivity(myIntent);
    }

    @Override
    public void deleteItem(int position) {
        deleteItemFromList(position);
    }

    private void deleteItemFromList(int position)
    {
        if(itemModels!=null){
            if(itemModels.size()>position) {
                Item item = itemModels.get(position);
                if (isNetworkAvailable()) {
                    ProgressBar pbItems = (ProgressBar) findViewById(R.id.pb_shopping_list_items);
                    pbItems.setVisibility(View.VISIBLE);
                    Resources res = getResources();
                    String url = res.getString(R.string.main_url) + "itemapi/deleteItem" + item.getId_item();
                    deleteTask(url);
                }
                else{
                    showError("error.IOException");
                }
            }
            else{
                showError("error.Unknown");
            }
        }
        else{
            showError("error.Unknown");
        }
    }

    private void deleteTask(String url){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOperator= ApiOperator.getInstance();
                String result = apiOperator.deleteTask(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(result.equalsIgnoreCase("error.IOException")||
                                result.equals("error.OKHttp")) {
                            showError(result);
                        }
                        else if(result.equalsIgnoreCase("null")){
                            showError("error.Unknown");
                        }
                        else{
                            ProgressBar pbItems = (ProgressBar) findViewById(R.id.pb_shopping_list_items);
                            pbItems.setVisibility(View.GONE);
                            setUpItems();
                        }
                    }
                });
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) {
                return false;
            } else {
                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                return (actNw != null) && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    private void showError(String error) {
        String message;
        Resources res = getResources();
        int duration;
        if (error.equals("error.IOException")||error.equals("error.OKHttp")) {
            message = res.getString(R.string.error_connection);
            duration = Toast.LENGTH_SHORT;
        }
        else if(error.equals("error.undelivered")){
            message = res.getString(R.string.error_undelivered);
            duration = Toast.LENGTH_LONG;
        }
        else {
            message = res.getString(R.string.error_unknown);
            duration = Toast.LENGTH_SHORT;
        }
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }
}