package com.iesvegademijas.socialflavours.data.remote.dto.foodRelated;

import android.net.ParseException;

import com.iesvegademijas.socialflavours.data.remote.dto.entities.Ingredient;
import com.iesvegademijas.socialflavours.data.remote.dto.entities.Item;
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShoppingList {
    private long id_shoppingList;
    private String listName;
    private List<Item> itemList;

    public ShoppingList(){}

    public ShoppingList(long id_shoppingList) {
        this.id_shoppingList = id_shoppingList;
        this.itemList = new ArrayList<>();
    }

    public ShoppingList(String name, List<Item> itemList) {
        this.listName = name;
        this.itemList = itemList;
    }

    public long getId_shoppingList() {
        return id_shoppingList;
    }

    public void setId_shoppingList(long id_shoppingList) {
        this.id_shoppingList = id_shoppingList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException, java.text.ParseException {
        if (!jsonObject.isNull("id_shoppingList")) {
            this.id_shoppingList = jsonObject.getLong("id_shoppingList");
        }
        else
        {
            this.id_shoppingList = -1;
        }

        if(!jsonObject.isNull("shoppingListName")) {
            this.listName = jsonObject.getString("shoppingListName");
        }
        else
        {
            this.listName = "";
        }

        if (!jsonObject.isNull("itemList")) {
            this.itemList = new ArrayList<>();

            JSONArray itemsArray = jsonObject.getJSONArray("itemList");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);
                Item item = new Item();
                item.fromJSON(itemObject);
                this.itemList.add(item);
            }
        } else {
            this.itemList = new ArrayList<>();
        }
    }
}
