package com.iesvegademijas.socialflavours.data.remote.dto.entities;

import android.net.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
    private long id_item;
    private String name;
    private int quantity;
    private boolean isChecked;

    public Item(){}

    public Item(long id_item, String name, int quantity, boolean isChecked) {
        this.id_item = id_item;
        this.name = name;
        this.quantity = quantity;
        this.isChecked = isChecked;
    }

    public long getId_item() {
        return id_item;
    }

    public void setId_item(long id_item) {
        this.id_item = id_item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException
    {
        if(!jsonObject.isNull("id_item")) {
            this.id_item = jsonObject.getLong("id_item");
        }
        else
        {
            this.id_item = -1;
        }

        if (!jsonObject.isNull("name")) {
            this.name = jsonObject.getString("name");
        }
        else
        {
            this.name = "";
        }

        if (!jsonObject.isNull("quantity")) {
            this.quantity = jsonObject.getInt("quantity");
        }
        else
        {
            this.quantity = -1;
        }

        if (!jsonObject.isNull("isChecked")) {
            this.isChecked = jsonObject.getBoolean("isChecked");
        }
        else
        {
            this.isChecked = false;
        }
    }
}
