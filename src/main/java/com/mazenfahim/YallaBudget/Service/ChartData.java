package com.mazenfahim.YallaBudget.Service;

public class ChartData {
    private String category;
    private double amount;
    public ChartData(String  category,double amount) {
        this.category=category;
        this.amount=amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
}
