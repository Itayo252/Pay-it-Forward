package com.example.pay_it_forward.Utils;

public class Transfer {
    private String from, to, id;
    private int amount;

    public Transfer(String from, String to, String id, int amount) {
        this.from = from;
        this.to = to;
        this.id = id;
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
