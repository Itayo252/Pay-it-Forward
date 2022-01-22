package com.example.pay_it_forward.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private String username, phoneNumber, password;

    //TODO implement
    public User(String username, String phoneNumber, String password) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(JSONObject userJSON) {
        this((String) Utils.safeGet(userJSON, Utils.USERNAME), (String) Utils.safeGet(userJSON, Utils.PHONE_NUMBER), (String) Utils.safeGet(userJSON, Utils.PASSWORD));
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONObject toJSON() {
        try {
            return new JSONObject().put(Utils.USERNAME, getUsername()).put(Utils.PHONE_NUMBER, getPhoneNumber()).put(Utils.PASSWORD, getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
