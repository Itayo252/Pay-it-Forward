package com.example.pay_it_forward.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.pay_it_forward.R;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        this.<Button>findViewById(R.id.btnPendingFriends).setOnClickListener(v -> startActivity(new Intent(FriendsActivity.this, PendingFriendsActivity.class)));
    }
}