package com.iesvegademijas.socialflavours.data.remote.dto.social;

import android.net.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendShip {
    private long id_friendship;
    private long idSender;
    private long idReceiver;
    private String status;

    public FriendShip(){}

    public FriendShip(long idFriendship, long idSender, long idReceiver, String status) {
        this.id_friendship = idFriendship;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.status = status;
    }

    public long getId_friendship() {
        return id_friendship;
    }

    public void setId_friendship(long id_friendship) {
        this.id_friendship = id_friendship;
    }

    public long getIdSender() {
        return idSender;
    }

    public void setIdSender(long idSender) {
        this.idSender = idSender;
    }

    public long getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(long idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException
    {
        if (!jsonObject.isNull("id_friendship")) {
            this.id_friendship = jsonObject.getLong("id_friendship");
        }
        else
        {
            this.id_friendship = -1;
        }

        if (!jsonObject.isNull("id_userSender")) {
            this.idSender = jsonObject.getLong("id_userSender");
        }
        else
        {
            this.idSender = -1;
        }

        if (!jsonObject.isNull("id_userReceiver")) {
            this.idReceiver = jsonObject.getLong("id_userReceiver");
        }
        else
        {
            this.idReceiver = -1;
        }
    }
}
