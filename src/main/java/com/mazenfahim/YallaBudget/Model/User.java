package com.mazenfahim.YallaBudget.Model;

import java.util.Objects;

public class User {
    private String UserName;
    private String PIN;

    public void SetPIN(String pin){
        PIN=pin;
    }
    public void SetName(String name){
        UserName=name;
    }
    public String getName(){
        return UserName;
    }
    public boolean VerifyPIN(String pin){
    return PIN.equals(pin);
    }

    public void UpdatePIN(String new_pin){
        PIN=new_pin;
    }

}
