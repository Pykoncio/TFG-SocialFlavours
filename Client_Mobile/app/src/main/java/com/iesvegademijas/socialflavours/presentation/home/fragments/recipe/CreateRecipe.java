package com.iesvegademijas.socialflavours.presentation.home.fragments.recipe;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.common.DateUtil;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Ingredient;
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateRecipe#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * // Verificar si se tienen los permisos necesarios
 * if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
 *         != PackageManager.PERMISSION_GRANTED) {
 *     // Si no se tienen los permisos, solicitarlos al usuario
 *     ActivityCompat.requestPermissions(this,
 *             new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
 *             MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
 * } else {
 *     // Si se tienen los permisos, proceder con la acción que los requiere
 *     // Por ejemplo, leer o escribir archivos externos
 * }
 */
public class CreateRecipe extends Fragment {

    private View myView;
    private ImageView recipeImageView;
    private String imagePath;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1; // Long id of the user
    private String mParam2;

    public CreateRecipe() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateRecipe.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateRecipe newInstance(String param1, String param2) {
        CreateRecipe fragment = new CreateRecipe();
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

        myView = inflater.inflate(R.layout.fragment_create_recipe, container, false);

        Button createButton = myView.findViewById(R.id.new_recipe_button_create);

        // Set OnClickListener for the button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your create method here
                create();
            }
        });

        recipeImageView = myView.findViewById(R.id.new_recipe_image);
        recipeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });

        populateSpinners();

        //region Ingredient List
        LinearLayout ingredientList = myView.findViewById(R.id.ingredientList);
        ImageButton addIngredientButton = myView.findViewById(R.id.new_recipe_add_ingredient_button);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   // Create a horizontal layout to contain the String of the list and the Button to eliminate it
                   LinearLayout horizontalLayout = new LinearLayout(getContext());
                   horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                   LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                           LinearLayout.LayoutParams.MATCH_PARENT,
                           LinearLayout.LayoutParams.WRAP_CONTENT
                   );
                   horizontalLayout.setLayoutParams(layoutParams);

                   // Create the editText
                   EditText editText = new EditText(getContext());
                   LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                           0,
                           LinearLayout.LayoutParams.WRAP_CONTENT,
                           1
                   );
                   editText.setLayoutParams(editTextParams);
                   editText.setHint("Ingredient");
                   editText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                   editText.setTextColor(Color.parseColor("#808080"));
                   editText.setPadding(5, 5, 5, 5);

                   // Create the imageButton to eliminate this String
                   ImageButton deleteButton = new ImageButton(getContext());
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
                           ingredientList.removeView(horizontalLayout);
                       }
                   });

                   // Add both resources to the horizontalLayout
                   horizontalLayout.addView(editText);
                   horizontalLayout.addView(deleteButton);

                   // Add the horizontalLayout to the ingredient list
                   ingredientList.addView(horizontalLayout);
               }
           });
        //endregion

        //region Steps List
            LinearLayout stepList = myView.findViewById(R.id.stepList);
            ImageButton addStepButton = myView.findViewById(R.id.new_recipe_add_step_button);

            addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a horizontal layout to contain the String of the list and the Button to eliminate it
                LinearLayout horizontalLayout = new LinearLayout(getContext());
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                horizontalLayout.setLayoutParams(layoutParams);

                // Create the editText
                EditText editText = new EditText(getContext());
                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );
                editText.setLayoutParams(editTextParams);
                editText.setHint("Ingredient");
                editText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                editText.setTextColor(Color.parseColor("#808080"));
                editText.setPadding(5, 5, 5, 5);

                // Create the imageButton to eliminate this String
                ImageButton deleteButton = new ImageButton(getContext());
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
                        stepList.removeView(horizontalLayout);
                    }
                });

                // Add both resources to the horizontalLayout
                horizontalLayout.addView(editText);
                horizontalLayout.addView(deleteButton);

                // Add the horizontalLayout to the step list
                stepList.addView(horizontalLayout);
            }
        });
        //endregion

        return myView;
    }

    //region Populate Spinners and Create Recipe

    private void populateSpinners(){
        // Rating
        Resources res = getResources();

        Spinner sRating = (Spinner) myView.findViewById(R.id.new_recipe_ratingSpinner);
        ArrayList<String> ratings = new ArrayList<>();

        ratings.add(res.getString(R.string.oneStar));
        ratings.add(res.getString(R.string.twoStars));
        ratings.add(res.getString(R.string.threeStars));
        ratings.add(res.getString(R.string.fourStars));
        ratings.add(res.getString(R.string.fiveStars));

        ArrayAdapter<String> adRating = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,
                ratings);

        adRating.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sRating.setAdapter(adRating);

        // Tags
        Spinner sTag = (Spinner) myView.findViewById(R.id.new_recipe_tagSpinner);
        ArrayList<String> tags = new ArrayList<>();

        tags.add(res.getString(R.string.breakfast));
        tags.add(res.getString(R.string.lunch));
        tags.add(res.getString(R.string.dinner));
        tags.add(res.getString(R.string.snack));

        ArrayAdapter<String> adTag = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,
                tags);

        adTag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sTag.setAdapter(adTag);
    }
    private ArrayList<String> getSteps() {
        ArrayList<String> steps = new ArrayList<>();
        LinearLayout stepList = myView.findViewById(R.id.stepList);

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
        LinearLayout ingredientList = myView.findViewById(R.id.ingredientList);

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
    public void create() {
        boolean continueToCreate = true;

        EditText etTitle = myView.findViewById(R.id.new_recipe_title);
        EditText etDescription = myView.findViewById(R.id.new_recipe_description);
        EditText etPreparationTime = myView.findViewById(R.id.new_recipe_et_preparationTime);
        Spinner sTags = (Spinner) myView.findViewById(R.id.new_recipe_tagSpinner);
        Spinner sRating =(Spinner) myView.findViewById(R.id.new_recipe_ratingSpinner);

        // Obtain the actual values
        List<String> ingredients = new ArrayList<>(getIngredients());
        List<String> steps = new ArrayList<>(getSteps());

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String preparationTime = etPreparationTime.getText().toString().trim();
        String tag = sTags.getSelectedItem().toString();
        String rating = sRating.getSelectedItem().toString();

        // Obtain the current date using Calendar
        Calendar calendar = Calendar.getInstance();

        Date currentDate = calendar.getTime();
        String formattedDate = DateUtil.formatDate(currentDate);

        Date creationDate = null;

        try {
            creationDate = DateUtil.parseDate(formattedDate);
        }catch (ParseException e) {
            e.printStackTrace();
            continueToCreate=false;
        }

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
            Button btCreate= myView.findViewById(R.id.new_recipe_button_create);
            ProgressBar pbCreate= myView.findViewById(R.id.pb_create_recipe);
            btCreate.setEnabled(false);
            btCreate.setClickable(false);
            pbCreate.setVisibility(View.VISIBLE);
            if (isNetworkAvailable()) {
                String url = getResources().getString(R.string.main_url) + "recipeapi/createRecipe";
                sendTask(url, title, rating, description, preparationTime, tag, ingredients, steps, creationDate);
            } else {
                showError("error.IOException");
            }
        }
    }



    private void sendTask(String url, String title, String rating,
                          String description, String preparationTime,
                          String tag, List<String> ingredients,
                          List<String> steps, Date date
                          ) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOperator= ApiOperator.getInstance();
                HashMap<String, Object> params = new HashMap<>();
                params.put("name", title);
                params.put("imagePath", imagePath);
                params.put("rating", rating);
                params.put("description", description);
                params.put("preparationTime", preparationTime);
                params.put("tag", tag);
                params.put("ingredients", ingredients);
                params.put("steps", steps);
                params.put("userId", mParam1);
                params.put("date", date);
                String result = apiOperator.postText(url,params);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Button btCreate= myView.findViewById(R.id.new_recipe_button_create);
                        ProgressBar pbCreate=(ProgressBar) myView.findViewById(R.id.pb_create_recipe);
                        pbCreate.setVisibility(View.GONE);
                        btCreate.setEnabled(true);
                        btCreate.setClickable(true);
                        long idCreated;
                        try{
                            idCreated=Long.parseLong(result);
                        }catch(NumberFormatException ex){
                            idCreated=-1;
                        }
                        if(idCreated>0){
                            // getActivity().getSupportFragmentManager().popBackStack(); One way to do it
                            getActivity().finish();
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
    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If we dont have the permissions, ask the user
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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
            Picasso.get().load(imageUri).into(recipeImageView);
            // Obtain the path for the image
            imagePath = getRealPathFromURI(imageUri);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
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

    //region Network utils
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
        Toast toast = Toast.makeText(myView.getContext(), message, duration);
        toast.show();
    }
    //endregion

}