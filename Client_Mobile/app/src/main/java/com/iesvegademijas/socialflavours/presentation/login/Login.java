package com.iesvegademijas.socialflavours.presentation.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;
import com.iesvegademijas.socialflavours.presentation.accrecovery.Recovery;
import com.iesvegademijas.socialflavours.presentation.home.HomePage;
import com.iesvegademijas.socialflavours.presentation.register.Register;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private long idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isUserLoggedIn())
        {
            Intent intent = new Intent().setClass(this, HomePage.class);
            startActivity(intent);
            finish();
        }

    }

    private boolean isUserLoggedIn()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MyUserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        if (username.isEmpty() || password.isEmpty())
        {
            return false;
        }
        else {return true;}
    }
    public void goToRegisterPage(View view)
    {
        Intent registerIntent = new Intent(this, Register.class);
        startActivity(registerIntent);
        finish();
    }

    public void goToRecoverPasswordActivity(View view)
    {
        Intent recoveryIntent = new Intent(this, Recovery.class);
        startActivity(recoveryIntent);
        finish();
    }

    public void login(View view) {
        EditText editTextUsername = findViewById(R.id.login_username_input);
        EditText editTextPassword = findViewById(R.id.login_password_input);

        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        Resources res = getResources();
        boolean correctCredentialsFormat = true;

        if (username.isEmpty())
        {
            editTextUsername.setError(res.getString(R.string.compulsory_field));
            correctCredentialsFormat = false;
        }
        if (password.isEmpty())
        {
            editTextPassword.setError(res.getString(R.string.compulsory_field));
            correctCredentialsFormat = false;
        }

        if (correctCredentialsFormat)
        {
            Button btLogin = findViewById(R.id.login_button);
            ProgressBar pbLogin = findViewById(R.id.pb_login);

            btLogin.setEnabled(false);
            btLogin.setClickable(false);
            pbLogin.setVisibility(View.VISIBLE);

            if (isNetworkAvailable())
            {
                String url = getResources().getString(R.string.main_url) + "userapi/userLogin/" + username + "_" + password;
                getListTask(url);
            }
            else {
                showError("error.IOException");
            }
        }
    }

    private void returnToHomeScreen(String username, String password) {

        if (!username.isEmpty() && !password.isEmpty())
        {
            SharedPreferences sharedPreferences = getSharedPreferences("MyUserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putLong("id_user", idUser);
            editor.apply();

            Button btLogin = findViewById(R.id.login_button);
            ProgressBar pbLogin = findViewById(R.id.pb_login);

            btLogin.setEnabled(true);
            btLogin.setClickable(true);
            pbLogin.setVisibility(View.GONE);

            Intent myIntent = new Intent().setClass(this, HomePage.class);
            startActivity(myIntent);
            finish();
        }

        Button btLogin = findViewById(R.id.login_button);
        ProgressBar pbLogin = findViewById(R.id.pb_login);

        btLogin.setEnabled(true);
        btLogin.setClickable(true);
        pbLogin.setVisibility(View.GONE);
    }

    //region server request
    private void getListTask(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiOperator apiOperator = ApiOperator.getInstance();
                    String result = apiOperator.okGetString(url);
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
                                getResultFromJSON(result);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showError(e.getMessage());
                            Button btLogin = findViewById(R.id.login_button);
                            ProgressBar pbLogin = findViewById(R.id.pb_login);

                            btLogin.setEnabled(true);
                            btLogin.setClickable(true);
                            pbLogin.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }

    private void getResultFromJSON(String result)
    {
        try
        {
            JSONObject userData = new JSONObject(result);
            User user = new User();
            user.fromJSON(userData);

            idUser = user.getId_user();

            returnToHomeScreen(user.getUsername(), user.getPassword());
        }
        catch (JSONException | java.text.ParseException e)
        {
            showError(e.getMessage());
        }
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