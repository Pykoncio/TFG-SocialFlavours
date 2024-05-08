package com.iesvegademijas.socialflavours.presentation.register;

import androidx.appcompat.app.AppCompatActivity;

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

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void returnToLoginScreen()
    {
        finish();
    }

    public void register()
    {
        EditText editTextUsername = findViewById(R.id.register_user_input);
        EditText editTextEmail = findViewById(R.id.register_email_input);
        EditText editTextPassword = findViewById(R.id.register_password_input);

        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        Resources res = getResources();
        boolean correctCredentialsFormat = true;

        if (username.isEmpty())
        {
            editTextUsername.setError(res.getString(R.string.compulsory_field));
            correctCredentialsFormat = false;
        }
        if (email.isEmpty())
        {
            editTextEmail.setError(res.getString(R.string.compulsory_field));
            correctCredentialsFormat = false;
        }
        if (password.isEmpty())
        {
            editTextPassword.setError(res.getString(R.string.compulsory_field));
            correctCredentialsFormat = false;
        }

        if (correctCredentialsFormat)
        {
            Button btLogin = findViewById(R.id.register_button);
            ProgressBar pbLogin = findViewById(R.id.pb_register);

            btLogin.setEnabled(false);
            pbLogin.setVisibility(View.VISIBLE);

            if (isNetworkAvailable())
            {
                String url = R.string.main_url + "/userapi/register";
                sendTask(url, username, email, password);
            }
            else {
                showError("error.IOException");
            }
        }
    }

    //region server request
    private void sendTask(String url, String username, String email,String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOperator= ApiOperator.getInstance();
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                String result = apiOperator.postText(url,params);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Button btLogin = findViewById(R.id.login_button);
                        ProgressBar pbLogin = findViewById(R.id.pb_login);
                        pbLogin.setVisibility(View.GONE);
                        btLogin.setEnabled(true);
                        btLogin.setClickable(true);
                        long idCreated;
                        try{
                            idCreated=Long.parseLong(result);
                        }catch(NumberFormatException ex){
                            idCreated=-1;
                        }
                        if(idCreated>0){
                            final Intent data = new Intent();
                            data.putExtra("username", username);
                            data.putExtra("password", password);
                            setResult(RESULT_OK, data);
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

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
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