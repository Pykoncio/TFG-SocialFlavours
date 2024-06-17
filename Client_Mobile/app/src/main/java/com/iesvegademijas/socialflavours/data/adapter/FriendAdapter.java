package com.iesvegademijas.socialflavours.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.dto.social.FriendShip;
import com.iesvegademijas.socialflavours.data.remote.dto.social.User;

import java.util.ArrayList;

public class FriendAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<User> friendsList;

    private FriendAdapter.FriendAdapterCallBack callback;
    public FriendAdapter(Context context, ArrayList<User> friendsList) {
        super();
        this.context = context;
        this.friendsList = friendsList;
    }


    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        if (friendsList==null) {
            return null;
        }
        else
        {
            return friendsList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        FriendAdapter.FriendWrapper friendWrapper;

        if (item==null)
        {
            friendWrapper = new  FriendAdapter.FriendWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.list_item_friend, parent, false);
            friendWrapper.username = item.findViewById(R.id.friend_username);
            friendWrapper.deleteFriendship = item.findViewById(R.id.ib_delete_friend);
            item.setTag(friendWrapper);
        }
        else
        {
            friendWrapper = (FriendAdapter.FriendWrapper) item.getTag();
        }

        User user = friendsList.get(position);

        friendWrapper.username.setText(user.getUsername());

        friendWrapper.deleteFriendship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.deleteFriendship(position);
            }
        });

        return item;
    }

    static class FriendWrapper {
        TextView username;
        ImageView deleteFriendship;
    }

    public interface FriendAdapterCallBack {
        void deleteFriendship(int position);
    }

    public void setCallback(FriendAdapter.FriendAdapterCallBack callback) {this.callback = callback;}
}
