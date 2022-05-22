package com.example.pay_it_forward.Utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class with many utilities method for the code
 */
public class Utils {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String USER = "user";
    public static final String USER_FROM_PHONE_NUMBER = "user_from_phone";
    public static final String FROM = "from";
    public static final String ID = "id";
    public static final String AMOUNT = "amount";
    public static final String OTHER_PHONE_NUMBER = "other_phone_number";
    public static final String TO = "to";

    /**
     * @param user a user ehose friends list will be returned
     * @return the user's friends list
     */
    public static List<Friend> getFriendsList(User user) {
        List<Friend> list = new ArrayList<>();
        JSONArray jsonArray = ServerUtils.getFriendsListOfUser(user); //request friends from server
        for (int i = 0; i < Objects.requireNonNull(jsonArray).length(); i++) {
            try {
                list.add(new Friend(((JSONObject) jsonArray.get(i)).getString(Utils.USERNAME),
                        ((JSONObject) jsonArray.get(i)).getString(Utils.PHONE_NUMBER)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * @param number a number
     * @return true iff the number is valid
     */
    public static boolean isValidNumber(String number) {
        return number.length() > 0 && (Integer.parseInt(number) > 0);
    }

    /**
     * @param user a user whose pending friends list will be returned
     * @return the user's pending friends list
     */
    public static List<Friend.PendingFriend> getPendingFriendsList(User user) {
        List<Friend.PendingFriend> list = new ArrayList<>();
        JSONArray jsonArray = ServerUtils.getPendingFriendsListOfUser(user); // requsting from the servr
        for (int i = 0; i < Objects.requireNonNull(jsonArray).length(); i++) {
            try {
                list.add(new Friend.PendingFriend(((JSONObject) jsonArray.get(i)).getString(Utils.USERNAME),
                        ((JSONObject) jsonArray.get(i)).getString(Utils.PHONE_NUMBER),
                        ((JSONObject) jsonArray.get(i)).getInt(Utils.ID)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<Transfer> getDebtsList(String phoneNumber) { //TODO save on phone instead on remote server
        List<Transfer> list = new ArrayList<>();
        JSONArray jsonArray = ServerUtils.getListOfDebts(phoneNumber); // request list from server
        for (int i = 0; i < Objects.requireNonNull(jsonArray).length(); i++) {
            try {
                list.add(new Transfer((String) Utils.safeGet(jsonArray.getJSONObject(i), Utils.FROM),
                        "",0,
                        (Integer) Utils.safeGet(jsonArray.getJSONObject(i), Utils.AMOUNT)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * An enum for code convenience in new screen opening
     */
    public enum Codes {
        SIGNIN_CODE,
        SIGNUP_CODE
    }


    /**
     * An enum to specify every signin result
     */
    public enum SigninFail {
        INVALID_PHONE_NUMBER,
        NO_SUCH_PHONE,
        WRONG_PASSWORD,
        SUCCESS
    }


    /**
     * An enum to specify every signup result
     */
    public enum SignupFail {
        INVALID_PHONE_NUMBER,
        USER_EXISTS,
        NOT_COOL_USERNAME,
        PASSWORD_NOT_STRONG_ENOUGH,
        PASSWORD_DOES_NOT_MATCH,
        SUCCESS
    }

    /**
     * wrapping a json get with try catch
     * @param json the json object
     * @param key the key to extract its value from the object
     * @return the object value assigned to the key
     */
    public static Object safeGet(JSONObject json, String key) {
        try {
            return json.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isStrongPassword(String password) {
        return password.length() > 8; //TODO implement
    }

    public static boolean notValidPhoneNumber(String phoneNumber) {
        return !PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }

    /**
     * grouping all toast raising methods
     */
    public static class RaiseMessage {

        public static void raiseWrongPassword(Context context) {
            Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show();
        }

        public static void raiseUserWithPhoneNumberExists(Context context) {
            Toast.makeText(context, "User with this phone number already exists, consider signing in", Toast.LENGTH_LONG).show();
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

        public static void raiseNotValidAmount(Context context) {
            Toast.makeText(context, "Not a valid amount of money!", Toast.LENGTH_SHORT).show();
        }

        public static void raiseNoUserWasChosen(Context context) {
            Toast.makeText(context, "No user was chosen!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * applying checks and after success signing in the user with given credentials
     * @param phoneNumber user's phone number
     * @param password users password
     * @return signFail enum according to success/failure
     */
    public static SigninFail tryToSignin(String phoneNumber, String password) {
        if (notValidPhoneNumber(phoneNumber)) {
            return SigninFail.INVALID_PHONE_NUMBER;
        } else if (ServerUtils.userWithPhoneNumberNotExists(phoneNumber)) {
            return SigninFail.NO_SUCH_PHONE;
        } else if (!ServerUtils.correctPassword(phoneNumber, password)) {
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
