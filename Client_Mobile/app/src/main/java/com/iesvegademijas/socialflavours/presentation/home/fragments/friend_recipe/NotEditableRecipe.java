package com.iesvegademijas.socialflavours.presentation.home.fragments.friend_recipe;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Ingredient;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Step;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotEditableRecipe extends AppCompatActivity {

    private static final int MAX_RETRIES = 5;
    private long idRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_not_editable_recipe);

        Intent myIntent = getIntent();
        idRecipe = myIntent.getLongExtra("id_recipe", -1);

        loadRecipe();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //region Load Recipe and Fill Information
    private void loadRecipe(){
        ProgressBar pbNotEditable = (ProgressBar) findViewById(R.id.pb_not_editable_recipe);
        pbNotEditable.setVisibility(View.VISIBLE);
        Button back = findViewById(R.id.not_editable_recipe_button_back);
        back.setClickable(false);
        back.setEnabled(false);
        String url = getResources().getString(R.string.main_url) + "recipeapi/getRecipe" + idRecipe;
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
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        ProgressBar pbNotEditable = (ProgressBar) findViewById(R.id.pb_not_editable_recipe);
                        pbNotEditable.setVisibility(View.GONE);
                        Button back = findViewById(R.id.not_editable_recipe_button_back);
                        back.setClickable(true);
                        back.setEnabled(true);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fillInformation(String result){
        try {
            JSONObject jsonShop=new JSONObject(result);
            Recipe recipe = new Recipe();
            recipe.fromJSON(jsonShop);

            EditText etTitle = findViewById(R.id.not_editable_recipe_title);
            EditText etDescription = findViewById(R.id.not_editable_recipe_description);
            EditText etPreparationTime = findViewById(R.id.not_editable_recipe_et_preparationTime);

            Spinner sRating = findViewById(R.id.not_editable_recipe_ratingSpinner);
            Spinner sTag = findViewById(R.id.not_editable_recipe_tagSpinner);

            ImageView imageView = findViewById(R.id.not_editable_recipe_image);

            /* IMPORTANT: NOT IMPLEMENTED LOADING THE IMAGE WITH THE URL SINCE IT DOESN'T LOAD PROPERLY

            File file = new File(getFilesDir(), recipe.getImagePath() );
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
            */

            imageView.setImageResource(R.drawable.default_recipe_image);

            etTitle.setText(recipe.getName());
            etDescription.setText(recipe.getDescription());
            etPreparationTime.setText(String.valueOf(recipe.getPreparationTime()));

            ArrayList<String> ratings = new ArrayList<>();

            ratings.add(getResources().getString(R.string.oneStar));
            ratings.add(getResources().getString(R.string.twoStars));
            ratings.add(getResources().getString(R.string.threeStars));
            ratings.add(getResources().getString(R.string.fourStars));
            ratings.add(getResources().getString(R.string.fiveStars));

            ArrayAdapter<String> adRating = new ArrayAdapter<>(this ,android.R.layout.simple_spinner_item,
                    ratings);

            adRating.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sRating.setAdapter(adRating);

            switch (recipe.getRating())
            {
                case "One Star":
                    sRating.setSelection(0);
                    break;
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
            }

            sRating.setEnabled(false);
            sRating.setClickable(false);

            ArrayList<String> tags = new ArrayList<>();

            tags.add(getResources().getString(R.string.breakfast));
            tags.add(getResources().getString(R.string.lunch));
            tags.add(getResources().getString(R.string.dinner));
            tags.add(getResources().getString(R.string.snack));

            ArrayAdapter<String> adTag = new ArrayAdapter<>(this ,android.R.layout.simple_spinner_item,
                    tags);
            adTag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sTag.setAdapter(adTag);

            switch (recipe.getTag())
            {
                case "Breakfast":
                    sTag.setSelection(0);
                    break;
                case "Lunch":
                    sTag.setSelection(1);
                    break;
                case "Dinner":
                    sTag.setSelection(2);
                    break;
                case "Snack":
                    sTag.setSelection(3);
                    break;
            }

            sTag.setEnabled(false);
            sTag.setClickable(false);

            List<Ingredient> sortedIngredients = recipe.getIngredients();
            Collections.sort(sortedIngredients, Comparator.comparingLong(Ingredient::getId_ingredient));

            LinearLayout ingredientList = findViewById(R.id.not_editable_ingredientList);
            for (Ingredient ingredient : sortedIngredients) {
                addIngredientView(ingredientList, ingredient.getName());
            }

            List<Step> sortedSteps = recipe.getSteps();
            Collections.sort(sortedSteps, Comparator.comparingLong(Step::getId_step));

            LinearLayout stepList = findViewById(R.id.not_editable_stepList);
            for (Step step : sortedSteps) {
                addStepView(stepList, step.getStep());
            }

            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_not_editable_recipe);
            pbMain.setVisibility(View.GONE);
        } catch (JSONException | ParseException e) {
            showError("error.json");
        }
    }

    private void addIngredientView(LinearLayout ingredientList, String ingredientName) {
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);
        horizontalLayout.setLayoutParams(layoutParams);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        editText.setLayoutParams(editTextParams);
        editText.setText(ingredientName);
        editText.setHint("Ingredient");
        editText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
        editText.setTextColor(Color.parseColor("#808080"));
        editText.setPadding(5, 5, 5, 5);
        editText.setEnabled(false);
        editText.setClickable(false);

        ImageButton deleteButton = new ImageButton(this);
        LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        deleteButton.setLayoutParams(deleteButtonParams);
        deleteButton.setImageResource(R.drawable.eliminate_blue);
        deleteButton.setBackgroundColor(Color.parseColor("#354F52"));
        deleteButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        deleteButton.setPadding(16, 16, 16, 16);

        editText.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                deleteButton.getLayoutParams().height = bottom - top;
                deleteButton.requestLayout();
            }
        });

        horizontalLayout.addView(editText);
        horizontalLayout.addView(deleteButton);
        ingredientList.addView(horizontalLayout);
    }

    private void addStepView(LinearLayout stepList, String stepText) {
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);
        horizontalLayout.setLayoutParams(layoutParams);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        editText.setLayoutParams(editTextParams);
        editText.setText(stepText);
        editText.setHint("Step");
        editText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
        editText.setTextColor(Color.parseColor("#808080"));
        editText.setPadding(5, 5, 5, 5);
        editText.setEnabled(false);
        editText.setClickable(false);

        ImageButton deleteButton = new ImageButton(this);
        LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        deleteButton.setLayoutParams(deleteButtonParams);
        deleteButton.setImageResource(R.drawable.eliminate_blue);
        deleteButton.setBackgroundColor(Color.parseColor("#354F52"));
        deleteButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        deleteButton.setPadding(16, 16, 16, 16);

        editText.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                deleteButton.getLayoutParams().height = bottom - top;
                deleteButton.requestLayout();
            }
        });


        horizontalLayout.addView(editText);
        horizontalLayout.addView(deleteButton);
        stepList.addView(horizontalLayout);
    }
    //endregion

    //region Network Utils
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

    public void Back(View view){
        setResult(RESULT_OK);
        finish();
    }
}