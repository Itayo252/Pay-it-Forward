package com.example.pay_it_forward.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pay_it_forward.R;
import com.example.pay_it_forward.Utils.ServerUtils;
import com.example.pay_it_forward.Utils.User;
import com.example.pay_it_forward.Utils.Utils;

public class SigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //setup buttons
        this.<Button>findViewById(R.id.btnGotoSignup).setOnClickListener(v -> signup());
        this.<Button>findViewById(R.id.btnSignin).setOnClickListener(v -> signin(getPhoneNumber(), getPassword()));
    }


    /**
     * setup and open signup screen
     */
    private void signup() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(Utils.PHONE_NUMBER, getPhoneNumber());
        intent.putExtra(Utils.PASSWORD, getPassword());
        startActivityForResult(intent, Utils.Codes.SIGNUP_CODE.ordinal());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.Codes.SIGNUP_CODE.ordinal() && resultCode == RESULT_OK) {
            closeSignin((User) data.getSerializableExtra(Utils.USER)); // if the signup was successful close the login and login with the created user credentials
        }
    }

    @NonNull
    private String getPhoneNumber() {
        return this.<EditText>findViewById(R.id.etSigninPhoneNumber).getText().toString();
    }

    @NonNull
    private String getPassword() {
        return this.<EditText>findViewById(R.id.etSigninPassword).getText().toString();
    }

    private void signin(String phoneNumber, String password) {
        Utils.SigninFail returnCode = Utils.tryToSignin(phoneNumber, password); // try to signin with the server
        //handle return code with appropriate massage or signin
        if (Utils.SigninFail.INVALID_PHONE_NUMBER == returnCode) {
            Utils.RaiseMessage.raiseNotValidPhoneNumber(this);
        } else if (Utils.SigninFail.NO_SUCH_PHONE == returnCode) {
            Utils.RaiseMessage.raiseUserWithPhoneNumberNotFound(this);
        } else if (Utils.SigninFail.WRONG_PASSWORD == returnCode) {
            Utils.RaiseMessage.raiseWrongPassword(this);
        } else if (Utils.SigninFail.SUCCESS == returnCode) {
            if (isRememberMeChecked()) saveCreds();
            closeSignin(new User(ServerUtils.getNameByPhone(phoneNumber), phoneNumber));
        }
    }

    private boolean isRememberMeChecked() {
        return this.<CheckBox>findViewById(R.id.cbRememberMe).isChecked();
    }

    private void saveCreds() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Utils.PHONE_NUMBER, getPhoneNumber()).putString(Utils.PASSWORD, getPassword()).apply();
    }

    /**
     * after signin was successful due to signup or user exists close the screen and sign in with the user
     * @param user the user to be logged in
     */
    private void closeSignin(User user) {
        getIntent().putExtra(Utils.USER, user);
        setResult(Activity.RESULT_OK, getIntent());
        finish();
    }


    /**
     * DO NOT LET THE USER GO BACK TO MAIN SCREEN IF NOT SIGNED IN
     */
    @Override
    public void onBackPressed() { //muy importante
        moveTaskToBack(true);
    }

}