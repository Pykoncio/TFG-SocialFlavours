package com.iesvegademijas.socialflavours.presentation.home.fragments.recipe;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContextCompat.getSystemService;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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

    private User user;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private static final int MAX_RETRIES = 5;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
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

        getUser(mParam1);

        // Find the button by its id
        Button createButton = myView.findViewById(R.id.new_recipe_button_accept);

        // Set OnClickListener for the button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your create method here
                create();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_create_recipe, container, false);


        recipeImageView = myView.findViewById(R.id.new_recipe_image);
        recipeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });


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

    public void create() {
        
    }



    //region Retrieve User
    private void getUser(String id)
    {
        ProgressBar pbMain = (ProgressBar) myView.findViewById(R.id.pb_create_recipe);
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
                            ProgressBar pbMain = (ProgressBar) myView.findViewById(R.id.pb_create_recipe);
                            pbMain.setVisibility(View.GONE);
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
}