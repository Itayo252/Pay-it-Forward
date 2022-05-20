package com.example.pay_it_forward.Utils;

/**
 * A class to hold information about a transfer
 */
public class Transfer {
    private String from;
    private String to;
    private int id;
    private int amount;

    public Transfer(String from, String to, int id, int amount) {
        this.from = from;
        this.to = to;
        this.id = id;
        this.amount = amount;
    }

    //getters and setters
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
