package com.mazenfahim.YallaBudget.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides filtering and sorting utilities for expense history.
 */
public class HistoryModel {
    /**
     * Model used to retrieve expenses.
     */
    private ExpenseModel expenseModel;

    /**
     * Creates the history model with its expense dependency.
     *
     * @param expenseModel model for expense retrieval
     */
    public HistoryModel(ExpenseModel expenseModel) {
        this.expenseModel = expenseModel;
    }

    /**
     * Loads all transactions for a cycle.
     *
     * @param cycleId budget cycle identifier
     * @return list of expenses for the cycle
     */
    public List<Expense> getTransactions(int cycleId) {
        return expenseModel.getExpensesByCycle(cycleId);
    }

    /**
     * Filters expenses by category.
     *
     * @param expenses list of expenses to filter
     * @param categoryId category identifier
     * @return filtered list
     */
    public List<Expense> filterByCategory(List<Expense> expenses, int categoryId) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().getId() == categoryId)
                .collect(Collectors.toList());
    }

    /**
     * Filters expenses by an optional date range.
     *
     * @param expenses list of expenses to filter
     * @param from start date (inclusive), or null to ignore
     * @param to end date (inclusive), or null to ignore
     * @return filtered list
     */
    public List<Expense> filterByDateRange(List<Expense> expenses, LocalDate from, LocalDate to) {
        return expenses.stream()
                .filter(expense -> {
                    LocalDate date = expense.getTimestamp().toLocalDate();
                    boolean afterFrom = from == null || !date.isBefore(from);
                    boolean beforeTo = to == null || !date.isAfter(to);
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Sorts expenses with the latest first.
     *
     * @param expenses list of expenses to sort
     * @return sorted list
     */
    public List<Expense> sortByLatest(List<Expense> expenses) {
        return expenses.stream()
                .sorted(Comparator.comparing(Expense::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
