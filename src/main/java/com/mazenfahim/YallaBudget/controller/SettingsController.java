package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.BudgetModel;
import com.mazenfahim.YallaBudget.model.PinModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {
    private PinModel pinModel = new PinModel();
    private BudgetModel budgetModel = new BudgetModel();

    @FXML
    private PasswordField oldPinInput;

    @FXML
    private PasswordField newPinInput;

    @FXML
    private PasswordField confirmNewPinInput;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    @FXML
    public void onChangePinClicked() {
        String oldPin = oldPinInput.getText() == null ? "" : oldPinInput.getText().trim();
        String newPin = newPinInput.getText() == null ? "" : newPinInput.getText().trim();
        String confirmNewPin = confirmNewPinInput.getText() == null ? "" : confirmNewPinInput.getText().trim();

        if (oldPin.isEmpty() || newPin.isEmpty() || confirmNewPin.isEmpty()) {
            showValidationError("Please fill in all PIN fields.");
            return;
        }

        if (!pinModel.verifyPin(oldPin)) {
            showValidationError("Old PIN is incorrect.");
            return;
        }

        if (!newPin.equals(confirmNewPin)) {
            showValidationError("New PIN and confirmation do not match.");
            return;
        }

        try {
            pinModel.updatePin(newPin);
            oldPinInput.clear();
            newPinInput.clear();
            confirmNewPinInput.clear();
            showValidationError("PIN changed successfully.");
        } catch (Exception e) {
            showValidationError("Could not update PIN.");
        }
    }

    @FXML
    public void onResetCycleClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Budget Cycle");
        alert.setHeaderText("Reset the current budget cycle?");
        alert.setContentText("This will remove the active cycle and its expenses.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    budgetModel.deleteCycle();
                    navigateTo("BudgetSetupView.fxml");
                } catch (Exception e) {
                    showValidationError("Could not reset the cycle.");
                }
            }
        });
    }

    @FXML
    public void onBackClicked() {
        try {
            navigateTo("DashboardView.fxml");
        } catch (IOException e) {
            showValidationError("Could not return to the dashboard.");
        }
    }

    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) oldPinInput.getScene().getWindow();
        stage.setScene(scene);
    }

    private void showValidationError(String message) {
        errorLabel.setText(message);
    }
}
