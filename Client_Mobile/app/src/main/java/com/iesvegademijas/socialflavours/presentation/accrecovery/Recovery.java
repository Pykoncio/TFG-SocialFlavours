package com.iesvegademijas.socialflavours.presentation.accrecovery;

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
import com.iesvegademijas.socialflavours.presentation.login.Login;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Recovery extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recovery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void rememberPassword(View view)
    {
        EditText etUsername = findViewById(R.id.recovery_username_input);
        String username = etUsername.getText().toString();

        if (username.isEmpty())
        {
            etUsername.setError(getResources().getString(R.string.compulsory_field));
        }
        else
        {
            Button recoveryButton = findViewById(R.id.recovery_button);
            ProgressBar pbRecovery = (ProgressBar) findViewById(R.id.pb_recovery);

            recoveryButton.setClickable(false);
            recoveryButton.setEnabled(false);

            pbRecovery.setVisibility(View.VISIBLE);

            if (isNetworkAvailable())
            {
                String url = getResources().getString(R.string.main_url) + "mailapi/send/" + username;
                getListTask(url);
            }
            else {
                etUsername.setError(getResources().getString(R.string.compulsory_field));
            }
        }
    }

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
                            Button recoveryButton = findViewById(R.id.recovery_button);
                            ProgressBar pbRecovery = (ProgressBar) findViewById(R.id.pb_recovery);

                            recoveryButton.setClickable(true);
                            recoveryButton.setEnabled(true);

                            pbRecovery.setVisibility(View.GONE);

                            if(result.equalsIgnoreCase("error.IOException")||
                                    result.equals("error.OKHttp")) {
                                showError(result);
                            }
                            else if(result.equalsIgnoreCase("null")){
                                showError("error.Unknown");
                            } else{
                                showError(getResources().getString(R.string.emailSent));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showError(e.getMessage());

                            Button recoveryButton = findViewById(R.id.recovery_button);
                            ProgressBar pbRecovery = (ProgressBar) findViewById(R.id.pb_recovery);

                            recoveryButton.setClickable(true);
                            recoveryButton.setEnabled(true);

                            pbRecovery.setVisibility(View.GONE);
                        }
                    });
                }

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
        else if(error.equals(R.string.emailSent)){
            message = res.getString(R.string.emailSent);
            duration = Toast.LENGTH_LONG;
        }
        else {
            message = res.getString(R.string.error_unknown);
            duration = Toast.LENGTH_SHORT;
        }
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }

    public void returnToLoginScreenFromRecovery(View view)
    {
        Intent intent = new Intent().setClass(this, Login.class);
        startActivity(intent);
        finish();
    }
}