package com.mazenfahim.YallaBudget.Model;

import java.util.Date;

public class BudgetCycle {
    private int Id;
    private double Total_Allowance;
    private Date StartDate;
    private Date EndDate;
    private double RemainingBalance;

    public void setId(int id) {
        Id = id;
    }

    public void setTotal_Allowance(double total_Allowance) {
        Total_Allowance = total_Allowance;
    }

    public void setDate(Date start,Date end){
        StartDate=start;
        EndDate=end;
    }

    public BudgetCycle(){
        RemainingBalance=Total_Allowance;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public int getId() {
        return Id;
    }

    public int CalculateRemainingDays(){
        int Days= Math.toIntExact(EndDate.getDay() - StartDate.getDay());
        return Days;
    }

    public double CalculateDailyLimit(){
        double DailyLimit=RemainingBalance/CalculateRemainingDays();
    return DailyLimit;
    }

    public void UpdateRemainingBalance(Double amount){
        RemainingBalance-=amount;
    }

}
