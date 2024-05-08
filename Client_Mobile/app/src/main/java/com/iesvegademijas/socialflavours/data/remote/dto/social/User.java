package com.iesvegademijas.socialflavours.data.remote.dto.social;

import android.net.ParseException;

import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Filter;
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
    private List<FriendShip> receivedFriendshipRequests;
    private List<FriendShip> sentFriendshipsRequests;
    private MealPlan mealPlan;
    private List<Filter> filters;
    private List<ShoppingList> shoppingLists;
    private List<Recipe> recipes;
    private List<User> friends;

    public User(){}

    public User(long id_user, String username, String password, String email, List<FriendShip> receivedFriendshipRequests, List<FriendShip> sentFriendshipsRequests, MealPlan mealPlan) {
        this.id_user = id_user;
        this.username = username;
        this.password = password;
        this.email = email;
        this.receivedFriendshipRequests = receivedFriendshipRequests;
        this.sentFriendshipsRequests = sentFriendshipsRequests;
        this.mealPlan = mealPlan;
    }

    public User(long id_user, String username, String password, String email, List<FriendShip> receivedFriendshipRequests, List<FriendShip> sentFriendshipsRequests, MealPlan mealPlan, List<Filter> filters, List<ShoppingList> shoppingLists, List<Recipe> recipes, List<User> friends) {
        this.id_user = id_user;
        this.username = username;
        this.password = password;
        this.email = email;
        this.receivedFriendshipRequests = receivedFriendshipRequests;
        this.sentFriendshipsRequests = sentFriendshipsRequests;
        this.mealPlan = mealPlan;
        this.filters = filters;
        this.shoppingLists = shoppingLists;
        this.recipes = recipes;
        this.friends = friends;
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

    public List<FriendShip> getReceivedFriendshipRequests() {
        return receivedFriendshipRequests;
    }

    public void setReceivedFriendshipRequests(List<FriendShip> receivedFriendshipRequests) {
        this.receivedFriendshipRequests = receivedFriendshipRequests;
    }

    public List<FriendShip> getSentFriendshipsRequests() {
        return sentFriendshipsRequests;
    }

    public void setSentFriendshipsRequests(List<FriendShip> sentFriendshipsRequests) {
        this.sentFriendshipsRequests = sentFriendshipsRequests;
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
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

        // Handling lists
        this.receivedFriendshipRequests = new ArrayList<>();
        JSONArray receivedRequestsArray = jsonObject.getJSONArray("receivedFriendshipRequests");
        for (int i = 0; i < receivedRequestsArray.length(); i++) {
            JSONObject receivedRequestObject = receivedRequestsArray.getJSONObject(i);
            FriendShip receivedRequest = new FriendShip();
            receivedRequest.fromJSON(receivedRequestObject);
            this.receivedFriendshipRequests.add(receivedRequest);
        }

        this.sentFriendshipsRequests = new ArrayList<>();
        JSONArray sentRequestsArray = jsonObject.getJSONArray("sentFriendshipRequests");
        for (int i = 0; i < sentRequestsArray.length(); i++) {
            JSONObject sentRequestObject = sentRequestsArray.getJSONObject(i);
            FriendShip sentRequest = new FriendShip();
            sentRequest.fromJSON(sentRequestObject);
            this.sentFriendshipsRequests.add(sentRequest);
        }

        if (!jsonObject.isNull("mealPlan")) {
            JSONObject mealPlanObject = jsonObject.getJSONObject("mealPlan");
            this.mealPlan = new MealPlan();
            this.mealPlan.fromJSON(mealPlanObject);
        } else {
            this.mealPlan = null;
        }

        this.filters = new ArrayList<>();
        JSONArray filtersArray = jsonObject.getJSONArray("filters");
        for (int i = 0; i < filtersArray.length(); i++) {
            JSONObject filterObject = filtersArray.getJSONObject(i);
            Filter filter = new Filter();
            filter.fromJSON(filterObject);
            this.filters.add(filter);
        }

        this.shoppingLists = new ArrayList<>();
        JSONArray shoppingListsArray = jsonObject.getJSONArray("shoppingLists");
        for (int i = 0; i < shoppingListsArray.length(); i++) {
            JSONObject shoppingListObject = shoppingListsArray.getJSONObject(i);
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.fromJSON(shoppingListObject);
            this.shoppingLists.add(shoppingList);
        }

        this.recipes = new ArrayList<>();
        JSONArray recipesArray = jsonObject.getJSONArray("recipes");
        for (int i = 0; i < recipesArray.length(); i++) {
            JSONObject recipeObject = recipesArray.getJSONObject(i);
            Recipe recipe = new Recipe();
            recipe.fromJSON(recipeObject);
            this.recipes.add(recipe);
        }

        this.friends = new ArrayList<>();
        JSONArray friendsArray = jsonObject.getJSONArray("friends");
        for (int i = 0; i < friendsArray.length(); i++) {
            JSONObject friendObject = friendsArray.getJSONObject(i);
            User friend = new User();
            friend.fromJSON(friendObject);
            this.friends.add(friend);
        }
    }

}
