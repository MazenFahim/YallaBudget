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

public class PinUnlockController {
    private PinModel pinModel = new PinModel();
    private BudgetModel budgetModel = new BudgetModel();

    @FXML
    private PasswordField pinInput;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

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

    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) pinInput.getScene().getWindow();
        stage.setScene(scene);
    }

    private void showValidationError(String message) {
        errorLabel.setText(message);
    }
}
