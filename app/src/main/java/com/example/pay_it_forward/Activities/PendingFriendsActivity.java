package com.example.pay_it_forward.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pay_it_forward.R;
import com.example.pay_it_forward.Utils.Friend;
import com.example.pay_it_forward.Utils.ServerUtils;
import com.example.pay_it_forward.Utils.User;
import com.example.pay_it_forward.Utils.Utils;

public class PendingFriendsActivity extends AppCompatActivity {
    //signed in user
    private User user;
    //adapter of listview
    private ArrayAdapter<Friend.PendingFriend> pendingFriendArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friends);

        //get user from previous screen
        user = (User) getIntent().getSerializableExtra(Utils.USER);

        //setup adapter for list view
        pendingFriendArrayAdapter = new ArrayAdapter<Friend.PendingFriend>(this, 0, 0, Utils.getPendingFriendsList(user)) { // request and parsing of the list
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(PendingFriendsActivity.this).inflate(R.layout.pending_friend_layout, parent, false);
                view.<TextView>findViewById(R.id.tvPendingFriendName).setText(getItem(position).getUsername());
                view.<TextView>findViewById(R.id.tvPendingFriendPhoneNumber).setText(getItem(position).getPhoneNumber());
                view.<Button>findViewById(R.id.btnPendingFriendAccept).setOnClickListener(v -> updateFriendshipRequest(getItem(position).getRequestID(), true)); //TODO debug
                view.<Button>findViewById(R.id.btnPendingFriendDeny).setOnClickListener(v -> updateFriendshipRequest(getItem(position).getRequestID(), false));
                return view;
            }
        };
        this.<ListView>findViewById(R.id.lvPendingFriendsList).setAdapter(pendingFriendArrayAdapter);
//        this.<ListView>findViewById(R.id.lvPendingFriendsList).setOnItemLongClickListener(); TODO add deletion of friends
    }

    /**
     * according to what was requested notify the server
     * @param requestID the request ID to handle
     * @param isAccepted has the user accepted the offer
     */
    private void updateFriendshipRequest(int requestID, boolean isAccepted) {
        pendingFriendArrayAdapter.remove(getFriendByRequestId(requestID));
        if (isAccepted) {
            ServerUtils.acceptFriendshipRequest(requestID);
        } else {
            ServerUtils.denyFriendshipRequest(requestID);
        }
    }

    /**
     * get the item with the desired requestID
     * @param requestId the id of the request to be found
     * @return the friend to be with this ID
     */
    private Friend.PendingFriend getFriendByRequestId(int requestId) {
        for (int i = 0; i < pendingFriendArrayAdapter.getCount(); i++) {
            if (pendingFriendArrayAdapter.getItem(i).getRequestID() == requestId) {
                return pendingFriendArrayAdapter.getItem(i);
            }
        }
        return null;
    }
}