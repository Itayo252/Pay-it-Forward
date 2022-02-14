package com.example.pay_it_forward.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pay_it_forward.R;
import com.example.pay_it_forward.Utils.ServerUtils;
import com.example.pay_it_forward.Utils.User;
import com.example.pay_it_forward.Utils.Utils;

import java.util.Random;


public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.<EditText>findViewById(R.id.etSignUpPhoneNumber).setText(getIntent().getStringExtra(Utils.PHONE_NUMBER));
        this.<EditText>findViewById(R.id.etSignUpPassword).setText(getIntent().getStringExtra(Utils.PASSWORD));
        this.<EditText>findViewById(R.id.etSignupPasswordVer).setText(getIntent().getStringExtra(Utils.PASSWORD));
        this.<Button>findViewById(R.id.btnSignUp).setOnClickListener(v -> signup());

    }

    private void signup() {
        Utils.SignupFail fail = checkFields();
        if (fail == Utils.SignupFail.INVALID_PHONE_NUMBER) {
            Utils.RaiseMessage.raiseNotValidPhoneNumber(this);
        } else if (fail == Utils.SignupFail.USER_EXISTS) {
            Utils.RaiseMessage.raiseUserWithPhoneNumberExists(this);
        } else if (fail == Utils.SignupFail.PASSWORD_DOES_NOT_MATCH) {
            Utils.RaiseMessage.raisePasswordDoesNotMatch(this);
        } else if (fail == Utils.SignupFail.PASSWORD_NOT_STRONG_ENOUGH) {
            Utils.RaiseMessage.raisePasswordNotStrongEnough(this);
        } else if (fail == Utils.SignupFail.NOT_COOL_USERNAME) {
            Utils.RaiseMessage.raiseNotCoolUserName(this);
        } else if (fail == Utils.SignupFail.SUCCESS) {
            User.UserWithPassword user = new User.UserWithPassword(getUsername(), getPhoneNumber(), getPassword()); //TODO change according to final implementation
            ServerUtils.registerNewUser(user);
            closeSignup();
        }
    }

    private Utils.SignupFail checkFields() { //order of ifs matter!
        if (Utils.notValidPhoneNumber(getPhoneNumber())) {
            return Utils.SignupFail.INVALID_PHONE_NUMBER;
        }
        if (!ServerUtils.userWithPhoneNumberNotExists(getPhoneNumber())) {
            return Utils.SignupFail.USER_EXISTS;
        }
        if (!getPassword().equals(getVerifyPassword())) {
            return Utils.SignupFail.PASSWORD_DOES_NOT_MATCH;
        }
        if (!Utils.isStrongPassword(getPassword())) {
            return Utils.SignupFail.PASSWORD_NOT_STRONG_ENOUGH;
        }
        if (notCoolUsername()) {
            return Utils.SignupFail.NOT_COOL_USERNAME;
        }
        return Utils.SignupFail.SUCCESS;
    }

    private boolean notCoolUsername() {
        return new Random().nextInt(2) > 0;
    }

    private void closeSignup() {
        getIntent().putExtra(Utils.USER, getUser());
        setResult(RESULT_OK, getIntent());
        finish();
    }

    private User.UserWithPassword getUser() {
        return new User.UserWithPassword(getUsername(), getPhoneNumber(), getPassword());
    }

    private String getPhoneNumber() {
        return this.<EditText>findViewById(R.id.etSignUpPhoneNumber).getText().toString();
    }

    private String getUsername() {
        return this.<EditText>findViewById(R.id.etSignUpUsername).getText().toString();
    }

    @NonNull
    private String getPassword() {
        return this.<EditText>findViewById(R.id.etSignUpPassword).getText().toString();
    }

    private String getVerifyPassword() {
        return this.<EditText>findViewById(R.id.etSignupPasswordVer).getText().toString();
    }
}