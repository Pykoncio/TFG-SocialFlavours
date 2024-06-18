package com.iesvegademijas.socialflavours.presentation.home.fragments.shopping_list;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.adapter.ShoppingListAdapter;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.ShoppingList;
import com.iesvegademijas.socialflavours.presentation.home.HomePage;
import com.iesvegademijas.socialflavours.presentation.home.fragments.shopping_list.item.ItemsList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingLists#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingLists extends Fragment implements ShoppingListAdapter.ShoppingListsAdapterCallBack {

    private View myView;
    private static final int MAX_RETRIES = 5;
    private ListView listView;
    private ArrayList<ShoppingList> shoppingListsModels;
    private ShoppingListAdapter shoppingListAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        setUpShoppingLists();
                    }
                }
            }
    );



    public ShoppingLists() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingLists.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingLists newInstance(String param1, String param2) {
        ShoppingLists fragment = new ShoppingLists();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_shopping_lists, container, false);

        listView = myView.findViewById(R.id.shopping_lists_shoppingList);

        Toolbar toolbar = myView.findViewById(R.id.toolbar_shoppingLists);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("Shopping Lists");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_hamburguer_24);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof HomePage) {
                    ((HomePage) getActivity()).drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        ImageButton fab = myView.findViewById(R.id.fab_add_shopping_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShoppingList(v);
            }
        });

        setUpShoppingLists();

        return myView;
    }
    private void createShoppingList(View view)
    {
        Intent myIntent = new Intent().setClass(view.getContext(), NewShoppingList.class);
        myIntent.putExtra("id_user", Long.parseLong(mParam1));
        activityResultLauncher.launch(myIntent);
    }

    @Override
    public void goToList(int position) {
        if (shoppingListsModels!=null)
        {
            if (shoppingListsModels.size() > position)
            {
                ShoppingList shoppingList = shoppingListsModels.get(position);
                Intent myIntent = new Intent().setClass(getContext(), ItemsList.class);
                myIntent.putExtra("id_shoppingList", shoppingList.getId_shoppingList());
                activityResultLauncher.launch(myIntent);
                setUpShoppingLists();
            }
        }
    }

    //region Set Up Shopping Lists
    private void setUpShoppingLists()
    {
        ProgressBar pbShoppingLists = (ProgressBar) myView.findViewById(R.id.pb_shopping_lists);
        pbShoppingLists.setVisibility(View.VISIBLE);
        String url = getResources().getString(R.string.main_url) + "shoppinglistapi/getAllShoppingListsFromUser" + mParam1;
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
            JSONArray shoppingListsList = new JSONArray(result);

            if (shoppingListsModels == null)
            {
                shoppingListsModels = new ArrayList<>();
            }
            else
            {
                shoppingListsModels.clear();
            }

            for (int i = 0; i < shoppingListsList.length(); i++)
            {
                JSONObject shoppingListObject = shoppingListsList.getJSONObject(i);
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.fromJSON(shoppingListObject);
                shoppingListsModels.add(shoppingList);
            }

            if (shoppingListAdapter==null)
            {
                shoppingListAdapter = new ShoppingListAdapter(getContext(), shoppingListsModels);
                shoppingListAdapter.setCallback(this);
                listView.setAdapter(shoppingListAdapter);
            }
            else
            {
                shoppingListAdapter.notifyDataSetChanged();
            }

            ProgressBar pbShoppingLists = myView.findViewById(R.id.pb_shopping_lists);
            pbShoppingLists.setVisibility(View.GONE);

            TextView tvEmptyShoppingList = myView.findViewById(R.id.tv_empty_list_shopping_lists);

            if (shoppingListsModels.isEmpty())
            {
                tvEmptyShoppingList.setVisibility(View.VISIBLE);
            }
            else
            {
                tvEmptyShoppingList.setVisibility(View.GONE);
            }
        }
        catch (JSONException | ParseException e)
        {
            throw new RuntimeException(e);
        }
    }
    //endregion

    //region Delete Shopping Lists
    @Override
    public void deleteShoppingList(int position) {
        androidx.appcompat.app.AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    private androidx.appcompat.app.AlertDialog AskOption(final int position)
    {
        androidx.appcompat.app.AlertDialog myQuittingDialogBox =new AlertDialog.Builder(myView.getContext())
                .setTitle(R.string.deleteShoppingList)
                .setMessage(R.string.deleteShoppingListMessage)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteSelectedShoppingList(position);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void deleteSelectedShoppingList(int position){
        if(shoppingListsModels!=null){
            if(shoppingListsModels.size()>position) {
                ShoppingList shoppingList = shoppingListsModels.get(position);
                if (isNetworkAvailable()) {
                    ProgressBar pbShoppingList = (ProgressBar) myView.findViewById(R.id.pb_shopping_lists);
                    pbShoppingList.setVisibility(View.VISIBLE);
                    Resources res = getResources();
                    String url = res.getString(R.string.main_url) + "shoppinglistapi/deleteShoppingList" + shoppingList.getId_shoppingList();
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
                            ProgressBar pbShoppingLists = (ProgressBar) myView.findViewById(R.id.pb_shopping_lists);
                            pbShoppingLists.setVisibility(View.GONE);
                            setUpShoppingLists();
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
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        Toast toast = Toast.makeText(getContext(), message, duration);
        toast.show();
    }
    //endregion
}