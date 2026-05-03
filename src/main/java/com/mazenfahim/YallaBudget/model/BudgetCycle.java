package com.mazenfahim.YallaBudget.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BudgetCycle {
    private int id;
    private double totalAllowance;
    private LocalDate startDate;
    private LocalDate endDate;
    private double remainingBalance;
    private List<Expense> expenses = new ArrayList<>();

    public BudgetCycle(double totalAllowance, LocalDate startDate, LocalDate endDate) {
        this(1, totalAllowance, startDate, endDate, totalAllowance);
    }

    public BudgetCycle(int id, double totalAllowance, LocalDate startDate, LocalDate endDate, double remainingBalance) {
        this.id = id;
        this.totalAllowance = totalAllowance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingBalance = remainingBalance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int calculateRemainingDays() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return (int) Math.max(days, 1);
    }

    public double calculateDailyLimit() {
        return Math.max(remainingBalance / calculateRemainingDays(), 0);
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public double getTotal_Allowance() {
        return totalAllowance;
    }

    public void UpdateRemainingBalance(Double amount) {
        remainingBalance -= amount;
    }

    public void AddExpense(Expense expense) {
        expenses.add(expense);
        UpdateRemainingBalance(expense.getAmount());
    }

    public double getSpending() {
        return totalAllowance - remainingBalance;
    }

    public double getPercentageSpending() {
        if (totalAllowance == 0) return 0;
        return (getSpending() / totalAllowance) * 100;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
