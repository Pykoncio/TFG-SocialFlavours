package com.iesvegademijas.socialflavours.data.remote.dto.foodRelated;

import android.net.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

public class Filter {
    private String filterType;

    public Filter(){}

    public Filter(String filterType)
    {
        this.filterType = filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterType() {
        return filterType;
    }

    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
        if (!jsonObject.isNull("filterType")) {
            this.filterType = jsonObject.getString(("filterType"));
        }
        else
        {
            this.filterType = "";
        }
    }
}
