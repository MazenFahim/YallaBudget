package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.BudgetModel;
import com.mazenfahim.YallaBudget.model.PinModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Handles PIN verification before allowing access to the app.
 */
public class PinUnlockController {
    /**
     * Model for user credential operations.
     */
    private PinModel pinModel = new PinModel();
    /**
     * Model for loading budget cycle state.
     */
    private BudgetModel budgetModel = new BudgetModel();

    /**
     * PIN input field.
     */
    @FXML
    private PasswordField pinInput;

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
     * Verifies the PIN and navigates to the appropriate view.
     */
    @FXML
    public void onUnlockClicked() {
        String pin = pinInput.getText() == null ? "" : pinInput.getText().trim();

        if (pin.isEmpty()) {
            showValidationError("Please enter your PIN.");
            return;
        }

        try {
            if (pinModel.verifyPin(pin)) {
                if (budgetModel.cycleExists()) {
                    navigateTo("DashboardView.fxml");
                } else {
                    navigateTo("BudgetSetupView.fxml");
                }
            } else {
                showValidationError("Incorrect PIN.");
            }
        } catch (Exception e) {
            showValidationError("Could not unlock the app. Please try again.");
        }
    }

    /**
     * Navigates to the requested view.
     *
     * @param fxmlFile target FXML file name
     * @throws IOException if the view cannot be loaded
     */
    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
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
