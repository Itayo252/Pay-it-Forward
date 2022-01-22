package com.example.pay_it_forward.Utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class ServerUtils {
    private static final String HOST = "192.168.1.207"; //TODO change to real host and port
    private static final int PORT = 6666;

    public static void updateServerTransfer(String transferId, boolean isAccepted) {
        try {
            request("update transfer", new JSONObject().put(transferId, isAccepted));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void registerNewUser(User user) {
        request("register user", user.toJSON()); //TODO maybe add return codes
    }

    public static JSONObject request(String requestName, JSONObject parameters) {
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

    private static class SocketTask extends AsyncTask<JSONObject, Void, JSONObject> {
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
