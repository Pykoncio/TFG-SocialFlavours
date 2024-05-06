package com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.mealPlanner;

import com.iesvegademijas.socialflavours.data.remote.dto.social.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MealPlan {
    private long id_planner;
    private User user;
    private List<PlanEntry> plannerEntries;

    public MealPlan(){}

    public MealPlan(long idPlanner, User user, List<PlanEntry> plannerEntries) {
        this.id_planner = idPlanner;
        this.user = user;
        this.plannerEntries = plannerEntries;
    }

    public long getId_planner() {
        return id_planner;
    }

    public void setId_planner(long id_planner) {
        this.id_planner = id_planner;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PlanEntry> getPlannerEntries() {
        return plannerEntries;
    }

    public void setPlannerEntries(List<PlanEntry> plannerEntries) {
        this.plannerEntries = plannerEntries;
    }

    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
        if(!jsonObject.isNull("id_planner")) {
            this.id_planner = jsonObject.getLong("id_planner");
        }
        else{
            this.id_planner=-1;
        }
        if(!jsonObject.isNull("user")) {
            JSONObject userObject = jsonObject.getJSONObject("user");
            this.user = new User();
            this.user.fromJSON(userObject);
        }
        else{
            this.user=null;
        }

        this.plannerEntries = new ArrayList<>();
        JSONArray plannerEntries = jsonObject.getJSONArray("plannerEntries");
        for (int i = 0; i < plannerEntries.length(); i++) {
            JSONObject plan = plannerEntries.getJSONObject(i);
            PlanEntry planEntry = new PlanEntry();
            planEntry.fromJSON(plan);
            this.plannerEntries.add(planEntry);
        }

    }
}
