package com.example.pay_it_forward.Utils;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ServerUtils {
    //host and port of server
    private static final String HOST = "pay.cyber.flipsbits.com"; //TODO change to real host and port
    private static final int PORT = 9696;

    public static void registerNewUser(User.UserWithPassword user) {
        request("register user", user.toJSON()); //TODO maybe add return codes
    }

    static JSONObject request(String requestName, JSONObject parameters) {
        try {
            //build request
            return send(new JSONObject().put("type", "request").put("request type", requestName).put("parameters", parameters));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject send(JSONObject jsonObject) {
        try {
            return new SocketTask().execute(jsonObject).get(); //call to sockettask with data
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
            request("add friend", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber).put(Utils.OTHER_PHONE_NUMBER, friendPhoneNumber)).getString("return value");
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

    public static void acceptFriendshipRequest(int requestID) {
        updateRequest(requestID, "update friendship request", true);
    }

    public static void denyFriendshipRequest(int requestID) {
        updateRequest(requestID, "update friendship request", false);
    }

    public static void updateRequest(int requestID, String s, boolean b) {
        try {
            request(s, new JSONObject().put(String.valueOf(requestID), b));
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

    public static JSONArray getListOfDebts(String phoneNumber) {
        try {
            return request("get debts list", new JSONObject().put(Utils.PHONE_NUMBER, phoneNumber)).getJSONArray("return value");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class SocketTask extends AsyncTask<JSONObject, Void, JSONObject> { //TODO switch to proper sockets
        private Socket socket;
        // RSA parameters

        // large primes p and q
        BigInteger p = largePrime(512);
        BigInteger q = largePrime(512);

        // n is the multiplication of p and q
        BigInteger clientN = p.multiply(q);

        // Phi(n) = (p-1)(q-1) (Euler's totient function)
        BigInteger phi = getPhi(p, q);

        // 1 < e < Phi(n) and gcd(e,Phi) = 1
        BigInteger clientE = genE(phi);

        // calculate d where d ≡ e^(-1) (mod Phi(n))
        BigInteger d = clientE.modInverse(phi);


        public static BigInteger serverE = BigInteger.ONE;
        public static BigInteger serverN;
        public static JSONObject serverPublicKey;
        public static char[] charBuffer;


        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            JSONObject rcv = new JSONObject();
            try {
                this.socket = new Socket(ServerUtils.HOST, ServerUtils.PORT); //establish connection to server

                // send client's public key
                OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
                JSONObject clientPublicKey = new JSONObject().put("client e", clientE).put("client n", clientN);
                writer.write(clientPublicKey.toString());
                writer.flush();

                // receive server's public key
                InputStreamReader reader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
                charBuffer = new char[1];
                StringBuilder receivedMessage = new StringBuilder();
                while (reader.read(charBuffer) != -1 && charBuffer[0] != '~') {//read one character at a time until end has reached
                    receivedMessage.append(charBuffer);
                }
                // parse message
                serverPublicKey = new JSONObject(receivedMessage.toString());
                serverE = new BigInteger(serverPublicKey.getString("server e"));
                serverN = new BigInteger(serverPublicKey.getString("server n"));

                // send with encryption
                send(jsonObjects[0]);
                rcv = receive();
                socket.close();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return rcv;
        }


        public void send(JSONObject send) throws IOException, JSONException {
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            String sendingStr = send.toString();
            String[] encryptedList = new String[sendingStr.length()];

            // encrypt message
            for (int i = 0; i < sendingStr.length(); i++) {
                encryptedList[i] = encrypt(BigInteger.valueOf(sendingStr.charAt(i)), serverE, serverN).toString();
            }
            JSONObject obj = new JSONObject();
            obj.put("encrypted data", new JSONArray(encryptedList));
            //send data
            writer.write(obj + "~");
            writer.flush();
        }


        public JSONObject receive() throws IOException, JSONException {
            InputStreamReader reader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            char[] charBuffer = new char[1];
            StringBuilder stringBuilder = new StringBuilder();
            while (reader.read(charBuffer) != -1 && charBuffer[0] != '~') { //read one character at a time until end has reached
                stringBuilder.append(charBuffer);
            }
            JSONObject encMsg = new JSONObject(stringBuilder.toString());
            JSONArray arr = encMsg.getJSONArray("encrypted data");
            StringBuilder decryptedMsg = new StringBuilder();
            //decrypt
            for (int i = 0; i < arr.length(); i++) {
                BigInteger encMessage = new BigInteger(arr.getString(i));
                decryptedMsg.append(new String(decrypt(encMessage, d, clientN).toByteArray()));
            }
            return new JSONObject(decryptedMsg.toString());
        }


        // generates a random large prime number of specified bit length
        public static BigInteger largePrime(int bits) {
            Random randomInteger = new Random();
            return BigInteger.probablePrime(bits, randomInteger);
        }

        // compute Phi(n) = (p-1)(q-1) (Euler's totient function)
        public static BigInteger getPhi(BigInteger p, BigInteger q) {
            return (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        }

        // find greatest common denominator (using recursive Euclidian algorithm)
        public static BigInteger gcd(BigInteger a, BigInteger b) {
            if (b.equals(BigInteger.ZERO)) {
                return a;
            } else {
                return gcd(b, a.mod(b));
            }
        }

        // generate e by finding a Phi such that they are co-primes (gcd = 1)
        public static BigInteger genE(BigInteger phi) {
            Random rand = new Random();
            BigInteger e = new BigInteger(1024, rand);
            do {
                e = new BigInteger(8, rand);
                while (e.min(phi).equals(phi)) { // while phi is smaller than e, look for a new e
                    e = new BigInteger(8, rand);
                }
            } while (!gcd(e, phi).equals(BigInteger.ONE)); // if gcd(e,phi) isn't 1 then stay in loop
            return e;
        }

        public static BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n) {
            return message.modPow(e, n); // (pow, mod)
        }

        public static BigInteger decrypt(BigInteger encMessage, BigInteger d, BigInteger n) {
            return encMessage.modPow(d, n); // (pow, mod)
        }
    }
}
