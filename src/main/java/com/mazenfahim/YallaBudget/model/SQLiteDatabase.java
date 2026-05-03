package com.mazenfahim.YallaBudget.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabase {
    private static final String URL = "jdbc:sqlite:yallabudget.db";

    public static Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void createTables() {
        String[] queries = {
                """
                CREATE TABLE IF NOT EXISTS budget_cycle (
                    id INTEGER PRIMARY KEY,
                    total_allowance REAL,
                    start_date TEXT,
                    end_date TEXT,
                    remaining_balance REAL
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS user (
                    id INTEGER PRIMARY KEY,
                    username TEXT,
                    pin TEXT
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS category (
                    id INTEGER PRIMARY KEY,
                    name TEXT,
                    description TEXT
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS expense (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL,
                    category_id INTEGER,
                    cycle_id INTEGER,
                    timestamp TEXT,
                    FOREIGN KEY (category_id) REFERENCES category(id),
                    FOREIGN KEY (cycle_id) REFERENCES budget_cycle(id)
                )
                """
        };

        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            for (String query : queries) {
                statement.execute(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database tables", e);
        }
    }

    public static boolean userExists() {
        String sql = "SELECT 1 FROM user WHERE id = 1";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check user", e);
        }
    }

    public static void saveUser(String username, String pin) {
        String sql = "INSERT INTO user(id, username, pin) VALUES(?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, 1);
            statement.setString(2, username);
            statement.setString(3, pin);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public static User loadUser() {
        String sql = "SELECT * FROM user WHERE id = 1";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return new User(
                        resultSet.getString("username"),
                        resultSet.getString("pin")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user", e);
        }
    }

    public static void updatePin(String newPin) {
        String sql = "UPDATE user SET pin = ? WHERE id = 1";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPin);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update PIN", e);
        }
    }

    public static boolean cycleExists() {
        String sql = "SELECT 1 FROM budget_cycle WHERE id = 1";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check budget cycle", e);
        }
    }

    public static void saveCycle(BudgetCycle cycle) {
        if (cycleExists()) {
            updateCycle(cycle);
        } else {
            insertCycle(cycle);
        }
    }

    private static void insertCycle(BudgetCycle cycle) {
        String sql = "INSERT INTO budget_cycle(id, total_allowance, start_date, end_date, remaining_balance) VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, 1);
            statement.setDouble(2, cycle.getTotal_Allowance());
            statement.setString(3, cycle.getStartDate().toString());
            statement.setString(4, cycle.getEndDate().toString());
            statement.setDouble(5, cycle.getRemainingBalance());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert budget cycle", e);
        }
    }

    private static void updateCycle(BudgetCycle cycle) {
        String sql = "UPDATE budget_cycle SET total_allowance = ?, start_date = ?, end_date = ?, remaining_balance = ? WHERE id = 1";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, cycle.getTotal_Allowance());
            statement.setString(2, cycle.getStartDate().toString());
            statement.setString(3, cycle.getEndDate().toString());
            statement.setDouble(4, cycle.getRemainingBalance());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update budget cycle", e);
        }
    }

    public static BudgetCycle loadCycle() {
        String sql = "SELECT * FROM budget_cycle WHERE id = 1";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return new BudgetCycle(
                        resultSet.getInt("id"),
                        resultSet.getDouble("total_allowance"),
                        LocalDate.parse(resultSet.getString("start_date")),
                        LocalDate.parse(resultSet.getString("end_date")),
                        resultSet.getDouble("remaining_balance")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load budget cycle", e);
        }
    }

    public static void deleteCycle() {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM expense WHERE cycle_id = 1");
            statement.executeUpdate("DELETE FROM budget_cycle WHERE id = 1");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete budget cycle", e);
        }
    }

    public static void insertCategories() {
        String sql = "INSERT OR IGNORE INTO category(id, name, description) VALUES " +
                "(1, 'Food', 'Expenses related to meals, groceries, and dining')," +
                "(2, 'Entertainment', 'Movies, games, events, and leisure activities')," +
                "(3, 'Transportation', 'Public transport, fuel, taxis, and travel')," +
                "(4, 'Shopping', 'Clothes, electronics, and personal items')," +
                "(5, 'Health', 'Medical expenses, pharmacy, and wellness')," +
                "(6, 'Education', 'Courses, books, and learning materials')";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert default categories", e);
        }
    }

    public static List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY id";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load categories", e);
        }
        return categories;
    }

    public static void saveExpense(Expense expense) {
        String sql = "INSERT INTO expense(amount, category_id, cycle_id, timestamp) VALUES(?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, expense.getAmount());
            statement.setInt(2, expense.getCategory().getId());
            statement.setInt(3, expense.getCycleId());
            statement.setString(4, expense.getTimestamp().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save expense", e);
        }
    }

    public static void updateExpense(Expense expense) {
        String sql = "UPDATE expense SET amount = ?, category_id = ?, timestamp = ? WHERE id = ? AND cycle_id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, expense.getAmount());
            statement.setInt(2, expense.getCategory().getId());
            statement.setString(3, expense.getTimestamp().toString());
            statement.setInt(4, expense.getId());
            statement.setInt(5, expense.getCycleId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update expense", e);
        }
    }

    public static void deleteExpense(Expense expense) {
        String sql = "DELETE FROM expense WHERE id = ? AND cycle_id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, expense.getId());
            statement.setInt(2, expense.getCycleId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete expense", e);
        }
    }

    public static List<Expense> getExpensesByCycle(int cycleId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = """
                SELECT e.id, e.amount, e.category_id, e.cycle_id, e.timestamp,
                       c.name AS cat_name, c.description AS cat_desc
                FROM expense e
                JOIN category c ON e.category_id = c.id
                WHERE e.cycle_id = ?
                ORDER BY e.timestamp ASC
                """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, cycleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Category category = new Category(
                            resultSet.getInt("category_id"),
                            resultSet.getString("cat_name"),
                            resultSet.getString("cat_desc")
                    );

                    Expense expense = new Expense(
                            resultSet.getDouble("amount"),
                            category,
                            resultSet.getInt("cycle_id")
                    );
                    expense.setId(resultSet.getInt("id"));
                    expense.setTimestamp(parseTimestamp(resultSet.getString("timestamp")));
                    expenses.add(expense);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load expenses", e);
        }
        return expenses;
    }

    private static LocalDateTime parseTimestamp(String value) {
        if (value == null || value.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception ignored) {
            return LocalDate.parse(value).atStartOfDay();
        }
    }
}
