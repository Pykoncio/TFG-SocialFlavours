package com.iesvegademijas.socialflavours.data.remote.dto.social;

import android.net.ParseException;

import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.mealPlanner.MealPlan;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.ShoppingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    private long id_user;
    private String username;
    private String password;
    private String email;

    public User(){}

    public User(long id_user, String username, String password, String email) {
        this.id_user = id_user;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException, java.text.ParseException {
        if (!jsonObject.isNull("id_user")) {
            this.id_user = jsonObject.getLong("id_user");
        } else {
            this.id_user = -1;
        }
        if (!jsonObject.isNull("username")) {
            this.username = jsonObject.getString("username");
        } else {
            this.username = "";
        }
        if (!jsonObject.isNull("password")) {
            // Assuming password is base64 encoded and needs to be decoded
            this.password = new String(android.util.Base64.decode(jsonObject.getString("password"), android.util.Base64.DEFAULT));
        } else {
            this.password = "";
        }
        if (!jsonObject.isNull("email")) {
            this.email = jsonObject.getString("email");
        } else {
            this.email = "";
        }
    }

}
