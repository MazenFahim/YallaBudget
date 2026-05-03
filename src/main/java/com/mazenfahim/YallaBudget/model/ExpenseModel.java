package com.mazenfahim.YallaBudget.model;

import java.util.List;

public class ExpenseModel {
    public void saveExpense(Expense expense) {
        SQLiteDatabase.saveExpense(expense);
    }

    public void updateExpense(Expense expense) {
        SQLiteDatabase.updateExpense(expense);
    }

    public void deleteExpense(Expense expense) {
        BudgetCycle cycle = SQLiteDatabase.loadCycle();
        SQLiteDatabase.deleteExpense(expense);

        if (cycle != null && expense != null && cycle.getId() == expense.getCycleId()) {
            cycle.UpdateRemainingBalance(-expense.getAmount());
            SQLiteDatabase.saveCycle(cycle);
        }
    }

    public List<Expense> getExpensesByCycle(int cycleId) {
        return SQLiteDatabase.getExpensesByCycle(cycleId);
    }

    public List<Category> getCategories() {
        return SQLiteDatabase.getCategories();
    }

    public boolean validateExpense(double amount, Category category) {
        return amount > 0 && category != null;
    }

    public void addExpense(BudgetCycle cycle, Expense expense) {
        if (cycle == null || expense == null || !validateExpense(expense.getAmount(), expense.getCategory())) {
            throw new IllegalArgumentException("Please enter a valid amount and select a category.");
        }

        cycle.AddExpense(expense);
        saveExpense(expense);
        SQLiteDatabase.saveCycle(cycle);
    }
}
