package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.BudgetCycle;
import com.mazenfahim.YallaBudget.model.BudgetModel;
import com.mazenfahim.YallaBudget.model.Category;
import com.mazenfahim.YallaBudget.model.Expense;
import com.mazenfahim.YallaBudget.model.ExpenseModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Handles the expense entry form and persists new expenses.
 */
public class ExpenseEntryController {
    /**
     * Model for expense operations.
     */
    private ExpenseModel expenseModel = new ExpenseModel();
    /**
     * Model for loading the active budget cycle.
     */
    private BudgetModel budgetModel = new BudgetModel();

    /**
     * Input for expense amount.
     */
    @FXML
    private TextField amountInput;

    /**
     * Dropdown list of available categories.
     */
    @FXML
    private ComboBox<Category> categoryComboBox;

    /**
     * Label for validation and error messages.
     */
    @FXML
    private Label errorLabel;

    /**
     * Initializes category list and clears messages.
     */
    @FXML
    public void initialize() {
        errorLabel.setText("");
        List<Category> categories = expenseModel.getCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    /**
     * Validates and saves a new expense for the active cycle.
     */
    @FXML
    public void onSubmitExpenseClicked() {
        try {
            double amount = Double.parseDouble(amountInput.getText().trim());
            Category selectedCategory = categoryComboBox.getValue();
            BudgetCycle cycle = budgetModel.loadCycle();

            if (!expenseModel.validateExpense(amount, selectedCategory)) {
                showValidationError("Please enter a valid amount and select a category.");
                return;
            }

            if (cycle == null) {
                showValidationError("Please create a budget cycle first.");
                return;
            }

            Expense expense = new Expense(amount, selectedCategory, cycle.getId());
            expenseModel.addExpense(cycle, expense);
            navigateTo();
        } catch (NumberFormatException e) {
            showValidationError("Please enter a valid amount.");
        } catch (Exception e) {
            showValidationError("Could not save the expense.");
        }
    }

    /**
     * Cancels entry and returns to the dashboard.
     */
    @FXML
    public void onCancelClicked() {
        try {
            navigateTo();
        } catch (IOException e) {
            showValidationError("Could not return to the dashboard.");
        }
    }

    /**
     * Navigates back to the dashboard view.
     *
     * @throws IOException if the dashboard view cannot be loaded
     */
    private void navigateTo() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/mazenfahim/YallaBudget/" + "DashboardView.fxml"
            )
        );
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) amountInput.getScene().getWindow();
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
