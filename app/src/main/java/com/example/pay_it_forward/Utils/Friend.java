package com.example.pay_it_forward.Utils;

public class Friend extends User {
    public Friend(String username, String phoneNumber) {
        super(username, phoneNumber);
    }

    @Override
    public String toString() {
        return getPhoneNumber().length() > 0 ? (getUsername() + " (" + getPhoneNumber() + ")") : "";
    }

    public static class PendingFriend extends Friend {
        private final String requestID;

        public PendingFriend(String username, String phoneNumber, String requestID) {
            super(username, phoneNumber);
            this.requestID = requestID;
        }

        public String getRequestID() {
            return requestID;
        }
    }
}
