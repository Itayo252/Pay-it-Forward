package com.example.pay_it_forward.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.pay_it_forward.R;
import com.example.pay_it_forward.Utils.User;
import com.example.pay_it_forward.Utils.Utils;

public class MainActivity extends AppCompatActivity {

    User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        signin();
        setupButtons();
    }


    private void updateName() {
        this.<TextView>findViewById(R.id.tvHello).setText(String.format(getString(R.string.helloMainScreen), currUser.getUsername()));
    }

    private void setupButtons() {
        this.<Button>findViewById(R.id.btnTransfer).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TransferActivity.class)));
        //this.<Button>findViewById(R.id.btnNetwork).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NetworkActivity.class))); //TODO implement
        this.<Button>findViewById(R.id.btnFriends).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FriendsActivity.class)));
    }

    private void signin() {
        if (savedCredentials()) {
            if (Utils.tryToSignin(Utils.getSavedPhoneNumber(this), Utils.getSavedPassword(this)) == Utils.SigninFail.SUCCESS) {
                currUser = new User("", Utils.getSavedPhoneNumber(this),"");//TODO re-add updateUser(Utils.getSavedPhoneNumber(this));
                updateName();
                return;
            }
        }
        openSigninScreen();
    }

    private void updateUser(String phoneNumber) {
        currUser = Utils.getUserFromPhone(phoneNumber);
    }

    private void openSigninScreen() {
        startActivityForResult(new Intent(this, SigninActivity.class), Utils.Codes.SIGNIN_CODE.ordinal());
    }

    private boolean savedCredentials() {
        return Utils.getSavedPhoneNumber(this).length() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.Codes.SIGNIN_CODE.ordinal() && resultCode == RESULT_OK) {
            currUser = (User) data.getSerializableExtra(Utils.USER);//TODO re-add updateUser(data.getStringExtra(Utils.PHONE_NUMBER));
            updateName();
        }
    }
}