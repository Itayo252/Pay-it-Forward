package com.example.pay_it_forward.Utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {


    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PHONE_NUMBER = "phone number";
    public static final String USER = "user";
    private static final String USER_FROM_PHONE_NUMBER = "user from phone";

    public static User getUserFromPhone(String phoneNumber) {
        return new User(getUserJSONFromServerFromPhoneNumber(phoneNumber));
    }

    private static JSONObject getUserJSONFromServerFromPhoneNumber(String phoneNumber) {
        try {
            return ServerUtils.request(Utils.USER_FROM_PHONE_NUMBER, new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object safeGet(JSONObject json, String key) {
        try {
            return json.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public enum Codes {
        SIGNIN_CODE,
        SIGNUP_CODE

    }

    public enum SigninFail {
        INVALID_PHONE_NUMBER,
        NO_SUCH_PHONE,
        WRONG_PASSWORD,
        SUCCESS

    }

    public enum SignupFail {
        INVALID_PHONE_NUMBER,
        USER_EXISTS,
        NOT_COOL_USERNAME,
        PASSWORD_NOT_STRONG_ENOUGH,
        PASSWORD_DOES_NOT_MATCH,
        SUCCESS

    }
    public static boolean isStrongPassword(String password) {
        return true; //TODO implement
    }

    public static boolean userWithPhoneNumberNotExists(String phoneNumber) {
        try {
            return !ServerUtils.request("user with phone number exists", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber)).getBoolean("return value"); //TODO implement
        } catch (JSONException e) {
            e.printStackTrace();
            return false; //TODO revisit
        }
    }

    public static boolean correctPassword(String phoneNumber, String password) {
        try {
            return ServerUtils.request("verify password for phone", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber).put(Utils.PASSWORD, password)).getBoolean("return value"); //TODO implement
        } catch (JSONException e) {
            e.printStackTrace();
            return true; //TODO revisit
        }
    }

    public static boolean notValidPhoneNumber(String phoneNumber) {
        return !PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }

    public static class RaiseMessage {

        public static void raiseWrongPassword(Context context) {
            Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show();
        }

        public static void raiseUserWithPhoneNumberExists(Context context) {
            Toast.makeText(context, "User with this phone number already exists, consider singing in", Toast.LENGTH_LONG).show();
        }

        public static void raiseNotValidPhoneNumber(Context context) {
            Toast.makeText(context, "This not a valid phone number!", Toast.LENGTH_SHORT).show();
        }

        public static void raiseUserWithPhoneNumberNotFound(Context context) {
            Toast.makeText(context, "No user with this phone number exists!", Toast.LENGTH_SHORT).show();
        }

        public static void raisePasswordDoesNotMatch(Context context) {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }

        public static void raisePasswordNotStrongEnough(Context context) {
            Toast.makeText(context, "Weak password!", Toast.LENGTH_SHORT).show();
        }

        public static void raiseNotCoolUserName(Context context) {
            Toast.makeText(context, "Not cool! try again :)", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getNameByPhone(String phoneNumber) {
        try {
            return ServerUtils.request("username for phone number", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber)).getString("return value"); //TODO implement
        } catch (JSONException e) {
            e.printStackTrace();
            return null; //TODO revisit
        }
    }

    public static SigninFail tryToSignin(String phoneNumber, String password) {
        if (notValidPhoneNumber(phoneNumber)) {
            return SigninFail.INVALID_PHONE_NUMBER;
        } else if (userWithPhoneNumberNotExists(phoneNumber)) {
            return SigninFail.NO_SUCH_PHONE;
        } else if (!correctPassword(phoneNumber, password)) {
            return SigninFail.WRONG_PASSWORD;
        } else {
            return SigninFail.SUCCESS;
        }
    }

    public static String getSavedDefaultValue(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static String getSavedPassword(Context context) {
        return getSavedDefaultValue(context, Utils.PASSWORD);
    }

    public static String getSavedPhoneNumber(Context context) {
        return getSavedDefaultValue(context, Utils.PHONE_NUMBER);
    }
}
