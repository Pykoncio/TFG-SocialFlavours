package com.iesvegademijas.socialflavours.presentation.home;


import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.ParseException;
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
import com.iesvegademijas.socialflavours.data.model.UserModel;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;
import com.iesvegademijas.socialflavours.presentation.login.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Launch the fragments
    private DrawerLayout drawerLayout;

    // List of the recipes of the user
    // private ArrayList<Recipe> recipes;
    // private RecipesAdapter recipesAdapter;

    private User user;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // To write into our shared preferences
        // SharedPreferences.Editor editor = sharedPref.edit();
        // editor.putString("username", String of the username);
        // editor.putString("password", String containing the password);
        // editor.commit();

        sharedPref = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        // Use these credentials to retrieve the user, if its null, then the user will be redirected to the login form
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");

        getUser(username, password);

        if (user != null)
        {
            //loadRecipes();
        }
        else // Send the user to the login form
        {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }


        // To write into our shared preferences
        // SharedPreferences.Editor editor = sharedPref.edit();
        // editor.putString("username", String of the username);
        // editor.putString("password", String containing the password);
        // editor.commit();

    }




    //region fragment navegation
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
    //endregion

    //region server request

    private User getUser(String username, String password) {
        if (isNetworkAvailable())
        {
            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_home);
            pbMain.setVisibility(View.VISIBLE);
            Resources res = getResources();

            String url = res.getString(R.string.main_url) + "/userLogin";
            getTaskList(url);
        }
        else {
            showError("error.IOException");
        }

        return user;
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
                            showError("error.desconocido");
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
            User user = new User();
            user.fromJSON(userData);

            

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