package com.mazenfahim.YallaBudget;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.util.HashSet;

public class LoginController {
    HashSet<User> users = new HashSet<>();
    @FXML
    CheckBox remember_me;
    @FXML
    Hyperlink forgotPassword;


    @FXML
    public TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;


    private String username;
    private String password;
    private String email;
    @FXML
    private void validateCredentials(ActionEvent event) throws IOException {
        username = usernameField.getText().trim();
        password = passwordField.getText().trim();
        email    = emailField.getText().trim();
        users.add(new User("Mazen", "mezoashee@gmail.com", "mezo1234"));
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        // 2. Basic email format check
        if (!email.contains("@") || !email.contains(".")) {
            showError("Invalid email format.");
            return;
        }

        // 3. Password length
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        // 4. Check if user exists in the HashSet
        boolean found = false;
        for (User user : users) {
            if (user.matches(email, password)) {
                found = true;
                break;
            }
        }

        if (found) {
            System.out.println("Login successful!");
            switchToMain(event);
        } else {
            showError("Invalid email or password.");
        }
    }

    private void showError(String message) {
        System.out.println("Error: " + message);
    }

    private void switchToMain(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
