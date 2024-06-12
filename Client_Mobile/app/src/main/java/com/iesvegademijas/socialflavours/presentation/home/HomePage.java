package com.iesvegademijas.socialflavours.presentation.home;


import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.adapter.RecipeAdapter;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.ShoppingList;
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;
import com.iesvegademijas.socialflavours.presentation.home.fragments.inbox.IncomingFriendshipRequests;
import com.iesvegademijas.socialflavours.presentation.home.fragments.meal_planner.MealPlanner;
import com.iesvegademijas.socialflavours.presentation.home.fragments.outbox.SendFriendshipRequest;
import com.iesvegademijas.socialflavours.presentation.home.fragments.recipe.CreateRecipe;
import com.iesvegademijas.socialflavours.presentation.home.fragments.recipe.FriendsRecipes;
import com.iesvegademijas.socialflavours.presentation.home.fragments.shopping_list.ShoppingLists;
import com.iesvegademijas.socialflavours.presentation.login.Login;
import com.iesvegademijas.socialflavours.presentation.modify_recipe.ModifyRecipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecipeAdapter.RecipesAdapterCallBack {

    // Launch the fragments
    public DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            back();
        }
    };

    // All the objects related to the recipes
    private ListView listView;
    private ArrayList<Recipe> recipeModels;
    private RecipeAdapter recipesAdapter;

    // We create the instance of the login activity, either to retrieve user data or to send the user to log in
    Intent loginIntent;
    private User user;

    // Tries to make the connection
    private static final int MAX_RETRIES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        listView = findViewById(R.id.recipeList);

        // Fragment Navigation
        drawerLayout = findViewById(R.id.drawerLayout);
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.closed);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set hamburger icon for the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_hamburguer_24);
        }

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!isUserLoggedIn())
        {
            loginIntent = new Intent(this, Login.class);
            startActivity(loginIntent);
            finish();
        }
        else
        {
            setUpRecipes();
        }
    }

    private boolean isUserLoggedIn()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MyUserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        Long idUser = sharedPreferences.getLong("id_user", -1);

        user = new User();
        if (username.isEmpty() || password.isEmpty())
        {
            user.setUsername(username);
            user.setPassword(password);
            user.setId_user(idUser);
            return false;
        }
        else
        {
            user.setUsername(username);
            user.setPassword(password);
            user.setId_user(idUser);
            return true;
        }
    }



    //region load the recipes
    private void setUpRecipes()
    {
        String url = getResources().getString(R.string.main_url) + "recipeapi/getAllRecipesFromUser" + user.getId_user();
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
            JSONArray recipeList = new JSONArray(result);

            if (recipeModels == null)
            {
                recipeModels = new ArrayList<>();
            }
            else
            {
                recipeModels.clear();
            }

            for (int i = 0; i < recipeList.length(); i++)
            {
                JSONObject recipeObject = recipeList.getJSONObject(i);
                Recipe recipe = new Recipe();
                recipe.fromJSON(recipeObject);
                recipeModels.add(recipe);
            }

            if (recipesAdapter==null)
            {
                recipesAdapter = new RecipeAdapter(this, recipeModels);
                recipesAdapter.setCallback(this);
                listView.setAdapter(recipesAdapter);
            }
            else
            {
                recipesAdapter.notifyDataSetChanged();
            }

            ProgressBar pbMain = findViewById(R.id.pb_home);
            pbMain.setVisibility(View.GONE);

            TextView tvEmptyList = findViewById(R.id.tv_empty_list_home);

            if (recipeModels.isEmpty())
            {
                tvEmptyList.setVisibility(View.VISIBLE);
            }
            else
            {
                tvEmptyList.setVisibility(View.GONE);
            }

        }
        catch (JSONException | ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editPressed(int position) {
        if (recipeModels!=null)
        {
            if (recipeModels.size() > position)
            {
                Recipe recipe = recipeModels.get(position);
                Intent myIntent = new Intent().setClass(this, ModifyRecipe.class);
                myIntent.putExtra("id_recipe", recipe.getId_recipe());
                startActivity(myIntent);
            }
        }
    }

    @Override
    public void deletePressed(int position) {
        androidx.appcompat.app.AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    private androidx.appcompat.app.AlertDialog AskOption(final int position)
    {
        androidx.appcompat.app.AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle(R.string.deleteRecipe)
                .setMessage(R.string.deleteRecipeMessage)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteRecipe(position);
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

    private void deleteRecipe(int position){
        if(recipeModels!=null){
            if(recipeModels.size()>position) {
                Recipe recipe = recipeModels.get(position);
                if (isNetworkAvailable()) {
                    ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_home);
                    pbMain.setVisibility(View.VISIBLE);
                    Resources res = getResources();
                    String url = res.getString(R.string.main_url) + "recipeapi/deleteRecipe" + recipe.getId_recipe();
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
                            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_home);
                            pbMain.setVisibility(View.GONE);
                            setUpRecipes();
                        }
                    }
                });
            }
        });
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

        if (idMenu == R.id.myRecipes)
        {
            return 1;
        }
        else if (idMenu == R.id.MyFriendsRecipes)
        {
            return 2;
        }
        else if (idMenu == R.id.incomingFriendshipRequests)
        {
            return 3;
        }
        else if (idMenu == R.id.sendFriendRequest) {
            return 4;
        }
        else if (idMenu == R.id.createRecipe) {
            return 5;
        }
        else if (idMenu == R.id.mealPlanner){
            return 6;
        }
        else if (idMenu == R.id.shoppingLists){
            return 7;
        }
        //if (idMenu == R.id.logOut)
        else {
            return 8;
        }
    }

    private void showFragment(int fragmentId)
    {
        Fragment fragment = null;
        String title = "";

        switch (fragmentId) // All the fragments will have the id of the user in the param1
        {
            case 1:
                Intent intent = new Intent(this, HomePage.class);
                startActivity(intent);
                return;
            case 2:
                fragment = FriendsRecipes.newInstance(String.valueOf(user.getId_user()), "");
                title = "My Friends Recipes";
                break;
            case 3:
                fragment = IncomingFriendshipRequests.newInstance(String.valueOf(user.getId_user()), "");
                title = "Incoming Friendships Requests";
                break;
            case 4:
                fragment = SendFriendshipRequest.newInstance(String.valueOf(user.getId_user()), "");
                title = "Outgoing Friendships Requests";
                break;
            case 5:
                fragment = CreateRecipe.newInstance(String.valueOf(user.getId_user()), "");
                title = "Create a new Recipe";
                break;
            case 6:
                fragment = MealPlanner.newInstance(String.valueOf(user.getId_user()), "");
                title = "Meal Planner";
                break;
            case 7:
                fragment = ShoppingLists.newInstance(String.valueOf(user.getId_user()), "");
                title = "Shopping Lists";
                break;
            case 8:
                logOut();
                return;
        }

        if (fragment!=null)
        {
            Log.d("HomePage", "Replacing fragment with ID: " + fragmentId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_content, fragment)
                    .commit();

            setTitle(title);
        }
        else
        {
            Log.d("HomePage", "Fragment is null for ID: " + fragmentId);
        }
    }


    private void logOut() {
        // Clear shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyUserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Open the log in activity
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // finish();
    }

    //endregion

    //region server request

    private void getUser(String username, String password) {
        ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_home);
        pbMain.setVisibility(View.VISIBLE);
        Resources res = getResources();
        String url = res.getString(R.string.main_url) + "userapi/userLogin";
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