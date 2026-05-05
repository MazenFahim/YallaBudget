package com.mazenfahim.YallaBudget.model;

import java.time.LocalDate;

/**
 * Provides validation and persistence operations for budget cycles.
 */
public class BudgetModel {
    /**
     * Saves the provided budget cycle to storage.
     *
     * @param cycle budget cycle to save
     */
    public void saveCycle(BudgetCycle cycle) {
        SQLiteDatabase.saveCycle(cycle);
    }

    /**
     * Loads the currently active budget cycle.
     *
     * @return active cycle or null if none exists
     */
    public BudgetCycle loadCycle() {
        return SQLiteDatabase.loadCycle();
    }

    /**
     * Deletes the current budget cycle and its expenses.
     */
    public void deleteCycle() {
        SQLiteDatabase.deleteCycle();
    }

    /**
     * Checks whether a budget cycle exists.
     *
     * @return true if a cycle is stored
     */
    public boolean cycleExists() {
        return SQLiteDatabase.cycleExists();
    }

    /**
     * Inserts default categories into storage.
     */
    public void insertCategories() {
        SQLiteDatabase.insertCategories();
    }

    /**
     * Validates that an allowance amount is positive.
     *
     * @param amount allowance amount
     * @return true if the amount is greater than zero
     */
    public boolean validateAmount(double amount) {
        return amount > 0;
    }

    /**
     * Validates that the provided date range is chronological.
     *
     * @param startDate cycle start date
     * @param endDate   cycle end date
     * @return true if both dates are present and start is before end
     */
    public boolean validateDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && startDate.isBefore(endDate);
    }

    /**
     * Checks whether the spending threshold has been reached.
     *
     * @param cycle active budget cycle
     * @return true if spending is at least 80 percent
     */
    public boolean checkThreshold(BudgetCycle cycle) {
        return cycle != null && cycle.getPercentageSpending() >= 80;
    }

}
