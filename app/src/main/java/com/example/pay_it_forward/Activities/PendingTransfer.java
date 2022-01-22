package com.example.pay_it_forward.Activities;

public class PendingTransfer {
    String from, id;
    int amount;

    public PendingTransfer(String from, String id, int amount) {
        this.from = from;
        this.id = id;
        this.amount = amount;
    }
}
