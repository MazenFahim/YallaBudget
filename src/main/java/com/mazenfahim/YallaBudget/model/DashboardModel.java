package com.mazenfahim.YallaBudget.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardModel {
    private ExpenseModel expenseModel;

    public DashboardModel(ExpenseModel expenseModel) {
        this.expenseModel = expenseModel;
    }

    public double getRemainingBalance(BudgetCycle cycle) {
        return cycle == null ? 0 : cycle.getRemainingBalance();
    }

    public double getDailyLimit(BudgetCycle cycle) {
        return cycle == null ? 0 : cycle.calculateDailyLimit();
    }

    public double getTotalSpending(BudgetCycle cycle) {
        return cycle == null ? 0 : cycle.getSpending();
    }

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

    public List<ChartData> prepareChartData(int cycleId) {
        Map<String, Double> categoryTotals = calculateCategoryTotals(cycleId);
        List<ChartData> chartData = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            chartData.add(new ChartData(entry.getKey(), entry.getValue()));
        }

        return chartData;
    }
}
