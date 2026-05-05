package com.mazenfahim.YallaBudget.model;

import java.time.LocalDateTime;

/**
 * Represents a single expense transaction in a budget cycle.
 */
public class Expense {
    /**
     * Database identifier for the expense.
     */
    private int id;
    /**
     * Amount spent.
     */
    private double amount;
    /**
     * Timestamp when the expense occurred.
     */
    private LocalDateTime timestamp;
    /**
     * Category assigned to the expense.
     */
    private Category category;
    /**
     * Identifier of the owning budget cycle.
     */
    private int cycleId;

    /**
     * Creates a new expense with the current timestamp.
     *
     * @param amount amount spent
     * @param category expense category
     * @param cycleId budget cycle identifier
     */
    public Expense(double amount, Category category, int cycleId) {
        this.amount = amount;
        this.category = category;
        this.cycleId = cycleId;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Sets the database identifier.
     *
     * @param id expense id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the database identifier.
     *
     * @return expense id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the expense category.
     *
     * @return category instance
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the expense timestamp.
     *
     * @return timestamp of the expense
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the expense timestamp.
     *
     * @param timestamp new timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the expense amount.
     *
     * @return amount spent
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the cycle identifier that owns this expense.
     *
     * @return cycle id
     */
    public int getCycleId() {
        return cycleId;
    }
}
