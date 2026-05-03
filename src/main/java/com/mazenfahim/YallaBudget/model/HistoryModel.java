package com.mazenfahim.YallaBudget.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryModel {
    private ExpenseModel expenseModel;

    public HistoryModel(ExpenseModel expenseModel) {
        this.expenseModel = expenseModel;
    }

    public List<Expense> getTransactions(int cycleId) {
        return expenseModel.getExpensesByCycle(cycleId);
    }

    public List<Expense> filterByCategory(List<Expense> expenses, int categoryId) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().getId() == categoryId)
                .collect(Collectors.toList());
    }

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

    public List<Expense> sortByLatest(List<Expense> expenses) {
        return expenses.stream()
                .sorted(Comparator.comparing(Expense::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
