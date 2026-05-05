package com.mazenfahim.YallaBudget.model;

import java.util.List;

/**
 * Provides persistence and validation for expenses.
 */
public class ExpenseModel {
    /**
     * Saves a new expense to storage.
     *
     * @param expense expense to save
     */
    public void saveExpense(Expense expense) {
        SQLiteDatabase.saveExpense(expense);
    }

    /**
     * Updates an existing expense in storage.
     *
     * @param expense expense to update
     */
    public void updateExpense(Expense expense) {
        SQLiteDatabase.updateExpense(expense);
    }

    /**
     * Deletes an expense and restores the cycle balance if needed.
     *
     * @param expense expense to delete
     */
    public void deleteExpense(Expense expense) {
        BudgetCycle cycle = SQLiteDatabase.loadCycle();
        SQLiteDatabase.deleteExpense(expense);

        if (cycle != null && expense != null && cycle.getId() == expense.getCycleId()) {
            cycle.UpdateRemainingBalance(-expense.getAmount());
            SQLiteDatabase.saveCycle(cycle);
        }
    }

    /**
     * Loads all expenses for the given budget cycle.
     *
     * @param cycleId budget cycle identifier
     * @return list of expenses for the cycle
     */
    public List<Expense> getExpensesByCycle(int cycleId) {
        return SQLiteDatabase.getExpensesByCycle(cycleId);
    }

    /**
     * Returns the list of available categories.
     *
     * @return list of categories
     */
    public List<Category> getCategories() {
        return SQLiteDatabase.getCategories();
    }

    /**
     * Validates an expense amount and category selection.
     *
     * @param amount expense amount
     * @param category selected category
     * @return true if the input is valid
     */
    public boolean validateExpense(double amount, Category category) {
        return amount > 0 && category != null;
    }

    /**
     * Adds an expense to a cycle and updates persistence.
     *
     * @param cycle active budget cycle
     * @param expense expense to add
     * @throws IllegalArgumentException if inputs are invalid
     */
    public void addExpense(BudgetCycle cycle, Expense expense) {
        if (cycle == null || expense == null || !validateExpense(expense.getAmount(), expense.getCategory())) {
            throw new IllegalArgumentException("Please enter a valid amount and select a category.");
        }

        cycle.AddExpense(expense);
        saveExpense(expense);
        SQLiteDatabase.saveCycle(cycle);
    }
}
