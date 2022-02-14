package com.example.pay_it_forward.Activities;

import android.content.Intent;
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
import androidx.core.view.WindowCompat;

import com.example.pay_it_forward.R;
import com.example.pay_it_forward.Utils.ServerUtils;
import com.example.pay_it_forward.Utils.Transfer;
import com.example.pay_it_forward.Utils.TransferUtils;
import com.example.pay_it_forward.Utils.User;
import com.example.pay_it_forward.Utils.Utils;

public class MainActivity extends AppCompatActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        signin();
        setupButtons();
        this.<ListView>findViewById(R.id.lvTransferHistory).setAdapter(new ArrayAdapter<Transfer>(this, 0, 0, TransferUtils.getRecentTransfers(user)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.transfer, parent, false);
                view.<TextView>findViewById(R.id.tvFromTo).setText(getItem(position).getFrom().equals(MainActivity.this.user.getPhoneNumber()) ? "To: " + getItem(position).getTo() : "From: " + getItem(position).getFrom());
                view.<TextView>findViewById(R.id.tvAmount).setText(String.valueOf(getItem(position).getAmount()));
                return view;
            }
        });
    }


    private void updateName() {
        this.<TextView>findViewById(R.id.tvHello).setText(String.format(getString(R.string.helloMainScreen), user.getUsername()));
    }

    private void setupButtons() {
        this.<Button>findViewById(R.id.btnTransfer).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TransferActivity.class).putExtra(Utils.USER, user)));
        //this.<Button>findViewById(R.id.btnNetwork).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NetworkActivity.class))); //TODO implement
        this.<Button>findViewById(R.id.btnFriends).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FriendsActivity.class).putExtra(Utils.USER, user)));
    }

    private void signin() {
        if (savedCredentials()) {
            if (Utils.tryToSignin(Utils.getSavedPhoneNumber(this), Utils.getSavedPassword(this)) == Utils.SigninFail.SUCCESS) {
                updateUser(Utils.getSavedPhoneNumber(this));
                updateName();
                return;
            }
        }
        openSigninScreen();
    }

    private void updateUser(String phoneNumber) {
        user = ServerUtils.getUserFromPhone(phoneNumber);
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
            assert data != null;
            user = (User) data.getSerializableExtra(Utils.USER);
            updateName();
        }
    }
}