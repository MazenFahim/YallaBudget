package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.BudgetCycle;
import com.mazenfahim.YallaBudget.model.BudgetModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class BudgetSetupController {
    private BudgetModel budgetModel = new BudgetModel();

    @FXML
    private TextField allowanceInput;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(30));
    }

    @FXML
    public void onStartCycleClicked() {
        try {
            double amount = Double.parseDouble(allowanceInput.getText().trim());
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();

            if (!budgetModel.validateAmount(amount)) {
                showValidationError("Allowance must be greater than zero.");
                return;
            }

            if (!budgetModel.validateDateRange(start, end)) {
                showValidationError("End date must be after start date.");
                return;
            }

            BudgetCycle cycle = new BudgetCycle(amount, start, end);
            budgetModel.saveCycle(cycle);
            budgetModel.insertCategories();
            navigateTo("DashboardView.fxml");
        } catch (NumberFormatException e) {
            showValidationError("Please enter a valid allowance amount.");
        } catch (Exception e) {
            showValidationError("Could not start the budget cycle.");
        }
    }

    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) allowanceInput.getScene().getWindow();
        stage.setScene(scene);
    }

    private void showValidationError(String message) {
        errorLabel.setText(message);
    }
}
