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

    private User user;
    private ArrayAdapter<Friend.PendingFriend> pendingFriendArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friends);

        user = (User) getIntent().getSerializableExtra(Utils.USER);

        pendingFriendArrayAdapter = new ArrayAdapter<Friend.PendingFriend>(this, 0, 0, Utils.getPendingFriendsList(user)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(PendingFriendsActivity.this).inflate(R.layout.pending_friend_layout, parent, false);
                view.<TextView>findViewById(R.id.tvPendingFriendName).setText(getItem(position).getUsername());
                view.<TextView>findViewById(R.id.tvPendingFriendPhoneNumber).setText(getItem(position).getPhoneNumber());
                view.<Button>findViewById(R.id.btnPendingFriendAccept).setOnClickListener(v -> updateFriendshipRequest(getItem(position).getRequestID(), true));
                view.<Button>findViewById(R.id.btnPendingFriendDeny).setOnClickListener(v -> updateFriendshipRequest(getItem(position).getRequestID(), false));
                return view;
            }
        };
        this.<ListView>findViewById(R.id.lvPendingFriendsList).setAdapter(pendingFriendArrayAdapter);
//        this.<ListView>findViewById(R.id.lvPendingFriendsList).setOnItemLongClickListener(); TODO add deletion of friends
    }

    private void updateFriendshipRequest(String requestID, boolean isAccepted) {
        pendingFriendArrayAdapter.remove(getFriendByRequestId(requestID));
        if (isAccepted) {
            ServerUtils.acceptFriendshipRequest(requestID);
        } else {
            ServerUtils.denyFriendshipRequest(requestID);
        }
    }

    private Friend.PendingFriend getFriendByRequestId(String requestId) {
        for (int i = 0; i < pendingFriendArrayAdapter.getCount(); i++) {
            if (pendingFriendArrayAdapter.getItem(i).getRequestID().equals(requestId)) {
                return pendingFriendArrayAdapter.getItem(i);
            }
        }
        return null;
    }
}