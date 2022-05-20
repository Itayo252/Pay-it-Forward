package com.example.pay_it_forward.Utils;

import androidx.annotation.NonNull;

/**
 * A class to differentiate between just a user or a friend usage in code
 */
public class Friend extends User {
    public Friend(String username, String phoneNumber) {
        super(username, phoneNumber);
    }

    @NonNull
    @Override
    public String toString() {
        return getPhoneNumber().length() > 0 ? (getUsername() + " (" + getPhoneNumber() + ")") : "";
    }

    /**
     * A subclass with added functionality of request ID
     */
    public static class PendingFriend extends Friend {
        private final int requestID;

        public PendingFriend(String username, String phoneNumber, int requestID) {
            super(username, phoneNumber);
            this.requestID = requestID;
        }

        public int getRequestID() {
            return requestID;
        }
    }
}
