package com.iesvegademijas.socialflavours.presentation.modify_recipe;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.common.DateUtil;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Ingredient;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Step;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModifyRecipe extends AppCompatActivity {

    private long idRecipe;

    private static final int MAX_RETRIES = 5;

    private String imagePath;
    private long idUser;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private ImageView recipeImageView;

    private ActivityResultLauncher<Intent> takeImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null)
                {
                    Uri uri = result.getData().getData();

                    imagePath = getRealPathFromURI(uri);

                    ImageView imageView = findViewById(R.id.modify_recipe_image);
                    imageView.setImageURI(uri);
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_recipe);

        Intent myIntent = getIntent();
        idRecipe = myIntent.getLongExtra("id_recipe", -1);

        loadRecipe();

        recipeImageView = findViewById(R.id.modify_recipe_image);

        recipeImageView.setOnClickListener( v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            takeImageLauncher.launch(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //region Load Recipe Data
    private void loadRecipe(){
        ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_modify_recipe);
        pbMain.setVisibility(View.VISIBLE);
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

            Intent myIntent = getIntent();
            idUser = myIntent.getLongExtra("id_user", -1);

            EditText etTitle = findViewById(R.id.modify_recipe_title);
            EditText etDescription = findViewById(R.id.modify_recipe_description);
            EditText etPreparationTime = findViewById(R.id.modify_recipe_et_preparationTime);

            Spinner sRating = findViewById(R.id.modify_recipe_ratingSpinner);
            Spinner sTag = findViewById(R.id.modify_recipe_tagSpinner);

            ImageView imageView = findViewById(R.id.modify_recipe_image);

            File file = new File(getFilesDir(), recipe.getImagePath() );
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);

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



            // Sorting the order of the Ingredient list
            List<Ingredient> sortedIngredients = recipe.getIngredients();
            Collections.sort(sortedIngredients, Comparator.comparingLong(Ingredient::getId_ingredient));

            // Populate existing ingredients
            LinearLayout ingredientList = findViewById(R.id.modify_ingredientList);
            for (Ingredient ingredient : sortedIngredients) {
                addIngredientView(ingredientList, ingredient.getName());
            }

            // Button to add new ingredient
            ImageButton addIngredientButton = findViewById(R.id.modify_recipe_add_ingredient_button);
            addIngredientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addIngredientView(ingredientList, "");
                }
            });

            // Sorting the order of the Step list
            List<Step> sortedSteps = recipe.getSteps();
            Collections.sort(sortedSteps, Comparator.comparingLong(Step::getId_step));

            // Populate existing steps
            LinearLayout stepList = findViewById(R.id.modify_stepList);
            for (Step step : sortedSteps) {
                addStepView(stepList, step.getStep());
            }

            // Button to add new step
            ImageButton addStepButton = findViewById(R.id.modify_recipe_add_step_button);
            addStepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addStepView(stepList, "");
                }
            });

            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_modify_recipe);
            pbMain.setVisibility(View.GONE);
        } catch (JSONException | ParseException e) {
            showError("error.json");
        }
    }

    // Method to add an Ingredient view dynamically
    private void addIngredientView(LinearLayout ingredientList, String ingredientName) {
        // Create a horizontal layout to contain ingredient name and delete button
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);
        horizontalLayout.setLayoutParams(layoutParams);

        // Create EditText for ingredient name
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

        // Create ImageButton to delete ingredient
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientList.removeView(horizontalLayout);
            }
        });

        horizontalLayout.addView(editText);
        horizontalLayout.addView(deleteButton);
        ingredientList.addView(horizontalLayout);
    }

    // Method to add a step view dynamically
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepList.removeView(horizontalLayout);
            }
        });

        horizontalLayout.addView(editText);
        horizontalLayout.addView(deleteButton);
        stepList.addView(horizontalLayout);
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

    //region Save State
    private ArrayList<String> getSteps() {
        ArrayList<String> steps = new ArrayList<>();
        LinearLayout stepList = findViewById(R.id.modify_stepList);

        for (int i = 0; i < stepList.getChildCount(); i++) {
            View view = stepList.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout horizontalLayout = (LinearLayout) view;
                EditText editText = (EditText) horizontalLayout.getChildAt(0);
                String step = editText.getText().toString().trim();
                if (!step.isEmpty()) {
                    steps.add(step);
                }
            }
        }
        return steps;
    }
    private ArrayList<String> getIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        LinearLayout ingredientList = findViewById(R.id.modify_ingredientList);

        for (int i = 0; i < ingredientList.getChildCount(); i++) {
            View view = ingredientList.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout horizontalLayout = (LinearLayout) view;
                EditText editText = (EditText) horizontalLayout.getChildAt(0);
                String ingredient = editText.getText().toString().trim();
                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                }
            }
        }
        return ingredients;
    }
    public void Save(View view){
        boolean continueToCreate = true;

        EditText etTitle = findViewById(R.id.modify_recipe_title);
        EditText etDescription = findViewById(R.id.modify_recipe_description);
        EditText etPreparationTime = findViewById(R.id.modify_recipe_et_preparationTime);
        Spinner sTags = (Spinner) findViewById(R.id.modify_recipe_tagSpinner);
        Spinner sRating =(Spinner) findViewById(R.id.modify_recipe_ratingSpinner);

        // Obtain the actual values
        List<String> ingredients = getIngredients();
        List<String> steps = getSteps();

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String preparationTime = etPreparationTime.getText().toString().trim();
        String tag = sTags.getSelectedItem().toString();
        String rating = sRating.getSelectedItem().toString();

        if(title.isEmpty()){
            etTitle.setError(getResources().getString(R.string.compulsory_field));
            continueToCreate=false;
        }
        if(description.isEmpty()){
            etDescription.setError(getResources().getString(R.string.compulsory_field));
            continueToCreate=false;
        }
        if(preparationTime.isEmpty()){
            etPreparationTime.setError(getResources().getString(R.string.compulsory_field));
            continueToCreate=false;
        }

        if (continueToCreate) {
            Button btSave= findViewById(R.id.modify_recipe_button_save);
            ProgressBar pbModify= findViewById(R.id.pb_modify_recipe);
            btSave.setEnabled(false);
            btSave.setClickable(false);
            pbModify.setVisibility(View.VISIBLE);
            if (isNetworkAvailable()) {
                String url = getResources().getString(R.string.main_url) + "recipeapi/updateRecipe";
                sendTask(url, idRecipe+"",title, rating, description, preparationTime, tag, ingredients, steps);
            } else {
                showError("error.IOException");
            }
        }
    }

    private void sendTask(String url, String idrecipe,String title, String rating, String description,
                          String preparationTime, String tag, List<String> ingredients,
                          List<String> steps) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOperator=ApiOperator.getInstance();
                HashMap<String, Object> params = new HashMap<>();
                params.put("id_recipe", idrecipe);
                params.put("name", title);
                params.put("description", description);
                params.put("imagePath", imagePath);
                params.put("preparationTime", preparationTime);
                params.put("tag", tag);
                params.put("rating", rating);
                params.put("ingredients", ingredients);
                params.put("steps", steps);
                params.put("userId", idUser);
                String result = apiOperator.putText(url,params);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Button btSave= findViewById(R.id.modify_recipe_button_save);
                        ProgressBar pbAccept=(ProgressBar) findViewById(R.id.pb_modify_recipe);
                        pbAccept.setVisibility(View.GONE);
                        btSave.setEnabled(true);
                        btSave.setClickable(true);
                        long createdId;
                        try{
                            createdId=Long.parseLong(result);
                        }catch(NumberFormatException ex){
                            createdId=-1;
                        }
                        if(createdId>0){
                            setResult(RESULT_OK);
                            finish();
                        }
                        else {
                            showError("error.Unknown");
                        }
                    }
                });
            }
        });
    }

    //endregion

    //region Image Picker
    public void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If we dont have the permissions, ask the user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            // If we have the permissions, pick the image
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, proceder con la acción
                pickImageFromGallery();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Using Picasso to load the Image View
            ImageView imageView = findViewById(R.id.modify_recipe_image);

            // Obtain the path for the image
            imagePath = getRealPathFromURI(imageUri);

            Picasso.get().load(imagePath).into(imageView);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    //endregion
}