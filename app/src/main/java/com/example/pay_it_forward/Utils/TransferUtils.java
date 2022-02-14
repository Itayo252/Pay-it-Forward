package com.example.pay_it_forward.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransferUtils {
    public static List<Transfer> getPendingTransfers(User user) {
        try {
            JSONArray jsonArray = ServerUtils.getPendingTransfersToUser(user);
            List<Transfer> pendingTransfers = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                pendingTransfers.add(new Transfer((String) Utils.safeGet(jsonArray.getJSONObject(i), Utils.FROM), (String) Utils.safeGet(jsonArray.getJSONObject(i), Utils.TO), (String) Utils.safeGet(jsonArray.getJSONObject(i), Utils.ID), (Integer) Utils.safeGet(jsonArray.getJSONObject(i), Utils.AMOUNT)));
            }
            return pendingTransfers;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void transfer(String fromPhoneNumber, String toPhoneNumber, int amount) {
        try {
            ServerUtils.request("transfer", new JSONObject().put(Utils.PHONE_NUMBER, fromPhoneNumber).put(Utils.OTHER_PHONE_NUMBER, toPhoneNumber).put(Utils.AMOUNT, amount));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<Transfer> getRecentTransfers(User user) {
        List<Transfer> list = new ArrayList<>();
        JSONArray jsonArray = ServerUtils.getRecentTransfers(user);
        for (int i = 0; i < Objects.requireNonNull(jsonArray).length(); i++) {
            try {
                list.add(new Transfer((String) Utils.safeGet(jsonArray.getJSONObject(i), Utils.FROM), (String) Utils.safeGet(jsonArray.getJSONObject(i), Utils.TO), (String) Utils.safeGet(jsonArray.getJSONObject(i), Utils.ID), (Integer) Utils.safeGet(jsonArray.getJSONObject(i), Utils.AMOUNT)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
