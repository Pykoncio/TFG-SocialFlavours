package com.iesvegademijas.socialflavours.presentation.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;
import com.iesvegademijas.socialflavours.presentation.register.Register;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    final Intent registerIntent = new Intent(this, Register.class);

    ActivityResultLauncher<Intent> newResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        EditText editTextUsername = findViewById(R.id.login_username_input);
                        EditText editTextPassword = findViewById(R.id.login_password_input);

                        editTextUsername.setText("");
                        editTextPassword.setText("");

                        String username = registerIntent.getStringExtra("username");
                        String password = registerIntent.getStringExtra("password");

                        if (username != null && password != null)
                        {
                            returnToHomeScreen(username, password);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void goToRegisterPage()
    {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
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
            pbLogin.setVisibility(View.VISIBLE);

            if (isNetworkAvailable())
            {
                String url = R.string.main_url + "/userapi/userLogin";
                getListaTask(url, username, password);
            }
            else {
                showError("error.IOException");
            }
        }
    }

    private void returnToHomeScreen(String username, String password) {
        final Intent data = new Intent();
        data.putExtra("username", username);
        data.putExtra("password", password);

        Button btLogin = findViewById(R.id.login_button);
        ProgressBar pbLogin = findViewById(R.id.pb_login);

        btLogin.setEnabled(true);
        pbLogin.setVisibility(View.GONE);

        setResult(RESULT_OK, data);
        finish();
    }

    //region server request
    private void getListaTask(String url, String username, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiOperator apiOperator = ApiOperator.getInstance();
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    String result = apiOperator.okGetString(url, params);
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