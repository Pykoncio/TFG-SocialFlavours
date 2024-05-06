package com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.mealPlanner;

import android.net.ParseException;

import com.iesvegademijas.socialflavours.common.DateUtil;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class PlanEntry {
    private long id_plan;
    private Date date;
    private String recipe_type;
    private Recipe recipe;

    public PlanEntry(){}

    public PlanEntry(long id_plan, Date date, String recipe_type, Recipe recipe) {
        this.id_plan = id_plan;
        this.date = date;
        this.recipe_type = recipe_type;
        this.recipe = recipe;
    }

    public long getId_plan() {
        return id_plan;
    }

    public void setId_plan(long id_plan) {
        this.id_plan = id_plan;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRecipe_type() {
        return recipe_type;
    }

    public void setRecipe_type(String recipe_type) {
        this.recipe_type = recipe_type;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException, java.text.ParseException {
        if (!jsonObject.isNull("id_plannerEntry")) {
            this.id_plan = jsonObject.getLong("id_plannerEntry");
        }
        else
        {
            this.id_plan = -1;
        }

        if (!jsonObject.isNull("date"))
        {
            String unParsedDate = jsonObject.getString("date");
            this.date = DateUtil.parseDate(unParsedDate);
        }
        else
        {
            this.date = null;
        }

        if (!jsonObject.isNull("recipe_type")) {
            this.recipe_type = jsonObject.getString("recipe_type");
        }
        else
        {
            this.recipe_type = "";
        }

        if (!jsonObject.isNull("recipe"))
        {
            JSONObject recipeData = jsonObject.getJSONObject("recipe");
            this.recipe = new Recipe();
            this.recipe.fromJSON(recipeData);
        }


    }
}
