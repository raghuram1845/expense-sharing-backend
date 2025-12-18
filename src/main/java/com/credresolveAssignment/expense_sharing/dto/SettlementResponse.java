package com.credresolveAssignment.expense_sharing.dto;

public class SettlementResponse {

    private final String from;
    private final String to;
    private final double amount;

    public SettlementResponse(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }
}

