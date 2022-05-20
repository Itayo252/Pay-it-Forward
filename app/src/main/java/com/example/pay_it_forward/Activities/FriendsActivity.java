package com.example.pay_it_forward.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class FriendsActivity extends AppCompatActivity {
    //signed in user
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        user = (User) getIntent().getSerializableExtra(Utils.USER); //get the user from previous screen

        //setup buttons
        this.<Button>findViewById(R.id.btnPendingFriends).setOnClickListener(v -> startActivity(new Intent(FriendsActivity.this, PendingFriendsActivity.class).putExtra(Utils.USER, user)));
        this.<Button>findViewById(R.id.btnAddFriend).setOnClickListener(v -> addFriend(this.<EditText>findViewById(R.id.etAddFriend).getText().toString()));
        this.<ListView>findViewById(R.id.lvFriendsList).setAdapter(new ArrayAdapter<Friend>(FriendsActivity.this, 0, 0, Utils.getFriendsList(user)) { //setup friends list
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.friend_layout, parent, false);
                ((TextView) view.findViewById(R.id.tvFriendName)).setText(getItem(position).getUsername());
                ((TextView) view.findViewById(R.id.tvFriendPhoneNumber)).setText(getItem(position).getPhoneNumber());
                return view;
            }
        });
    }

    /**
     * get the phone number from the screen, check it and send friendship request
     * @param friendPhoneNumber the phone number to send the request to
     */
    private void addFriend(String friendPhoneNumber) {
        this.<EditText>findViewById(R.id.etAddFriend).setText(""); //reset the input
        if (friendPhoneNumber.length() == 0) {
            return;
        }
        if (ServerUtils.userWithPhoneNumberNotExists(friendPhoneNumber)) {
            Utils.RaiseMessage.raiseUserWithPhoneNumberNotFound(this);
            return;
        }
        ServerUtils.addFriend(user.getPhoneNumber(), friendPhoneNumber);
    }
}