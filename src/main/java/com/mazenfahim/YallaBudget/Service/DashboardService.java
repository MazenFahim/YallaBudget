package com.mazenfahim.YallaBudget.Service;

import com.mazenfahim.YallaBudget.Manager.ExpenseManager;
import com.mazenfahim.YallaBudget.Model.BudgetCycle;
import com.mazenfahim.YallaBudget.Model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {

    private ExpenseManager expenseManager;

    public DashboardService(ExpenseManager expenseManager){
        this.expenseManager=expenseManager;
    }

    public double getRemainingBalance(BudgetCycle Cycle){
        return Cycle.getRemainingBalance();
    }

    public double getDailyLimit(BudgetCycle Cycle){
        return Cycle.calculateDailyLimit();
    }

    public double getTotalSpending(BudgetCycle Cycle){
        return Cycle.getSpending();
    }

    public Map<String,Double> calculateCategoryTotals(int CycleId){
        Map<String,Double> categoryTotals=new HashMap<>();
        List<Expense> expenses = expenseManager.getExpensesByCycle(CycleId);
        for(Expense expense:expenses){
            String categoryName=expense.getCategory().getCategoryName();
            double amount=expense.getAmount();
            categoryTotals.put(categoryName,
                    categoryTotals.getOrDefault(categoryName, 0.0) + amount);
        }
        return categoryTotals;
    }

    public List<ChartData> prepareChartData(int cycleId){

        Map<String,Double> categoryTotals=calculateCategoryTotals(cycleId);
        List<ChartData> chartData=new ArrayList<>();
        for(Map.Entry<String,Double> entry:categoryTotals.entrySet()){
            chartData.add(new ChartData(entry.getKey(),entry.getValue()));
        }
        return chartData;
    }

}
