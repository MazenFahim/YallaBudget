package com.mazenfahim.YallaBudget.model;

public class PinModel {
    public boolean userExists() {
        return SQLiteDatabase.userExists();
    }

    public void saveUser(String username, String pin) {
        SQLiteDatabase.saveUser(username, pin);
    }

    public User loadUser() {
        return SQLiteDatabase.loadUser();
    }

    public boolean verifyPin(String pin) {
        User user = loadUser();
        return user != null && user.VerifyPIN(pin);
    }

    public void updatePin(String newPin) {
        SQLiteDatabase.updatePin(newPin);
    }
}
