package com.mazenfahim.YallaBudget.Service;

import com.mazenfahim.YallaBudget.Model.BudgetCycle;

import java.time.LocalDate;

public class BudgetService {
    public void createCycle(double total_Allowance, LocalDate startDate, LocalDate endDate){
        BudgetCycle newCycle=new BudgetCycle(total_Allowance,startDate,endDate);
    //BudgetManager.saveCycl(Cycle);
    }
    public double recalculateDailyLimit(BudgetCycle Cycle){
       return Cycle.calculateDailyLimit();
    }

    //    public void ApplyRollover(){
//        //not needed now
//    }
    public boolean checkThreshold(BudgetCycle Cycle){
        return Cycle.getPercentageSpending()>=80;
    }

    public boolean validateAmount(double amount){
        return amount > 0;
    }

    public boolean validateDateRange(LocalDate startDate,LocalDate endDate){
        return(startDate.isBefore(endDate));
    }
}
