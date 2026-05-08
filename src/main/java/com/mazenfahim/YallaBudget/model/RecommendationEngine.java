package com.mazenfahim.YallaBudget.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Analyzes spending data and generates actionable recommendations.
 */
public class RecommendationEngine {

    /**
     * Analyzes the budget cycle and category totals to produce recommendations.
     *
     * @param cycle          active budget cycle
     * @param categoryTotals map of category name to total amount spent
     * @return list of recommendation strings
     */
    public static List<String> analyze(BudgetCycle cycle, Map<String, Double> categoryTotals) {
        List<String> tips = new ArrayList<>();

        double totalBudget = cycle.getTotal_Allowance();
        double totalSpent  = cycle.getSpending();
        double ratio       = totalSpent / totalBudget;

        // Overall budget warnings
        if (ratio >= 0.9) {
            tips.add("🚨 You've used over 90% of your budget. Avoid all non-essential spending until the next cycle.");
        } else if (ratio >= 0.7) {
            tips.add("📉 You've spent 70% of your budget. Slow down and prioritize necessities only.");
        } else if (ratio >= 0.5) {
            tips.add("💡 You're halfway through your budget. Keep an eye on discretionary spending.");
        } else if (ratio < 0.5 && ratio > 0) {
            tips.add("✅ Great job! Your spending is well balanced across all categories.");
        }

        // Per-category recommendations
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double spent = entry.getValue();
            double categoryRatio = spent / totalBudget;

            switch (category) {
                case "Food" -> {
                    if (categoryRatio >= 0.4) {
                        tips.add("🍔 Food is taking up " + percent(categoryRatio) + "% of your budget. Try cooking at home more often and reduce takeout.");
                    } else if (categoryRatio >= 0.25) {
                        tips.add("🥗 Consider meal prepping for the week to reduce food costs.");
                    }
                }
                case "Entertainment" -> {
                    if (categoryRatio >= 0.3) {
                        tips.add("🎬 Entertainment spending is high at " + percent(categoryRatio) + "%. Look for free or low-cost activities.");
                    } else if (categoryRatio >= 0.15) {
                        tips.add("🎮 Try setting a weekly entertainment limit to keep this category in check.");
                    }
                }
                case "Transportation" -> {
                    if (categoryRatio >= 0.3) {
                        tips.add("🚗 Transportation is consuming " + percent(categoryRatio) + "% of your budget. Consider carpooling or public transit.");
                    } else if (categoryRatio >= 0.15) {
                        tips.add("🚌 Look into weekly or monthly transit passes — they're usually cheaper than daily fares.");
                    }
                }
                case "Shopping" -> {
                    if (categoryRatio >= 0.3) {
                        tips.add("🛍️ Shopping is at " + percent(categoryRatio) + "% of your budget. Avoid impulse purchases and stick to a list.");
                    } else if (categoryRatio >= 0.15) {
                        tips.add("⏳ Wait 24 hours before making non-essential purchases to avoid impulse buying.");
                    }
                }
                case "Health" -> {
                    if (categoryRatio >= 0.3) {
                        tips.add("🏥 Health spending is high. Check if any expenses can be covered by insurance or substituted with lower-cost options.");
                    } else if (categoryRatio >= 0.15) {
                        tips.add("💊 Look for generic alternatives to branded medicines to reduce health costs.");
                    }
                }
                case "Education" -> {
                    if (categoryRatio >= 0.3) {
                        tips.add("📚 Education is taking " + percent(categoryRatio) + "% of your budget. Explore free resources like YouTube or public libraries.");
                    } else if (categoryRatio >= 0.15) {
                        tips.add("🎓 Look for student discounts or free online courses to supplement paid ones.");
                    }
                }
            }
        }

        if (!tips.isEmpty()) {
            return tips;
        }
        return null;
    }

    private static int percent(double ratio) {
        return (int) (ratio * 100);
    }
}