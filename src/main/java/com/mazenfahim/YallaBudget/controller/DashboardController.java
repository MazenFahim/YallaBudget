package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.BudgetCycle;
import com.mazenfahim.YallaBudget.model.BudgetModel;
import com.mazenfahim.YallaBudget.model.ChartData;
import com.mazenfahim.YallaBudget.model.DashboardModel;
import com.mazenfahim.YallaBudget.model.ExpenseModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {
    private BudgetModel budgetModel = new BudgetModel();
    private ExpenseModel expenseModel = new ExpenseModel();
    private DashboardModel dashboardModel = new DashboardModel(expenseModel);

    @FXML
    private Label remainingBalanceLabel;

    @FXML
    private Label dailyLimitLabel;

    @FXML
    private Label totalSpendingLabel;

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private Label thresholdWarningLabel;

    @FXML
    public void initialize() {
        refreshDashboard();
    }

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
    }

    @FXML
    public void onAddExpenseClicked() {
        navigateSafely("ExpenseEntryView.fxml");
    }

    @FXML
    public void onHistoryClicked() {
        navigateSafely("HistoryView.fxml");
    }

    @FXML
    public void onSettingsClicked() {
        navigateSafely("SettingsView.fxml");
    }

    private String formatMoney(double value) {
        return String.format("%.2f EGP", value);
    }

    private void navigateSafely(String fxmlFile) {
        try {
            navigateTo(fxmlFile);
        } catch (IOException e) {
            thresholdWarningLabel.setText("Could not open the selected page.");
        }
    }

    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) remainingBalanceLabel.getScene().getWindow();
        stage.setScene(scene);
    }
}
