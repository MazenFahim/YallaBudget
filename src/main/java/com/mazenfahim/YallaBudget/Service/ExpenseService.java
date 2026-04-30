package com.mazenfahim.YallaBudget.Service;

import com.mazenfahim.YallaBudget.Manager.BudgetManager;
import com.mazenfahim.YallaBudget.Manager.ExpenseManager;
import com.mazenfahim.YallaBudget.Model.BudgetCycle;
import com.mazenfahim.YallaBudget.Model.Category;
import com.mazenfahim.YallaBudget.Model.Expense;

import java.util.List;

public class ExpenseService {

    private ExpenseManager expenseManager;

    public ExpenseService(ExpenseManager expenseManager){
        this.expenseManager=expenseManager;
    }

    public boolean validateExpense(double amount, Category category) {
        if (amount > 0 && category != null)
            return true;
        else
            return false;
    }

    public void addExpense(BudgetCycle Cycle, Expense expense){
        if(!validateExpense(expense.getAmount(),expense.getCategory())) return;
        Cycle.AddExpense(expense);
        expenseManager.saveExpense(expense);
    }

    public List<Expense> getExpensesForCycle(int CycleId){
        return expenseManager.getExpensesByCycle(CycleId);
    }

    public void editExpense(Expense expense){
        expenseManager.updateExpense(expense);
    }

    public void deleteExpense(BudgetCycle cycle,Expense expense){
        cycle.UpdateRemainingBalance(-expense.getAmount());
        expenseManager.deleteExpense(expense);
    }
}
