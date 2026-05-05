package com.mazenfahim.YallaBudget.model;

/**
 * Provides persistence operations for user PIN data.
 */
public class PinModel {
    /**
     * Checks whether a user record exists.
     *
     * @return true if a user is stored
     */
    public boolean userExists() {
        return SQLiteDatabase.userExists();
    }

    /**
     * Saves a new user with the provided PIN.
     *
     * @param username username to store
     * @param pin PIN value to store
     */
    public void saveUser(String username, String pin) {
        SQLiteDatabase.saveUser(username, pin);
    }

    /**
     * Loads the stored user record.
     *
     * @return user instance or null if not found
     */
    public User loadUser() {
        return SQLiteDatabase.loadUser();
    }

    /**
     * Verifies a PIN against the stored user.
     *
     * @param pin PIN to verify
     * @return true if the PIN matches
     */
    public boolean verifyPin(String pin) {
        User user = loadUser();
        return user != null && user.VerifyPIN(pin);
    }

    /**
     * Updates the stored PIN value.
     *
     * @param newPin new PIN to store
     */
    public void updatePin(String newPin) {
        SQLiteDatabase.updatePin(newPin);
    }
}
