package com.mazenfahim.YallaBudget.Model;



public class User {
    private String UserName;
    private String PIN;

    public User(String name,String pin){
        this.UserName=name;
        this.PIN=pin;
    }

    public String getPIN(){
        return PIN;
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
