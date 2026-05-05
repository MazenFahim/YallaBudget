package com.mazenfahim.YallaBudget.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides aggregation logic for the dashboard view.
 */
public class DashboardModel {
    /**
     * Model used to load expenses for calculations.
     */
    private ExpenseModel expenseModel;

    /**
     * Creates the dashboard model with its expense dependency.
     *
     * @param expenseModel model for expense retrieval
     */
    public DashboardModel(ExpenseModel expenseModel) {
        this.expenseModel = expenseModel;
    }

    /**
     * Calculates the remaining balance for a cycle.
     *
     * @param cycle budget cycle
     * @return remaining balance or zero if cycle is null
     */
    public double getRemainingBalance(BudgetCycle cycle) {
        return cycle == null ? 0 : cycle.getRemainingBalance();
    }

    /**
     * Calculates the daily limit for a cycle.
     *
     * @param cycle budget cycle
     * @return daily limit or zero if cycle is null
     */
    public double getDailyLimit(BudgetCycle cycle) {
        return cycle == null ? 0 : cycle.calculateDailyLimit();
    }

    /**
     * Calculates total spending for a cycle.
     *
     * @param cycle budget cycle
     * @return total spending or zero if cycle is null
     */
    public double getTotalSpending(BudgetCycle cycle) {
        return cycle == null ? 0 : cycle.getSpending();
    }

    /**
     * Aggregates expense totals by category for a cycle.
     *
     * @param cycleId budget cycle identifier
     * @return map of category name to total amount
     */
    public Map<String, Double> calculateCategoryTotals(int cycleId) {
        Map<String, Double> categoryTotals = new HashMap<>();
        List<Expense> expenses = expenseModel.getExpensesByCycle(cycleId);

        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().getCategoryName();
            double amount = expense.getAmount();
            categoryTotals.put(categoryName, categoryTotals.getOrDefault(categoryName, 0.0) + amount);
        }

        return categoryTotals;
    }

    /**
     * Prepares chart data entries from category totals.
     *
     * @param cycleId budget cycle identifier
     * @return list of chart data entries
     */
    public List<ChartData> prepareChartData(int cycleId) {
        Map<String, Double> categoryTotals = calculateCategoryTotals(cycleId);
        List<ChartData> chartData = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            chartData.add(new ChartData(entry.getKey(), entry.getValue()));
        }

        return chartData;
    }
}
