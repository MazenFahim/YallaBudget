package com.mazenfahim.YallaBudget.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a budget cycle with allowance, dates, and expenses.
 */
public class BudgetCycle {
    /**
     * Database identifier for the cycle.
     */
    private int id;
    /**
     * Total allowance assigned to the cycle.
     */
    private double totalAllowance;
    /**
     * Start date of the cycle.
     */
    private LocalDate startDate;
    /**
     * End date of the cycle.
     */
    private LocalDate endDate;
    /**
     * Remaining balance available to spend.
     */
    private double remainingBalance;
    /**
     * List of expenses recorded in the cycle.
     */
    private List<Expense> expenses = new ArrayList<>();

    /**
     * Creates a new budget cycle with an initial remaining balance.
     *
     * @param totalAllowance total allowance for the cycle
     * @param startDate      start date of the cycle
     * @param endDate        end date of the cycle
     */
    public BudgetCycle(double totalAllowance, LocalDate startDate, LocalDate endDate) {
        this(1, totalAllowance, startDate, endDate, totalAllowance);
    }

    /**
     * Creates a budget cycle with explicit id and remaining balance.
     *
     * @param id                database identifier
     * @param totalAllowance     total allowance for the cycle
     * @param startDate         start date of the cycle
     * @param endDate           end date of the cycle
     * @param remainingBalance   remaining balance for the cycle
     */
    public BudgetCycle(int id, double totalAllowance, LocalDate startDate, LocalDate endDate, double remainingBalance) {
        this.id = id;
        this.totalAllowance = totalAllowance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingBalance = remainingBalance;
    }

    /**
     * Sets the database identifier.
     *
     * @param id database identifier
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the database identifier.
     *
     * @return cycle id
     */
    public int getId() {
        return id;
    }

    /**
     * Calculates the number of days in the cycle
     *
     * @return cycle duration in days, at least 1
     */
    public int calculateRemainingDays() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return (int) Math.max(days, 1);
    }

    /**
     * Calculates the daily spending limit by dividing the remaining balance
     * by the calculated cycle duration.
     *
     * @return daily limit amount
     */
    public double calculateDailyLimit() {
        return Math.max(remainingBalance / calculateRemainingDays(), 0);
    }

    /**
     * Returns the remaining balance.
     *
     * @return remaining balance
     */
    public double getRemainingBalance() {
        return remainingBalance;
    }

    /**
     * Returns the total allowance.
     *
     * @return total allowance for the cycle
     */
    public double getTotal_Allowance() {
        return totalAllowance;
    }

    /**
     * Decreases the remaining balance by the provided amount.
     *
     * @param amount amount to subtract from the remaining balance
     */
    public void UpdateRemainingBalance(Double amount) {
        remainingBalance -= amount;
    }

    /**
     * Adds an expense and updates the remaining balance.
     *
     * @param expense expense to add
     */
    public void AddExpense(Expense expense) {
        expenses.add(expense);
        UpdateRemainingBalance(expense.getAmount());
    }

    /**
     * Returns the total amount spent in the cycle.
     *
     * @return total spending
     */
    public double getSpending() {
        return totalAllowance - remainingBalance;
    }

    /**
     * Calculates the percentage of allowance spent.
     *
     * @return percentage spent, or 0 if allowance is zero
     */
    public double getPercentageSpending() {
        if (totalAllowance == 0) return 0;
        return (getSpending() / totalAllowance) * 100;
    }

    /**
     * Returns the cycle start date.
     *
     * @return start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the cycle end date.
     *
     * @return end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }
}
