package com.iesvegademijas.socialflavours.presentation.home;


import static android.app.PendingIntent.getActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.dto.User;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Launch the fragments
    private DrawerLayout drawerLayout;

    // List of the recipes of the user
    // private ArrayList<Recipe> recipes;
    // private RecipesAdapter recipesAdapter;

    SharedPreferences sharedPref;

    ActivityResultLauncher<Intent> newResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        sharedPref = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

                        // Use these credentials to retrieve the user, if its null, then the user will be redirected to the login form
                        String username = sharedPref.getString("username", "");
                        String password = sharedPref.getString("password", "");

                        //loadRecipes();

                        // To write into our shared preferences
                        // SharedPreferences.Editor editor = sharedPref.edit();
                        // editor.putString("username", String of the username);
                        // editor.putString("password", String containing the password);
                        // editor.commit();
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        sharedPref = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        // Use these credentials to retrieve the user, if its null, then the user will be redirected to the login form
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");

        User user = getUser(username, password);

        //loadRecipes();

        // To write into our shared preferences
        // SharedPreferences.Editor editor = sharedPref.edit();
        // editor.putString("username", String of the username);
        // editor.putString("password", String containing the password);
        // editor.commit();

    }

    private User getUser(String username, String password) {
        if (isNetworkAvailable())
        {

        }
        else {
            showError("error.IOException");
        }

        return user;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int titleId = getTitleId(menuItem);
        showFragment(titleId);
    }

    private int getTitleId(@NonNull MenuItem menuItem) {
        int idMenu = menuItem.getItemId();

        switch (idMenu)
        {

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
}