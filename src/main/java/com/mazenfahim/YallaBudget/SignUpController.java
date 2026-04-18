package com.mazenfahim.YallaBudget;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField passwordVisible;
    @FXML private TextField confirmPasswordVisible;

    public void storeCredentials(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Sign up successful");
        alert.showAndWait();
        goToLogin(actionEvent);
    }

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mazenfahim/YallaBudget/login.fxml")
        );
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 860, 600));
        stage.show();
    }

    @FXML
    private void togglePasswordVisibility() {
        toggle(passwordField, passwordVisible);
    }

    @FXML
    private void toggleConfirmVisibility() {
        toggle(confirmPasswordField, confirmPasswordVisible);
    }

    private void toggle(PasswordField hidden, TextField visible) {
        if (hidden.isVisible()) {
            visible.setText(hidden.getText());
            hidden.setVisible(false);
            hidden.setManaged(false);
            visible.setVisible(true);
            visible.setManaged(true);
        } else {
            hidden.setText(visible.getText());
            visible.setVisible(false);
            visible.setManaged(false);
            hidden.setVisible(true);
            hidden.setManaged(true);
        }
    }
}