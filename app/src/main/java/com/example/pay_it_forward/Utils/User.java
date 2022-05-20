package com.example.pay_it_forward.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * A class to hold all information of a user
 */
public class User implements Serializable {
    private String username, phoneNumber;

    //TODO implement and heavily rethink the fucking idea of plaintext passwords you moron
    public User(String username, String phoneNumber) {
        this.username = username;
        this.phoneNumber = phoneNumber;

    }

    /**
     *
     * @param userJSON a json that holds the information of the user to be created
     */
    public User(JSONObject userJSON) {
        this((String) Utils.safeGet(userJSON, Utils.USERNAME), (String) Utils.safeGet(userJSON, Utils.PHONE_NUMBER));
    }

    //getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public JSONObject toJSON() {
        try {
            return new JSONObject().put(Utils.USERNAME, getUsername()).put(Utils.PHONE_NUMBER, getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * not all users instances need to hold the password, but for those who needs it, this class exists
     */
    public static class UserWithPassword extends User {
        private String password;

        public UserWithPassword(String username, String phoneNumber, String password) {
            super(username, phoneNumber);
            this.password = password;
        }

        public UserWithPassword(JSONObject userJSON) {
            super(userJSON);
            this.password = (String) Utils.safeGet(userJSON, Utils.PASSWORD);
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public JSONObject toJSON() {
            try {
                return super.toJSON().put(Utils.PASSWORD, getPassword());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
