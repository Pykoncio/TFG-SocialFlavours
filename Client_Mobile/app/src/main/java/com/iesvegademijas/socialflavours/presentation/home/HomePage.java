package com.iesvegademijas.socialflavours.presentation.home;


import static android.app.PendingIntent.getActivity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;
import com.iesvegademijas.socialflavours.presentation.login.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Launch the fragments
    private DrawerLayout drawerLayout;

    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            back();
        }
    };

    // All the objects related to the recipes

    // We create the instance of the login activity, either to retrieve user data or to send the user to log in
    final Intent loginIntent = new Intent(this, Login.class);
    private User user;
    private SharedPreferences sharedPref;

    // Tries to make the connection
    private static final int MAX_RETRIES = 5;

    ActivityResultLauncher<Intent> newResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        retrieveUser();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        user = new User();
        retrieveUser();
    }

    private void retrieveUser()
    {
        sharedPref = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        // If we have logged or registered successfully, we write in our shared preferences
        user.setUsername(loginIntent.getStringExtra("username"));
        user.setPassword(loginIntent.getStringExtra("password"));

        if(user.getUsername() == null || user.getPassword() == null)
        {
            // Use these shared preferences to retrieve the user, if its null, then the user will be redirected to the login form
            user.setUsername(sharedPref.getString("username", ""));
            user.setUsername(sharedPref.getString("password", ""));

            getUser(user.getUsername(), user.getPassword());

            if (user.getUsername() == "" || user.getPassword() == "")
            {
                newResultLauncher.launch(loginIntent);
            }
            else
            {
                loadRecipes(user.getId_user());
            }
        }
        else // We write the new Login values into the shared preferences
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", user.getUsername());
            editor.putString("password", user.getPassword());
            editor.commit();
        }


    }

    //region load the recipes
    private void loadRecipes(long idUser) {
        // Just a placeholder
        ProgressBar pbHome = findViewById(R.id.pb_home);
        pbHome.setVisibility(View.GONE);
    }
    //endregion

    //region fragment navigation

    private void back()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            finish();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int titleId = getTitleId(menuItem);
        showFragment(titleId);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getTitleId(@NonNull MenuItem menuItem) {
        int idMenu = menuItem.getItemId();
        Resources res = getResources();

        if (idMenu == R.id.nav_ubicacion)
        {
            return 1;
        }
        else if (idMenu == R.id.nav_camara)
        {
            return 2;
        }
        else
        {
            return 3;
        }
    }

    private void showFragment(int fragmentId)
    {
        Fragment fragment;
        String title;

        switch (fragmentId)
        {
            case 1:
                fragment = Fragment.newInstance("", "");
                title = "Test";
                break;
            case 2:
                break;
            case 3:
                break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_content, fragment)
                .commit();

        setTitle(title);
    }

    //endregion

    //region server request

    private void getUser(String username, String password) {
        ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_home);
        pbMain.setVisibility(View.VISIBLE);
        Resources res = getResources();
        String url = res.getString(R.string.main_url) + "/userLogin";
        if (isNetworkAvailable())
        {
            for (int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
                try {
                    getTaskList(url);
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
        }
        else {
            showError("error.IOException");
        }
    }

    private void getTaskList(String url) {
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
                            // If we obtained the user we should read the JSON received from the server
                            getResultFromJSON(result);
                        }
                    }
                });
            }
        });
    }

    private void getResultFromJSON(String result)
    {
        try
        {
            JSONObject userData = new JSONObject(result);
            this.user = new User();
            user.fromJSON(userData);

            if (user.getId_user() != -1)
            {
                loadRecipes(user.getId_user());
            }
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