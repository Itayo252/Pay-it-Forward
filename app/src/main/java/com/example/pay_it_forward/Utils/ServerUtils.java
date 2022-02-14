package com.example.pay_it_forward.Utils;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ServerUtils {
    private static final String HOST = "192.168.1.207"; //TODO change to real host and port
    private static final int PORT = 6666;

    public static void registerNewUser(User.UserWithPassword user) {
        request("register user", user.toJSON()); //TODO maybe add return codes
    }

    static JSONObject request(String requestName, JSONObject parameters) {
        try {
            return send(new JSONObject().put("type", "request").put("request type", requestName).put("parameters", parameters));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject send(JSONObject jsonObject) {
        try {
            return new SocketTask().execute(jsonObject).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static JSONObject getUserJSONFromPhoneNumber(String phoneNumber) {
        try {
            return (JSONObject) request(Utils.USER_FROM_PHONE_NUMBER, new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber)).get("return value");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    static JSONArray getPendingTransfersToUser(User user) throws JSONException {
        return request("get pending transfers", new JSONObject().put(Utils.PHONE_NUMBER, user.getPhoneNumber())).getJSONArray("return value");
    }

    public static User getUserFromPhone(String phoneNumber) {
        return new User(getUserJSONFromPhoneNumber(phoneNumber));
    }

    public static boolean userWithPhoneNumberNotExists(String phoneNumber) {
        try {
            return !request("user with phone number exists", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber)).getBoolean("return value");
        } catch (JSONException e) {
            e.printStackTrace();
            return false; //TODO revisit
        }
    }

    public static boolean correctPassword(String phoneNumber, String password) {
        try {
            return request("verify password for phone", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber).put(Utils.PASSWORD, password)).getBoolean("return value");
        } catch (JSONException e) {
            e.printStackTrace();
            return true; //TODO revisit
        }
    }

    public static String getNameByPhone(String phoneNumber) {
        try {
            return request("username for phone number", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber)).getString("return value");
        } catch (JSONException e) {
            e.printStackTrace();
            return null; //TODO revisit
        }
    }

    public static void addFriend(String phoneNumber, String friendPhoneNumber) {
        try {
            request("username for phone number", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber).put(Utils.OTHER_PHONE_NUMBER, friendPhoneNumber)).getString("return value");
        } catch (JSONException e) {
            e.printStackTrace(); //TODO revisit
        }
    }

    public static JSONArray getFriendsListOfUser(User user) {
        try {
            return request("get friends list", new JSONObject().put(Utils.PHONE_NUMBER, user.getPhoneNumber())).getJSONArray("return value");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void acceptFriendshipRequest(String requestID) {
        updateRequest(requestID, "update friendship request", true);
    }

    public static void denyFriendshipRequest(String requestID) {
        updateRequest(requestID, "update friendship request", false);
    }

    public static void updateRequest(String requestID, String s, boolean b) {
        try {
            request(s, new JSONObject().put(requestID, b));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getPendingFriendsListOfUser(User user) {
        try {
            return Objects.requireNonNull(request("get pending friends list", new JSONObject().put(Utils.PHONE_NUMBER, user.getPhoneNumber()))).getJSONArray("return value");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray getRecentTransfers(User user) {
        try {
            return request("get recent transfers", new JSONObject().put(Utils.PHONE_NUMBER, user.getPhoneNumber())).getJSONArray("return value");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class SocketTask extends AsyncTask<JSONObject, Void, JSONObject> { //TODO switch to proper sockets
        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            try {
                Socket socket = new Socket(ServerUtils.HOST, ServerUtils.PORT);

                for (JSONObject j : jsonObjects) {
                    socket.getOutputStream().write(j.toString().getBytes(StandardCharsets.UTF_8));
                }

                int c;
                StringBuilder s = new StringBuilder();
                while ((c = socket.getInputStream().read()) != -1) {
                    s.append((char) c);
                }
                socket.close();
                return new JSONObject(s.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
