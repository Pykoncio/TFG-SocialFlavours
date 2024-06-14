package com.iesvegademijas.socialflavours.presentation.home.fragments.outbox;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iesvegademijas.socialflavours.R;
import com.iesvegademijas.socialflavours.data.remote.ApiOperator;
import com.iesvegademijas.socialflavours.presentation.home.HomePage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SendFriendshipRequest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendFriendshipRequest extends Fragment {

    private View myView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1; // Long id of the current user
    private String mParam2;

    public SendFriendshipRequest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendFriendshipRequest.
     */
    // TODO: Rename and change types and number of parameters
    public static SendFriendshipRequest newInstance(String param1, String param2) {
        SendFriendshipRequest fragment = new SendFriendshipRequest();
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

        myView = inflater.inflate(R.layout.fragment_send_friendship_request, container, false);

        // Find the toolbar from the inflated layout
        Toolbar toolbar = myView.findViewById(R.id.toolbar_send_friendship_request);

        // Set the toolbar as the SupportActionBar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("Outgoing Friendships Requests");
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

        Button sendRequest = myView.findViewById(R.id.send_friendship_request);

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your create method here
                sendFriendshipRequest();
            }
        });

        return myView;
    }

    private void sendFriendshipRequest()
    {
        EditText etUsername = myView.findViewById(R.id.send_request_to_user);
        String username = etUsername.getText().toString();

        if (username.isEmpty())
        {
            etUsername.setError(getResources().getString(R.string.compulsory_field));
        }
        else
        {
            Button btSendRequest = myView.findViewById(R.id.send_friendship_request);
            ProgressBar pbSendRequest = myView.findViewById(R.id.pb_send_friendship_request);
            btSendRequest.setClickable(false);
            btSendRequest.setEnabled(false);
            pbSendRequest.setVisibility(View.VISIBLE);

            if (isNetworkAvailable()) {
                String url = getResources().getString(R.string.main_url) + "friendshipapi/newRequest" + mParam1 + "To"+ username;
                sendTask(url);
            } else {
                showError("error.IOException");
            }
        }
    }

    private void sendTask(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ApiOperator apiOperator= ApiOperator.getInstance();
            String result = apiOperator.postText(url);

            handler.post(() -> {
                Button btCreate= myView.findViewById(R.id.send_friendship_request);
                ProgressBar pbCreate=(ProgressBar) myView.findViewById(R.id.pb_send_friendship_request);
                pbCreate.setVisibility(View.GONE);
                btCreate.setEnabled(true);
                btCreate.setClickable(true);
                long idCreated;
                try{
                    idCreated=Long.parseLong(result);
                }catch(NumberFormatException ex){
                    idCreated=-1;
                }
                if(idCreated>0){
                    showError("Friendship Request Sent Successfully!");
                }
                else {
                    showError("error.Unknown");
                }
            });
        });
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
        else if (error.equals("Friendship Request Sent Successfully!"))
        {
            message = res.getString(R.string.friendshipRequest);
            duration = Toast.LENGTH_LONG;
        }
        else {
            message = res.getString(R.string.error_unknown);
            duration = Toast.LENGTH_SHORT;
        }
        Toast toast = Toast.makeText(myView.getContext(), message, duration);
        toast.show();
    }
}