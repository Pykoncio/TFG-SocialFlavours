package com.iesvegademijas.socialflavours.data.remote.dto.foodRelated;

import com.iesvegademijas.socialflavours.common.DateUtil;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Ingredient;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Step;
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recipe {
    private long id_recipe;
    private User user;
    private String name;
    private String description;
    private String rating;
    private String imagePath;
    private int preparationTime;
    private String tag;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    public Recipe(){}

    public Recipe(long id_recipe, User user, String name, String description, String rating, String imagePath, int preparationTime, String tag) {
        this.id_recipe = id_recipe;
        this.user = user;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.imagePath = imagePath;
        this.preparationTime = preparationTime;
        this.tag = tag;
    }

    public Recipe(long id_recipe, User user, String name, String description, String rating, String imagePath, int preparationTime, String tag,  List<Ingredient> ingredients, List<Step> steps) {
        this.id_recipe = id_recipe;
        this.user = user;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.imagePath = imagePath;
        this.preparationTime = preparationTime;
        this.tag  = tag;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public long getId_recipe() {
        return id_recipe;
    }

    public void setId_recipe(long id_recipe) {
        this.id_recipe = id_recipe;
    }

    public User getUser() {
        return user;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }


    public void fromJSON(JSONObject jsonObject) throws JSONException, java.text.ParseException {
        if (!jsonObject.isNull("id_recipe")) {
            this.id_recipe = jsonObject.getLong("id_recipe");
        } else {
            this.id_recipe = -1;
        }

        if (!jsonObject.isNull("user")) {
            JSONObject userObject = jsonObject.getJSONObject("user");
            this.user = new User();
            this.user.fromJSON(userObject);
        } else {
            this.user = null;
        }

        if (!jsonObject.isNull("name")) {
            this.name = jsonObject.getString("name");
        } else {
            this.name = "";
        }

        if (!jsonObject.isNull("description")) {
            this.description = jsonObject.getString("description");
        } else {
            this.description = "";
        }

        if (!jsonObject.isNull("rating")) {
            this.rating = jsonObject.getString("rating");
        } else {
            this.rating = "";
        }

        if (!jsonObject.isNull("imagePath")) {
            this.imagePath = jsonObject.getString("imagePath");
        } else {
            this.imagePath = "";
        }

        if (!jsonObject.isNull("preparationTime")) {
            this.preparationTime = jsonObject.getInt("preparationTime");
        } else {
            this.preparationTime = 0;
        }

        if (!jsonObject.isNull("tag")) {
            this.tag = jsonObject.getString("tag");
        } else {
            this.tag = "";
        }

        // Parse ingredients array
        this.ingredients = new ArrayList<>();
        if (!jsonObject.isNull("ingredients")) {
            JSONArray ingredientsArray = jsonObject.getJSONArray("ingredients");
            for (int i = 0; i < ingredientsArray.length(); i++) {
                JSONObject ingredientObject = ingredientsArray.getJSONObject(i);
                Ingredient ingredient = new Ingredient();
                ingredient.setId_ingredient(ingredientObject.getLong("id_ingredient"));
                ingredient.setName(ingredientObject.getString("name"));
                this.ingredients.add(ingredient);
            }
        }

        // Parse steps array
        this.steps = new ArrayList<>();
        if (!jsonObject.isNull("steps")) {
            JSONArray stepsArray = jsonObject.getJSONArray("steps");
            for (int i = 0; i < stepsArray.length(); i++) {
                JSONObject stepObject = stepsArray.getJSONObject(i);
                Step step = new Step();
                step.setId_step(stepObject.getLong("id_step"));
                step.setStep(stepObject.getString("step"));
                this.steps.add(step);
            }
        }
    }

}
