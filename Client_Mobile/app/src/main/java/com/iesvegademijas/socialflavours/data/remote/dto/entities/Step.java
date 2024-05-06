package com.iesvegademijas.socialflavours.data.remote.dto.entities;

import android.net.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

public class Step {
    private long id_step;
    private String step;

    public Step(){}

    public Step(long id_step, String step)
    {
        this.id_step = id_step;
        this.step = step;
    }

    public long getId_step() {
        return id_step;
    }

    public void setId_step(long id_step) {
        this.id_step = id_step;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException
    {
        if (!jsonObject.isNull("id_step")) {
            this.id_step = jsonObject.getLong("id_step");
        }
        else
        {
            this.id_step = -1;
        }

        if (!jsonObject.isNull("step")) {
            this.step = jsonObject.getString("step");
        }
        else
        {
            this.step = "";
        }
    }
}
