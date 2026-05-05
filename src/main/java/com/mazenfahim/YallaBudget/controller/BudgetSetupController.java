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

/**
 * Handles creating a new budget cycle from the setup screen.
 */
public class BudgetSetupController {
    /**
     * Model facade for budget cycle operations.
     */
    private final BudgetModel budgetModel = new BudgetModel();

    /**
     * Input for total allowance amount.
     */
    @FXML
    private TextField allowanceInput;

    /**
     * Date picker for the cycle start date.
     */
    @FXML
    private DatePicker startDatePicker;

    /**
     * Date picker for the cycle end date.
     */
    @FXML
    private DatePicker endDatePicker;

    /**
     * Label for validation and error messages.
     */
    @FXML
    private Label errorLabel;

    /**
     * Initializes default dates and clears messages when the view loads.
     */
    @FXML
    public void initialize() {
        errorLabel.setText("");
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(30));
    }

    /**
     * Validates input and starts a new budget cycle.
     */
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
            navigateTo();
        } catch (NumberFormatException e) {
            showValidationError("Please enter a valid allowance amount.");
        } catch (Exception e) {
            showValidationError("Could not start the budget cycle.");
        }
    }

    /**
     * Navigates to the dashboard after a cycle is created.
     *
     * @throws IOException if the dashboard view cannot be loaded
     */
    private void navigateTo() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + "DashboardView.fxml"));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) allowanceInput.getScene().getWindow();
        stage.setScene(scene);
    }

    /**
     * Displays a validation or error message to the user.
     *
     * @param message message to display
     */
    private void showValidationError(String message) {
        errorLabel.setText(message);
    }
}
