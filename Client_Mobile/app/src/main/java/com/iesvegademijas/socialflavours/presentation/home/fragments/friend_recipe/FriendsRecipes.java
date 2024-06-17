package com.iesvegademijas.socialflavours.presentation.home.fragments.friend_recipe;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.adapter.RecipeAdapter;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;
import com.iesvegademijas.socialflavours.presentation.home.HomePage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsRecipes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsRecipes extends Fragment implements RecipeAdapter.RecipesAdapterCallBack {

    private View myView;
    private static final int MAX_RETRIES = 5;
    private ListView listView;
    private ArrayList<Recipe> recipeModels;
    private RecipeAdapter recipesAdapter;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        setUpFriendsRecipes();
                    }
                }
            }
    );

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsRecipes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsRecipes.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsRecipes newInstance(String param1, String param2) {
        FriendsRecipes fragment = new FriendsRecipes();
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
        myView = inflater.inflate(R.layout.fragment_friends_recipes, container, false);

        Toolbar toolbar = myView.findViewById(R.id.toolbar_friends_recipes);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("Outgoing Friendships Requests");
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

        listView = myView.findViewById(R.id.friends_recipes_list);
        setUpFriendsRecipes();

        return myView;
    }

    //region Retrieve Friends Recipes
    private void setUpFriendsRecipes()
    {
        String url = getResources().getString(R.string.main_url) + "recipeapi/getAllRecipesFromUserFriends" + mParam1;
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
                recipesAdapter = new RecipeAdapter(getContext(), recipeModels);
                recipesAdapter.setCallback(this);
                listView.setAdapter(recipesAdapter);
            }
            else
            {
                recipesAdapter.notifyDataSetChanged();
            }

            TextView tvFriendsRecipes = myView.findViewById(R.id.tv_empty_list_friends_recipes);

            if (recipeModels.isEmpty())
            {
                tvFriendsRecipes.setVisibility(View.VISIBLE);
            }
            else
            {
                tvFriendsRecipes.setVisibility(View.GONE);
            }
        }
        catch (JSONException | ParseException e)
        {
            throw new RuntimeException(e);
        }
    }
    //endregion
    @Override
    public void editPressed(int position) {
        if (recipeModels!=null)
        {
            if (recipeModels.size() > position)
            {
                Recipe recipe = recipeModels.get(position);
                Intent myIntent = new Intent().setClass(getContext(), NotEditableRecipe.class);
                myIntent.putExtra("id_recipe", recipe.getId_recipe());
                activityResultLauncher.launch(myIntent);
            }
        }
    }

    @Override
    public void deletePressed(int position) {}

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