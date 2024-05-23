package com.iesvegademijas.socialflavours.presentation.home.fragments.recipe;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotEditableRecipe extends AppCompatActivity {

    private static final int MAX_RETRIES = 5;
    private long idRecipe;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("id_recipe", idRecipe);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_not_editable_recipe);

        if(savedInstanceState!=null){
            idRecipe = savedInstanceState.getLong("id_recipe",-1);
        }
        else{
            Intent intent = getIntent();
            idRecipe = intent.getLongExtra("id_recipe", -1);
        }

        loadRecipe();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadRecipe(){
        ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_modify_recipe);
        pbMain.setVisibility(View.VISIBLE);
        String url = R.string.main_url + "recipeapi/getRecipe" + idRecipe;
        if (isNetworkAvailable()) {
            for (int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
                try {
                    getRecipe(url);
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

    private void getRecipe(String url) {
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
                            fillInformation(result);
                        }
                    }
                });
            }
        });
    }

    private void fillInformation(String result){
        try {
            JSONObject jsonShop=new JSONObject(result);
            Recipe recipe = new Recipe();
            recipe.fromJSON(jsonShop);

            EditText etTitle = findViewById(R.id.modify_recipe_title);
            EditText etDescription = findViewById(R.id.modify_recipe_description);
            EditText etPreparationTime = findViewById(R.id.modify_recipe_et_preparationTime);

            Spinner sRating = findViewById(R.id.modify_recipe_ratingSpinner);
            Spinner sTag = findViewById(R.id.modify_recipe_tagSpinner);

            ImageView imageView = findViewById(R.id.modify_recipe_image);

            LinearLayout layoutIngredients = findViewById(R.id.modify_ingredientList);
            ImageButton addIngredientButton = findViewById(R.id.modify_recipe_add_ingredient_button);

            LinearLayout layoutSteps = findViewById(R.id.modify_stepList);
            ImageButton addStepButton = findViewById(R.id.modify_recipe_add_step_button);

            // Load data
            if (recipe.getImagePath() == "")
            {
                Picasso.get().load(recipe.getImagePath()).into(imageView);
            }
            else
            {
                Picasso.get().load(R.drawable.default_recipe_image).into(imageView);
            }

            etTitle.setText(recipe.getName());
            etDescription.setText(recipe.getDescription());
            etPreparationTime.setText(recipe.getPreparationTime());

            ArrayList<String> ratings = new ArrayList<>();

            ratings.add(getResources().getString(R.string.oneStar));
            ratings.add(getResources().getString(R.string.twoStars));
            ratings.add(getResources().getString(R.string.threeStars));
            ratings.add(getResources().getString(R.string.fourStars));
            ratings.add(getResources().getString(R.string.fiveStars));

            switch (recipe.getRating())
            {
                case "Two Stars":
                    sRating.setSelection(1);
                    break;
                case "Three Stars":
                    sRating.setSelection(2);
                    break;
                case "Four Stars":
                    sRating.setSelection(3);
                    break;
                case "Five Stars":
                    sRating.setSelection(4);
                    break;
                default:
                    sRating.setSelection(0);
                    break;
            }

            ArrayAdapter<String> adRating = new ArrayAdapter<>(this ,android.R.layout.simple_spinner_item,
                    ratings);

            adRating.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sRating.setAdapter(adRating);


            ArrayList<String> tags = new ArrayList<>();

            tags.add(getResources().getString(R.string.breakfast));
            tags.add(getResources().getString(R.string.lunch));
            tags.add(getResources().getString(R.string.dinner));
            tags.add(getResources().getString(R.string.snack));

            switch (recipe.getTag())
            {
                case "Launch":
                    sRating.setSelection(1);
                    break;
                case "Dinner":
                    sRating.setSelection(2);
                    break;
                case "Snack":
                    sRating.setSelection(3);
                    break;
                default:
                    sRating.setSelection(0);
                    break;
            }

            ArrayAdapter<String> adTag = new ArrayAdapter<>(this ,android.R.layout.simple_spinner_item,
                    tags);

            adTag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sTag.setAdapter(adTag);

            addIngredientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout horizontalLayout = new LinearLayout(getBaseContext());
                    horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    horizontalLayout.setLayoutParams(layoutParams);

                    for (int i = 0; i < recipe.getIngredients().size(); i++) {
                        EditText editText = new EditText(getBaseContext());
                        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        );
                        editText.setLayoutParams(editTextParams);
                        editText.setHint(recipe.getIngredients().get(i).getName());
                        editText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                        editText.setTextColor(Color.parseColor("#808080"));
                        editText.setPadding(5, 5, 5, 5);

                        ImageButton deleteButton = new ImageButton(getBaseContext());
                        LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        deleteButton.setLayoutParams(deleteButtonParams);
                        deleteButton.setImageResource(R.drawable.eliminate_blue);
                        deleteButton.setBackgroundColor(Color.parseColor("#354F52"));
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutIngredients.removeView(horizontalLayout);
                            }
                        });

                        horizontalLayout.addView(editText);
                        horizontalLayout.addView(deleteButton);
                    }

                    layoutIngredients.addView(horizontalLayout);
                }
            });

            addStepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout horizontalLayout2 = new LinearLayout(getBaseContext());
                    horizontalLayout2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    horizontalLayout2.setLayoutParams(layoutParams2);

                    for (int i = 0; i < recipe.getSteps().size(); i++) {
                        EditText editText = new EditText(getBaseContext());
                        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        );
                        editText.setLayoutParams(editTextParams);
                        editText.setHint(recipe.getIngredients().get(i).getName());
                        editText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                        editText.setTextColor(Color.parseColor("#808080"));
                        editText.setPadding(5, 5, 5, 5);

                        ImageButton deleteButton = new ImageButton(getBaseContext());
                        LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        deleteButton.setLayoutParams(deleteButtonParams);
                        deleteButton.setImageResource(R.drawable.eliminate_blue);
                        deleteButton.setBackgroundColor(Color.parseColor("#354F52"));
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutSteps.removeView(horizontalLayout2);
                            }
                        });

                        horizontalLayout2.addView(editText);
                        horizontalLayout2.addView(deleteButton);
                    }

                    layoutSteps.addView(horizontalLayout2);

                }
            });

            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_modify_recipe);
            pbMain.setVisibility(View.GONE);
        } catch (JSONException | ParseException e) {
            showError("error.json");
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

    public void Accept(){
        finish();
    }
}