package com.mazenfahim.YallaBudget.model;

/**
 * Represents a single user with a username and PIN.
 */
public class User {
    /**
     * Display name for the user.
     */
    private String userName;
    /**
     * Stored PIN for authentication.
     */
    private String pin;

    /**
     * Creates a user with a name and PIN.
     *
     * @param name username to store
     * @param pin  PIN to store
     */
    public User(String name, String pin) {
        this.userName = name;
        this.pin = pin;
    }

    /**
     * Returns the stored PIN.
     *
     * @return PIN value
     */
    public String getPIN() {
        return pin;
    }

    /**
     * Returns the user name.
     *
     * @return user name
     */
    public String getName() {
        return userName;
    }

    /**
     * Verifies a provided PIN against the stored value.
     *
     * @param pin PIN to verify
     * @return true if the PIN matches
     */
    public boolean VerifyPIN(String pin) {
        return this.pin.equals(pin);
    }

    /**
     * Updates the stored PIN.
     *
     * @param newPin new PIN to store
     */
    public void UpdatePIN(String newPin) {
        this.pin = newPin;
    }
}
