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

public class PinSetupController {
    private PinModel pinModel = new PinModel();

    @FXML
    private PasswordField pinInput;

    @FXML
    private PasswordField confirmPinInput;

    @FXML
    private TextField usernameInput;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

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
            navigateTo("BudgetSetupView.fxml");
        } catch (Exception e) {
            showValidationError("Could not create PIN. Please try again.");
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
