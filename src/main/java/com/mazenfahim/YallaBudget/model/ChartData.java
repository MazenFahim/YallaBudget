package com.mazenfahim.YallaBudget.model;

/**
 * Simple data holder for chart labels and amounts.
 */
public class ChartData {
    /**
     * Category label for chart display.
     */
    private String category;
    /**
     * Amount associated with the category.
     */
    private double amount;

    /**
     * Creates a chart data entry.
     *
     * @param category category label
     * @param amount category total amount
     */
    public ChartData(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    /**
     * Returns the amount.
     *
     * @return amount value
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the category label.
     *
     * @return category label
     */
    public String getCategory() {
        return category;
    }
}
