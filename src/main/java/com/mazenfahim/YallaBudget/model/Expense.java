package com.mazenfahim.YallaBudget.model;

import java.time.LocalDateTime;

public class Expense {
    private int id;
    private double amount;
    private LocalDateTime timestamp;
    private Category category;
    private int cycleId;

    public Expense(double amount, Category category, int cycleId) {
        this.amount = amount;
        this.category = category;
        this.cycleId = cycleId;
        this.timestamp = LocalDateTime.now();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public int getCycleId() {
        return cycleId;
    }
}
