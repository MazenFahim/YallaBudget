package com.mazenfahim.YallaBudget.model;

public class User {
    private String userName;
    private String pin;

    public User(String name, String pin) {
        this.userName = name;
        this.pin = pin;
    }

    public String getPIN() {
        return pin;
    }

    public String getName() {
        return userName;
    }

    public boolean VerifyPIN(String pin) {
        return this.pin.equals(pin);
    }

    public void UpdatePIN(String newPin) {
        this.pin = newPin;
    }
}
