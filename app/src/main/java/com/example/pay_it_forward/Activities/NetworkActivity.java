package com.example.pay_it_forward.Activities;

import android.os.Bundle;

import com.example.pay_it_forward.Utils.ServerUtils;
import com.example.pay_it_forward.Utils.Transfer;
import com.example.pay_it_forward.Utils.User;
import com.example.pay_it_forward.Utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.pay_it_forward.R;

public class NetworkActivity extends AppCompatActivity {

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        user = (User) getIntent().getSerializableExtra(Utils.USER); //get the user from previous screen

        this.<ListView>findViewById(R.id.lvTransfersList).setAdapter(new ArrayAdapter<Transfer>(this, 0,0, Utils.getDebtsList(user.getPhoneNumber())){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(NetworkActivity.this).inflate(R.layout.transfer, parent, false);
                view.<TextView>findViewById(R.id.tvFromTo).setText(getItem(position).getFrom());
                view.<TextView>findViewById(R.id.tvAmount).setText(String.valueOf(getItem(position).getAmount()));
                return view;
            }
        });
    }
}