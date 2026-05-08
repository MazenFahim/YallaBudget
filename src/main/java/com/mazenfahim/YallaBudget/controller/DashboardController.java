package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controls the main dashboard view that summarizes budget status.
 */
public class DashboardController {
    /**
     * Model for budget cycle operations.
     */
    private BudgetModel budgetModel = new BudgetModel();
    /**
     * Model for expense persistence.
     */
    private ExpenseModel expenseModel = new ExpenseModel();
    /**
     * Model for aggregations displayed on the dashboard.
     */
    private DashboardModel dashboardModel = new DashboardModel(expenseModel);

    /**
     * Label showing remaining balance.
     */
    @FXML
    private Label remainingBalanceLabel;

    /**
     * Label showing daily spending limit.
     */
    @FXML
    private Label dailyLimitLabel;

    /**
     * Label showing total spending.
     */
    @FXML
    private Label totalSpendingLabel;

    /**
     * Pie chart for spending by category.
     */
    @FXML
    private PieChart categoryPieChart;

    /**
     * Label used for threshold warning messages.
     */
    @FXML
    private Label thresholdWarningLabel;
    /**
     * VBox container for displaying recommendations.
     */
    @FXML
    public VBox recommendationsBox;
    /**
     * Initializes the dashboard when the view loads.
     */
    @FXML
    public void initialize() {
        refreshDashboard();
    }

    /**
     * Refreshes all dashboard metrics and charts from current data.
     */
    public void refreshDashboard() {
        BudgetCycle cycle = budgetModel.loadCycle();

        if (cycle == null) {
            remainingBalanceLabel.setText("No active cycle");
            dailyLimitLabel.setText("--");
            totalSpendingLabel.setText("--");
            thresholdWarningLabel.setText("Create a budget cycle first.");
            categoryPieChart.getData().clear();
            return;
        }

        remainingBalanceLabel.setText(formatMoney(dashboardModel.getRemainingBalance(cycle)));
        dailyLimitLabel.setText(formatMoney(dashboardModel.getDailyLimit(cycle)));
        totalSpendingLabel.setText(formatMoney(dashboardModel.getTotalSpending(cycle)));

        categoryPieChart.getData().clear();
        List<ChartData> chartData = dashboardModel.prepareChartData(cycle.getId());
        for (ChartData data : chartData) {
            categoryPieChart.getData().add(new PieChart.Data(data.getCategory(), data.getAmount()));
        }

        if (budgetModel.checkThreshold(cycle)) {
            thresholdWarningLabel.setText("Warning: you have spent 80% or more of your allowance.");
        } else {
            thresholdWarningLabel.setText("");
        }

        Map<String, Double> categoryTotals = dashboardModel.calculateCategoryTotals(cycle.getId());
        List<String> tips = RecommendationEngine.analyze(cycle, categoryTotals);

        recommendationsBox.getChildren().clear();
        if (tips != null) {
            for (String tip : tips) {
                Label tipLabel = new Label(tip);
                tipLabel.setWrapText(true);
                tipLabel.getStyleClass().add("recommendation-label");
                recommendationsBox.getChildren().add(tipLabel);
            }
        }
    }

    /**
     * Opens the expense entry screen.
     */
    @FXML
    public void onAddExpenseClicked() {
        navigateSafely("ExpenseEntryView.fxml");
    }

    /**
     * Opens the transaction history screen.
     */
    @FXML
    public void onHistoryClicked() {
        navigateSafely("HistoryView.fxml");
    }

    /**
     * Opens the settings screen.
     */
    @FXML
    public void onSettingsClicked() {
        navigateSafely("SettingsView.fxml");
    }

    /**
     * Formats a currency value for display.
     *
     * @param value amount to format
     * @return formatted amount with currency label
     */
    private String formatMoney(double value) {
        return String.format("%.2f EGP", value);
    }

    /**
     * Navigates to an FXML view and shows a warning if it fails.
     *
     * @param fxmlFile target FXML file name
     */
    private void navigateSafely(String fxmlFile) {
        try {
            navigateTo(fxmlFile);
        } catch (IOException e) {
            thresholdWarningLabel.setText("Could not open the selected page.");
        }
    }

    /**
     * Loads and displays the requested FXML view.
     *
     * @param fxmlFile target FXML file name
     * @throws IOException if the view cannot be loaded
     */
    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) remainingBalanceLabel.getScene().getWindow();
        stage.setScene(scene);
    }
}
