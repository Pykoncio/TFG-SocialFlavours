package com.iesvegademijas.socialflavours.presentation.home.fragments.inbox;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.adapter.FriendshipRequestAdapter;
import com.iesvegademijas.socialflavours.data.adapter.RecipeAdapter;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.data.remote.dto.foodRelated.Recipe;
import com.iesvegademijas.socialflavours.data.remote.dto.social.FriendShip;
import com.iesvegademijas.socialflavours.presentation.home.HomePage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomingFriendshipRequests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomingFriendshipRequests extends Fragment implements FriendshipRequestAdapter.FriendshipRequestAdapterCallBack {

    private View myView;
    private static final int MAX_RETRIES = 5;

    private ListView listView;
    private ArrayList<FriendShip> friendshipRequestsModels;
    private FriendshipRequestAdapter friendshipRequestsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomingFriendshipRequests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomingFriendshipRequests.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomingFriendshipRequests newInstance(String param1, String param2) {
        IncomingFriendshipRequests fragment = new IncomingFriendshipRequests();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_incoming_friendship_requests, container, false);

        listView = myView.findViewById(R.id.incoming_friendship_requests_list);

        // Find the toolbar from the inflated layout
        Toolbar toolbar = myView.findViewById(R.id.toolbar_shoppingLists);

        // Set the toolbar as the SupportActionBar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("Incoming Friendship Requests");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_hamburguer_24);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof HomePage) {
                    ((HomePage) getActivity()).drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        setUpFriendshipRequests();

        return myView;
    }

    private void setUpFriendshipRequests()
    {
        String url = getResources().getString(R.string.main_url) + "friendshipapi/pending" + mParam1;
        if (isNetworkAvailable()) {
            for (int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
                try {
                    getListTask(url);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (retryCount < MAX_RETRIES) {
                        showError(e.getMessage());
                    } else {
                        showError("error.connection");
                    }
                }
            }
        } else {
            showError("error.IOException");
        }
    }

    private void getListTask(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiOperator apiOperator= ApiOperator.getInstance();
                    String result = apiOperator.getString(url);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(result.equalsIgnoreCase("error.IOException")||
                                    result.equals("error.OKHttp")) {
                                showError(result);
                            }
                            else if(result.equalsIgnoreCase("null")){
                                showError("error.Unknown");
                            } else{
                                resetList(result);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showError(e.getMessage());
                        }
                    });
                }

            }
        });
    }

    private void resetList(String result)
    {
        try
        {
            JSONArray friendshipRequests = new JSONArray(result);

            if (friendshipRequestsModels == null)
            {
                friendshipRequestsModels = new ArrayList<>();
            }
            else
            {
                friendshipRequestsModels.clear();
            }

            for (int i = 0; i < friendshipRequests.length(); i++)
            {
                JSONObject friendshipRequestObject = friendshipRequests.getJSONObject(i);
                FriendShip friendShip = new FriendShip();
                friendShip.fromJSON(friendshipRequestObject);
                friendshipRequestsModels.add(friendShip);
            }

            if (friendshipRequestsAdapter==null)
            {
                friendshipRequestsAdapter = new FriendshipRequestAdapter(getContext(), friendshipRequestsModels);
                friendshipRequestsAdapter.setCallback(this);
                listView.setAdapter(friendshipRequestsAdapter);
            }
            else
            {
                friendshipRequestsAdapter.notifyDataSetChanged();
            }

            TextView tvIncomingFriendshipRequests = myView.findViewById(R.id.tv_empty_list_incoming_friendship_requests);

            if (friendshipRequestsModels.isEmpty())
            {
                tvIncomingFriendshipRequests.setVisibility(View.VISIBLE);
            }
            else
            {
                tvIncomingFriendshipRequests.setVisibility(View.GONE);
            }
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) {
                return false;
            } else {
                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                return (actNw != null) && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    private void showError(String error) {
        String message;
        Resources res = getResources();
        int duration;
        if (error.equals("error.IOException")||error.equals("error.OKHttp")) {
            message = res.getString(R.string.error_connection);
            duration = Toast.LENGTH_SHORT;
        }
        else if(error.equals("error.undelivered")){
            message = res.getString(R.string.error_undelivered);
            duration = Toast.LENGTH_LONG;
        }
        else {
            message = res.getString(R.string.error_unknown);
            duration = Toast.LENGTH_SHORT;
        }
        Toast toast = Toast.makeText(getContext(), message, duration);
        toast.show();
    }

    @Override
    public void acceptFriendship(int position) {
        if (friendshipRequestsModels!=null)
        {
            if (friendshipRequestsModels.size() > 0)
            {
                FriendShip friendShip = friendshipRequestsModels.get(position);

                if (isNetworkAvailable()) {
                    String url = getResources().getString(R.string.main_url) + friendShip.getId_friendship() + "/accept";
                    sendTask(url);
                } else {
                    showError("error.IOException");
                }
            }
        }
    }

    private void sendTask(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOperator=ApiOperator.getInstance();
                String result = apiOperator.putText(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long createdId;
                        try{
                            createdId=Long.parseLong(result);
                        }catch(NumberFormatException ex){
                            createdId=-1;
                        }
                        if(createdId>0){
                            setUpFriendshipRequests();
                        }
                        else {
                            showError("error.Unknown");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void declineFriendship(int position) {

        androidx.appcompat.app.AlertDialog diaBox = AskOption(position);
        diaBox.show();
    }

    private androidx.appcompat.app.AlertDialog AskOption(final int position)
    {
        androidx.appcompat.app.AlertDialog myQuittingDialogBox =new AlertDialog.Builder(myView.getContext())
                .setTitle(R.string.decline_friendship_request)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteRequest(position);//Si confirmamos eliminamos el usuario
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void deleteRequest(int position)
    {
        if (friendshipRequestsModels!=null)
        {
            if (friendshipRequestsModels.size() > 0)
            {
                FriendShip friendShip = friendshipRequestsModels.get(position);

                if (isNetworkAvailable()) {
                    String url = getResources().getString(R.string.main_url) + friendShip.getId_friendship() + "/decline";
                    deleteTask(url);
                } else {
                    showError("error.IOException");
                }
            }
        }
    }

    private void deleteTask(String url){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ApiOperator apiOperator= ApiOperator.getInstance();
                String result = apiOperator.deleteTask(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(result.equalsIgnoreCase("error.IOException")||
                                result.equals("error.OKHttp")) {
                            showError(result);
                        }
                        else if(result.equalsIgnoreCase("null")){
                            showError("error.Unknown");
                        }
                        else{
                            setUpFriendshipRequests();
                        }
                    }
                });
            }
        });
    }
}