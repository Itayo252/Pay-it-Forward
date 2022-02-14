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

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        user = (User) getIntent().getSerializableExtra(Utils.USER);

        this.<Button>findViewById(R.id.btnPendingFriends).setOnClickListener(v -> startActivity(new Intent(FriendsActivity.this, PendingFriendsActivity.class).putExtra(Utils.USER, user)));
        this.<Button>findViewById(R.id.btnAddFriend).setOnClickListener(v -> addFriend(this.<EditText>findViewById(R.id.etAddFriend).getText().toString()));
        ArrayAdapter<Friend> friendArrayAdapter = new ArrayAdapter<Friend>(this, 0, 0, Utils.getFriendsList(user)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.friend_layout, parent, false);
                ((TextView) view.findViewById(R.id.tvFriendName)).setText(getItem(position).getUsername());
                ((TextView) view.findViewById(R.id.tvFriendPhoneNumber)).setText(getItem(position).getPhoneNumber());
                return view;
            }
        };
        this.<ListView>findViewById(R.id.lvFriendsList).setAdapter(friendArrayAdapter);
    }

    private void addFriend(String friendPhoneNumber) {
        this.<EditText>findViewById(R.id.etAddFriend).setText("");
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