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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModifyItem extends AppCompatActivity {

    private long idItem;
    private long idShoppingList;
    private static final int MAX_RETRIES = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_item);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id_item") && intent.hasExtra("id_shoppingList"))
        {
            idItem = intent.getLongExtra("id_item", -1);
            idShoppingList = intent.getLongExtra("id_shoppingList", -1);
        }

        loadItem();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //region Retrieve Item
    private void loadItem()
    {
        ProgressBar pbEdit = (ProgressBar) findViewById(R.id.pb_modify_item);
        pbEdit.setVisibility(View.VISIBLE);
        String url = getResources().getString(R.string.main_url) + "itemapi/getItem" + idItem;
        if (isNetworkAvailable()) {
            for (int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
                try {
                    getItem(url);
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

    private void getItem(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOp= ApiOperator.getInstance();
                String result = apiOp.getString(url);
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
                            fillInformation(result);
                        }
                    }
                });
            }
        });
    }

    private void fillInformation(String result)
    {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Item item = new Item();
            item.fromJSON(jsonObject);

            EditText etName = findViewById(R.id.modify_item_name);
            EditText etQuantity = findViewById(R.id.modify_item_quantity);

            etName.setText(item.getName());
            etQuantity.setText(String.valueOf(item.getQuantity()));

            ProgressBar pbEdit = (ProgressBar) findViewById(R.id.pb_modify_item);
            pbEdit.setVisibility(View.GONE);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region Modify Item
    public void modifyItem(View view){
        boolean continueToModify = true;

        EditText etName = findViewById(R.id.modify_item_name);
        EditText etQuantity = findViewById(R.id.modify_item_quantity);

        String name = etName.getText().toString();
        if(name.isEmpty())
        {
            continueToModify = false;
            etName.setError(getResources().getString(R.string.compulsory_field));
        }
        String quantity = etQuantity.getText().toString();
        if (quantity.isEmpty())
        {
            continueToModify = false;
            etQuantity.setError(getResources().getString(R.string.compulsory_field));
        }

        if (continueToModify){
            Button btEdit= findViewById(R.id.modify_item_button_add);
            ProgressBar pbEdit= findViewById(R.id.pb_modify_item);
            btEdit.setEnabled(false);
            btEdit.setClickable(false);
            pbEdit.setVisibility(View.VISIBLE);
            if (isNetworkAvailable()) {
                String url = getResources().getString(R.string.main_url) + "itemapi/itemUpdate";
                sendTask(url, idItem+"",name, quantity, idShoppingList+"");
            } else {
                showError("error.IOException");
            }
        }
    }

    private void sendTask(String url, String idItem,String name, String quantity, String idShoppingList) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOperator=ApiOperator.getInstance();
                HashMap<String, Object> params = new HashMap<>();
                params.put("id_item", idItem);
                params.put("name", name);
                params.put("quantity", quantity);
                params.put("id_shoppingList", idShoppingList);
                String result = apiOperator.putText(url,params);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Button btSave= findViewById(R.id.modify_item_button_add);
                        ProgressBar pbAccept=(ProgressBar) findViewById(R.id.pb_modify_item);
                        pbAccept.setVisibility(View.GONE);
                        btSave.setEnabled(true);
                        btSave.setClickable(true);
                        long createdId;
                        try{
                            createdId=Long.parseLong(result);
                        }catch(NumberFormatException ex){
                            createdId=-1;
                        }
                        if(createdId>0){
                            setResult(RESULT_OK);
                            finish();
                        }
                        else {
                            showError("error.Unknown");
                        }
                    }
                });
            }
        });
    }
    //endregion

    //region Network Utils
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
    //endregion
}