package com.mazenfahim.YallaBudget.Model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BudgetCycle {
    private int Id;
    private double Total_Allowance;
    private LocalDate StartDate;
    private LocalDate EndDate;
    private double RemainingBalance;
    private List<Expense> Expenses=new ArrayList<>();

    public BudgetCycle(double total_Allowance,LocalDate startDate,LocalDate endDate){
        this.Total_Allowance=total_Allowance;
        this.StartDate=startDate;
        this.EndDate=endDate;
        this.RemainingBalance=total_Allowance;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public int calculateRemainingDays(){
        return Math.toIntExact(ChronoUnit.DAYS.between(StartDate, EndDate));
    }

    public double calculateDailyLimit(){
        double DailyLimit=RemainingBalance/calculateRemainingDays();
        return DailyLimit > 0 ? DailyLimit : 0;
    }

    public double getRemainingBalance() {
        return RemainingBalance;
    }

    public double getTotal_Allowance() {
        return Total_Allowance;
    }

    public void UpdateRemainingBalance(Double amount){
        RemainingBalance-=amount;
    }

    public void AddExpense(Expense expense){
        Expenses.add(expense);
        UpdateRemainingBalance(expense.getAmount());
    }

    public double getSpending(){
        return Total_Allowance-RemainingBalance;
    }

    public double getPercentageSpending(){
        if (Total_Allowance == 0) return 0;
        return (getSpending()/Total_Allowance)*100;
    }
    public LocalDate getStartDate(){
        return StartDate;
    }
    public LocalDate getEndDate(){
        return EndDate;
    }
}
