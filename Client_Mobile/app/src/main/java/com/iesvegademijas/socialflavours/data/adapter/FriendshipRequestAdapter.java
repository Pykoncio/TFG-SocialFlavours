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

import java.util.ArrayList;

public class FriendshipRequestAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FriendShip> friendShipRequests;

    private FriendshipRequestAdapter.FriendshipRequestAdapterCallBack callback;
    public FriendshipRequestAdapter(Context context, ArrayList<FriendShip> friendShipRequests) {
        super();
        this.context = context;
        this.friendShipRequests = friendShipRequests;
    }


    @Override
    public int getCount() {
        return friendShipRequests.size();
    }

    @Override
    public Object getItem(int position) {
        if (friendShipRequests==null) {
            return null;
        }
        else
        {
            return friendShipRequests.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        FriendshipRequestAdapter.FriendshipWrapper friendshipWrapper;

        if (item==null)
        {
            friendshipWrapper = new FriendshipRequestAdapter.FriendshipWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.list_item_friendship_request, parent, false);
            friendshipWrapper.container = item.findViewById(R.id.item_container_friendship_request);
            friendshipWrapper.username = item.findViewById(R.id.item_username_friendship_request);
            friendshipWrapper.sentence = item.findViewById(R.id.label_add_new_friend);
            friendshipWrapper.acceptFriendship = item.findViewById(R.id.ib_li_accept_friend);
            friendshipWrapper.declineFriendship = item.findViewById(R.id.ib_li_decline_friend);
            item.setTag(friendshipWrapper);
        }
        else
        {
            friendshipWrapper = (FriendshipRequestAdapter.FriendshipWrapper) item.getTag();
        }

        FriendShip friendShip = friendShipRequests.get(position);

        friendshipWrapper.username.setText(friendShip.getUsernameSender());
        friendshipWrapper.sentence.setText(R.string.add_friend_sentence);

        friendshipWrapper.acceptFriendship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.acceptFriendship(position);
            }
        });

        friendshipWrapper.declineFriendship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.declineFriendship(position);
            }
        });

        return item;
    }

    static class FriendshipWrapper {

        RelativeLayout container;

        TextView username;
        TextView sentence;
        ImageView acceptFriendship;
        ImageView declineFriendship;

    }

    public interface FriendshipRequestAdapterCallBack {
        void acceptFriendship(int position);
        void declineFriendship(int position);
    }

    public void setCallback(FriendshipRequestAdapter.FriendshipRequestAdapterCallBack callback) {this.callback = callback;}
}
