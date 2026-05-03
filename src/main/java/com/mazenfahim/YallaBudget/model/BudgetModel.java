package com.mazenfahim.YallaBudget.model;

import java.time.LocalDate;

public class BudgetModel {
    public void saveCycle(BudgetCycle cycle) {
        SQLiteDatabase.saveCycle(cycle);
    }

    public BudgetCycle loadCycle() {
        return SQLiteDatabase.loadCycle();
    }

    public void deleteCycle() {
        SQLiteDatabase.deleteCycle();
    }

    public boolean cycleExists() {
        return SQLiteDatabase.cycleExists();
    }

    public void insertCategories() {
        SQLiteDatabase.insertCategories();
    }

    public boolean validateAmount(double amount) {
        return amount > 0;
    }

    public boolean validateDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && startDate.isBefore(endDate);
    }

    public boolean checkThreshold(BudgetCycle cycle) {
        return cycle != null && cycle.getPercentageSpending() >= 80;
    }

    public double recalculateDailyLimit(BudgetCycle cycle) {
        if (cycle == null) return 0;
        return cycle.calculateDailyLimit();
    }
}
