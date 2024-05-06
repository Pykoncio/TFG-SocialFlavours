package com.iesvegademijas.socialflavours.data.remote.dto.entities;

import android.net.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

public class Ingredient {

    private long id_ingredient;
    private String name;

    public Ingredient(){}

    public Ingredient(long id_step, String step)
    {
        this.id_ingredient = id_step;
        this.name = step;
    }

    public long getId_ingredient() {
        return id_ingredient;
    }

    public void setId_ingredient(long id_ingredient) {
        this.id_ingredient = id_ingredient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException
    {
        if (!jsonObject.isNull("id_step")) {
            this.id_ingredient = jsonObject.getLong("id_step");
        }
        else
        {
            this.id_ingredient = -1;
        }

        if (!jsonObject.isNull("step")) {
            this.name = jsonObject.getString("step");
        }
        else
        {
            this.name = "";
        }
    }
}
