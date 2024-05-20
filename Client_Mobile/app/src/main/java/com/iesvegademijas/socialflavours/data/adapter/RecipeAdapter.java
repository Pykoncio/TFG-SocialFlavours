package com.iesvegademijas.socialflavours.data.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;
import com.iesvegademijas.socialflavours.presentation.home.HomePage;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Recipe> recipes;

    private RecipesAdapterCallBack callback;
    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        super();
        this.context = context;
        this.recipes = recipes;
    }


    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        if (recipes==null) {
            return null;
        }
        else
        {
            return recipes.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        RecipeAdapter.RecipeWrapper recipeWrapper;

        if (item==null)
        {
            recipeWrapper = new RecipeAdapter.RecipeWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.list_item_recipe, parent, false);
            recipeWrapper.container = item.findViewById(R.id.item_container);
            recipeWrapper.recipeIcon = item.findViewById(R.id.item_imageView);
            recipeWrapper.title = item.findViewById(R.id.item_title);
            recipeWrapper.preparationTime = item.findViewById(R.id.item_preparationTime);
            recipeWrapper.tag = item.findViewById(R.id.item_tag);
            item.setTag(recipeWrapper);
        }
        else
        {
            recipeWrapper = (RecipeAdapter.RecipeWrapper) item.getTag();
        }

        Recipe recipe = recipes.get(position);

        recipeWrapper.title.setText(recipe.getName());
        recipeWrapper.preparationTime.setText(recipe.getPreparationTime());
        recipeWrapper.tag.setText(recipe.getTag());

        // Load the recipeIcon or set a default image
        if (recipe.getImagePath() != null && !recipe.getImagePath().isEmpty()) {
            Picasso.get().load(recipe.getImagePath()).into(recipeWrapper.recipeIcon);
        } else {
            recipeWrapper.recipeIcon.setImageResource(R.drawable.default_recipe_image);
        }

        recipeWrapper.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.editPressed(position);
            }
        });

        recipeWrapper.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (callback != null) {
                    callback.deletePressed(position);
                }
                return true; // Return true to indicate the long click event is handled
            }
        });

        return item;
    }

    static class RecipeWrapper {

        ConstraintLayout container;
        ImageView recipeIcon;
        TextView title;
        TextView preparationTime;
        TextView tag;

    }

    public interface RecipesAdapterCallBack {
        public void editPressed(int position);
        public void deletePressed(int position);
    }

    public void setCallback(RecipesAdapterCallBack callback) {this.callback = callback;}
}
