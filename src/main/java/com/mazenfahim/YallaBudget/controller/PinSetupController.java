package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.PinModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Handles initial user setup and PIN creation.
 */
public class PinSetupController {
    /**
     * Model for saving user credentials.
     */
    private final PinModel pinModel = new PinModel();

    /**
     * PIN input field.
     */
    @FXML
    private PasswordField pinInput;

    /**
     * Confirmation PIN input field.
     */
    @FXML
    private PasswordField confirmPinInput;

    /**
     * Username input field.
     */
    @FXML
    private TextField usernameInput;

    /**
     * Label for validation and error messages.
     */
    @FXML
    private Label errorLabel;

    /**
     * Clears error messages on view load.
     */
    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    /**
     * Validates input and saves a new user PIN.
     */
    @FXML
    public void onCreatePinClicked() {
        String username = usernameInput.getText() == null ? "" : usernameInput.getText().trim();
        String pin = pinInput.getText() == null ? "" : pinInput.getText().trim();
        String confirmPin = confirmPinInput.getText() == null ? "" : confirmPinInput.getText().trim();

        if (username.isEmpty() || pin.isEmpty() || confirmPin.isEmpty()) {
            showValidationError("Please fill in all fields.");
            return;
        }

        if (!pin.equals(confirmPin)) {
            showValidationError("PIN and confirmation do not match.");
            return;
        }

        try {
            pinModel.saveUser(username, pin);
            navigateTo();
        } catch (Exception e) {
            showValidationError("Could not create PIN. Please try again.");
        }
    }

    /**
     * Navigates to the budget setup view.
     *
     * @throws IOException if the view cannot be loaded
     */
    private void navigateTo() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + "BudgetSetupView.fxml"));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) pinInput.getScene().getWindow();
        stage.setScene(scene);
    }

    /**
     * Displays a validation or error message.
     *
     * @param message message to display
     */
    private void showValidationError(String message) {
        errorLabel.setText(message);
    }
}
