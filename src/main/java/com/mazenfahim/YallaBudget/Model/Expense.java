package com.mazenfahim.YallaBudget.Model;

import java.util.Date;

public class Expense {
    private int Id;
    private double Amount;
    private int CategoryId;
    private Date Timestamp;

    public void setId(int id){
        Id=id;
    }
    public void setAmount(double amount){
        Amount=amount;
    }

}
