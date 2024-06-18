package com.iesvegademijas.socialflavours.presentation.home.fragments.shopping_list;

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

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewShoppingList extends AppCompatActivity {

    private long id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_shopping_list);

        Intent intent = getIntent();
        id_user = intent.getLongExtra("id_user", -1);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //region Create Shopping List
    public void createShoppingList(View view)
    {
        EditText etName = findViewById(R.id.new_shopping_list_name);
        String name = etName.getText().toString();

        if (!name.isEmpty())
        {
            Button btCreate = findViewById(R.id.new_shopping_list_button);
            ProgressBar pbCreate = findViewById(R.id.pb_new_shopping_list);
            btCreate.setEnabled(false);
            btCreate.setClickable(false);
            pbCreate.setVisibility(View.VISIBLE);
            if (isNetworkAvailable()) {
                String url = getResources().getString(R.string.main_url) + "shoppinglistapi/createShoppingList";
                sendTask(url, name);
            } else {
                showError("error.IOException");
            }
        }
        else
        {
            etName.setError(getResources().getString(R.string.compulsory_field));
        }
    }

    private void sendTask(String url, String name) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOperator= ApiOperator.getInstance();
                HashMap<String, Object> params = new HashMap<>();
                params.put("id_user", id_user);
                params.put("name", name);
                String result = apiOperator.postText(url,params);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Button btCreate= findViewById(R.id.new_shopping_list_button);
                        ProgressBar pbCreate=(ProgressBar) findViewById(R.id.pb_new_shopping_list);
                        pbCreate.setVisibility(View.GONE);
                        btCreate.setEnabled(true);
                        btCreate.setClickable(true);
                        long idCreated;
                        try{
                            idCreated=Long.parseLong(result);
                        }catch(NumberFormatException ex){
                            idCreated=-1;
                        }
                        if(idCreated>0){
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