# YallaBudget / Masroofy Code Explanation

This file explains the current JavaFX Maven application architecture and how the screens, controllers, services, managers, models, FXML files, and SQLite database work together.

The goal of this document is to help a teammate who is new to the project understand the code without needing to guess why each layer exists.

---

## 1. The Big Idea

YallaBudget is an offline budgeting application. The user creates a local PIN, creates a budget cycle, logs expenses, views dashboard statistics, reviews history, filters expenses, deletes expenses, and resets the current cycle.

The project currently uses this layered structure:

```text
Controller  ->  Service  ->  Manager  ->  SQLite Database
     |             |            |
     |             |            +-- runs SQL queries and updates
     |             +--------------- contains business rules and calculations
     +----------------------------- handles JavaFX UI events only

Model classes are shared data objects used by all layers.
```

A simple example:

```text
User clicks "Save Expense"
        ↓
ExpenseEntryController reads amount/category from the FXML fields
        ↓
ExpenseService validates the expense and updates the budget balance
        ↓
ExpenseManager saves the transaction in SQLite
        ↓
BudgetManager updates the remaining balance in SQLite
        ↓
Controller opens DashboardView.fxml
```

This means:

- **FXML files** describe the screen layout.
- **Controllers** respond to button clicks and read/write screen fields.
- **Services** contain the application rules.
- **Managers** contain database operations.
- **Models** represent real application objects like `BudgetCycle`, `Expense`, `Category`, and `User`.

---

## 2. Project Structure

```text
YallaBudget/
├── pom.xml
├── README.txt
├── explain.md
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java
        │   └── com/mazenfahim/YallaBudget/
        │       ├── Launcher.java
        │       ├── YallaBudgetApplication.java
        │       ├── Controller/
        │       │   ├── PinSetupController.java
        │       │   ├── PinUnlockController.java
        │       │   ├── SetupController.java
        │       │   ├── DashboardController.java
        │       │   ├── ExpenseEntryController.java
        │       │   ├── HistoryController.java
        │       │   ├── SettingController.java
        │       │   └── SettingsController.java
        │       ├── Service/
        │       │   ├── UserService.java
        │       │   ├── BudgetService.java
        │       │   ├── ExpenseService.java
        │       │   ├── DashboardService.java
        │       │   ├── HistoryService.java
        │       │   └── ChartData.java
        │       ├── Manager/
        │       │   ├── SQLiteManager.java
        │       │   ├── UserManager.java
        │       │   ├── BudgetManager.java
        │       │   └── ExpenseManager.java
        │       └── Model/
        │           ├── User.java
        │           ├── BudgetCycle.java
        │           ├── Category.java
        │           └── Expense.java
        └── resources/
            └── com/mazenfahim/YallaBudget/
                ├── PinSetupView.fxml
                ├── PinUnlockView.fxml
                ├── SetupView.fxml
                ├── BudgetSetupView.fxml
                ├── DashboardView.fxml
                ├── ExpenseEntryView.fxml
                ├── HistoryView.fxml
                ├── SettingsView.fxml
                ├── home.fxml
                └── css/home.css
```

---

## 3. Important Terms Before Reading the Code

### 3.1 JavaFX `Stage`

A `Stage` is the application window.

Think of it as the actual desktop window that has the title bar, close button, minimize button, and the content inside it.

In this project, we usually keep the same `Stage` and replace its screen content when navigating between pages.

### 3.2 JavaFX `Scene`

A `Scene` is the content inside the window.

The `Stage` owns a `Scene`.

```text
Stage = the window
Scene = the current page/screen shown inside the window
FXML = the layout used to build the scene
```

### 3.3 JavaFX `Node`

A `Node` is any visual element inside the screen.

Examples:

- `TextField`
- `PasswordField`
- `Label`
- `Button`
- `TableView`
- `PieChart`

So `allowanceInput` is not a window. It is just a text field inside the current scene.

### 3.4 FXML

FXML is an XML file that describes the UI layout.

Example:

```text
<TextField fx:id="allowanceInput" promptText="Total allowance" />
```

This means the screen has a text field, and its ID is `allowanceInput`.

The controller can access this field because it has:

```text
@FXML
private TextField allowanceInput;
```

The names must match exactly:

```text
FXML fx:id="allowanceInput"
Controller field: private TextField allowanceInput;
```

### 3.5 `@FXML`

`@FXML` tells JavaFX:

> This field or method is connected to something in the FXML file.

Without `@FXML`, JavaFX may not inject the field or call the method from the FXML.

Example:

```text
@FXML
private TextField allowanceInput;
```

JavaFX reads the FXML, finds `fx:id="allowanceInput"`, creates the text field, then puts that created text field into this Java variable.

### 3.6 `initialize()`

A controller's `initialize()` method is called automatically by JavaFX after the FXML file has been loaded and after all `@FXML` fields have been connected.

Example:

```text
@FXML
public void initialize() {
    errorLabel.setText("");
}
```

This runs when the screen opens, not when a button is clicked.

### 3.7 `onAction`

In FXML, a button can call a controller method.

Example:

```text
<Button text="Start Cycle" onAction="#onCreateCycleClicked" />
```

This means:

> When the user clicks this button, call `onCreateCycleClicked()` in the controller.

The controller must contain:

```text
@FXML
public void onCreateCycleClicked() {
    // button logic here
}
```

---

## 4. The Most Confusing Part: `navigateTo()`

You asked specifically about this code:

```text
private void navigateTo(String fxmlFile) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
    Scene scene = new Scene(loader.load(), 900, 680);
    Stage stage = (Stage) allowanceInput.getScene().getWindow();
    stage.setScene(scene);
}
```

Let's explain it line by line.

### 4.1 Method header

```text
private void navigateTo(String fxmlFile) throws IOException
```

- `private` means only this controller can use this helper method.
- `void` means the method does not return anything.
- `navigateTo` is the method name.
- `String fxmlFile` means we pass the name of the FXML file we want to open.
- `throws IOException` means loading the FXML file might fail, so Java requires us to handle that possible error.

Example call:

```text
navigateTo("DashboardView.fxml");
```

This tells the method to open the dashboard screen.

### 4.2 Loading the FXML file

```text
FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
```

This creates an `FXMLLoader` object.

`FXMLLoader` is responsible for reading an FXML file and converting it into real JavaFX UI objects.

The path:

```text
"/com/mazenfahim/YallaBudget/" + fxmlFile
```

means:

```text
Look inside src/main/resources/com/mazenfahim/YallaBudget/
```

If `fxmlFile` is:

```text
DashboardView.fxml
```

then the full resource path becomes:

```text
/com/mazenfahim/YallaBudget/DashboardView.fxml
```

So this line does not show the screen yet. It only prepares a loader for the file.

### 4.3 Creating the new scene

```text
Scene scene = new Scene(loader.load(), 900, 680);
```

`loader.load()` reads the FXML and creates the root UI node from it.

For example, most of our FXML files start with:

```text
<AnchorPane ...>
```

So `loader.load()` returns that `AnchorPane` with all its children inside it.

Then:

```text
new Scene(loader.load(), 900, 680)
```

wraps that FXML layout inside a new JavaFX scene.

`900` is the width.

`680` is the height.

So this line means:

> Build a new page from the FXML and make it 900 by 680 pixels.

### 4.4 The confusing line

```text
Stage stage = (Stage) allowanceInput.getScene().getWindow();
```

This line does **not** mean that `allowanceInput` becomes a stage.

`allowanceInput` remains a normal `TextField`.

We are only using it as a way to reach the current window.

The chain works like this:

```text
allowanceInput
    ↓ getScene()
current Scene that contains this text field
    ↓ getWindow()
current Window that contains this scene
    ↓ cast to Stage
JavaFX Stage object
```

So the meaning is:

> Starting from a field that definitely exists on this screen, find the scene it belongs to, then find the window that scene belongs to.

Why do we do this?

Because inside a controller, JavaFX does not automatically give us a `Stage` variable.

But any visible UI control, like `allowanceInput`, already lives inside the current screen. So we can use it to find the current window.

This is the same as asking:

> `allowanceInput`, which page are you inside? Which window is showing that page?

It is not converting the input field into a stage. It is using the input field as a reference point to find the stage.

In another controller, the code may use another field:

```text
Stage stage = (Stage) pinInput.getScene().getWindow();
```

or:

```text
Stage stage = (Stage) remainingBalanceLabel.getScene().getWindow();
```

The exact field does not matter much, as long as it is an FXML field that exists on the current screen.

### 4.5 Replacing the screen

```text
stage.setScene(scene);
```

This line tells the existing window:

> Replace your current screen with this new scene.

The app does not open a second window. It uses the same window and swaps the content.

---

## 5. How FXML Connects to a Controller

Let's use `SetupView.fxml` and `SetupController.java` as the example.

### 5.1 The FXML root line

```text
<AnchorPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="com.mazenfahim.YallaBudget.Controller.SetupController"
            stylesheets="@css/home.css"
            prefHeight="680.0" prefWidth="900.0"
            styleClass="app-root">
```

Important parts:

```text
fx:controller="com.mazenfahim.YallaBudget.Controller.SetupController"
```

This tells JavaFX:

> This screen is controlled by `SetupController`.

So when the FXML is loaded, JavaFX creates a `SetupController` object automatically.

```text
stylesheets="@css/home.css"
```

This connects the screen to the CSS file.

```text
styleClass="app-root"
```

This applies the `.app-root` CSS class from `home.css` to the main root container.

### 5.2 Field connection

FXML:

```text
<TextField fx:id="allowanceInput" promptText="Total allowance" />
<DatePicker fx:id="startDatePicker" promptText="Start date" />
<DatePicker fx:id="endDatePicker" promptText="End date" />
<Label fx:id="errorLabel" styleClass="error-label" />
```

Controller:

```text
@FXML
private TextField allowanceInput;

@FXML
private DatePicker startDatePicker;

@FXML
private DatePicker endDatePicker;

@FXML
private Label errorLabel;
```

The names are the bridge.

```text
allowanceInput in FXML  -> allowanceInput in Java
startDatePicker in FXML -> startDatePicker in Java
endDatePicker in FXML   -> endDatePicker in Java
errorLabel in FXML      -> errorLabel in Java
```

### 5.3 Button connection

FXML:

```text
<Button text="Start Cycle" onAction="#onCreateCycleClicked" />
```

Controller:

```text
@FXML
public void onCreateCycleClicked() {
    // logic here
}
```

When the user clicks the button, JavaFX calls `onCreateCycleClicked()`.

---

## 6. Application Startup Flow

The app starts from `Launcher.java`.

### 6.1 `Launcher.java`

```text
package com.mazenfahim.YallaBudget;
```

This file belongs to the main application package.

```text
import javafx.application.Application;
```

This imports JavaFX's `Application` class, which is needed to launch JavaFX apps.

```text
public class Launcher {
```

This defines the launcher class.

```text
public static void main(String[] args) {
```

This is the normal Java entry point.

```text
Application.launch(YallaBudgetApplication.class, args);
```

This starts JavaFX and tells it that `YallaBudgetApplication` is the real application class.

Why do we need a separate `Launcher`?

Because JavaFX Maven projects often work better when the `main()` method is in a plain launcher class that starts the JavaFX `Application` class.

### 6.2 `YallaBudgetApplication.java`

This is the main JavaFX application class.

```text
public class YallaBudgetApplication extends Application
```

This means the class is a JavaFX app.

```text
private static final int WINDOW_WIDTH = 900;
private static final int WINDOW_HEIGHT = 680;
```

These are constants for the window size.

- `private` means only this class can use them.
- `static` means they belong to the class, not to a specific object.
- `final` means the value cannot be changed.

```text
@Override
public void start(Stage stage) throws IOException
```

JavaFX calls `start()` automatically after the app launches.

The `stage` parameter is the main app window.

```text
SQLiteManager.createTables();
```

Before opening any screen, the app makes sure all SQLite tables exist.

If the database file does not exist yet, SQLite creates it.

```text
BudgetService budgetService = new BudgetService();
```

This creates the service responsible for budget rules.

```text
budgetService.ensureDefaultCategories();
```

This inserts default categories like Food, Transportation, Entertainment, etc.

It uses `INSERT OR IGNORE`, so it does not duplicate categories if they already exist.

```text
UserService userService = new UserService();
```

This creates the service responsible for PIN authentication.

```text
String initialView = userService.userExists() ? "PinUnlockView.fxml" : "PinSetupView.fxml";
```

This decides the first screen:

- If a user already exists, open the PIN unlock screen.
- If no user exists, open the PIN setup screen.

This is a ternary operator. It means:

```text
if (userService.userExists()) {
    initialView = "PinUnlockView.fxml";
} else {
    initialView = "PinSetupView.fxml";
}
```

```text
FXMLLoader loader = new FXMLLoader(YallaBudgetApplication.class.getResource(initialView));
```

This prepares to load the first FXML screen.

Because `initialView` is just `PinUnlockView.fxml` or `PinSetupView.fxml`, JavaFX looks relative to the package resource path.

```text
Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
```

This loads the FXML and wraps it inside a JavaFX scene.

```text
stage.setTitle("YallaBudget");
```

This sets the window title.

```text
stage.setScene(scene);
```

This places the first screen inside the window.

```text
stage.show();
```

This finally displays the window to the user.

---

## 7. Layer Responsibilities

### 7.1 Controller Layer

Controllers are responsible for the UI.

They should:

- read input from FXML fields
- react to button clicks
- show validation errors
- call services
- navigate between screens

They should not:

- write SQL directly
- contain database code
- contain complicated business calculations

Example:

```text
double allowance = Double.parseDouble(allowanceInput.getText().trim());
budgetService.createCycle(allowance, start, end);
```

The controller reads the text field and calls the service.

### 7.2 Service Layer

Services are responsible for business logic.

They should:

- validate values
- calculate daily limit
- check threshold alerts
- decide what should happen when adding/deleting expenses
- coordinate managers

Example:

```text
public boolean validateAmount(double amount) {
    return amount > 0;
}
```

### 7.3 Manager Layer

Managers are responsible for persistence.

They should:

- run database insert/update/delete/select operations
- convert database rows into model objects
- call `SQLiteManager`

Example:

```text
public BudgetCycle loadCycle() {
    String sql = "SELECT id, total_allowance, start_date, end_date, remaining_balance FROM budget_cycle WHERE id = 1";
    ...
}
```

### 7.4 Model Layer

Models represent real data.

They should:

- store object attributes
- provide getters/setters
- contain very small object behavior

Example:

```text
public class Expense {
    private double amount;
    private LocalDateTime timestamp;
    private Category category;
    private int cycleId;
}
```

---

## 8. Database Design

The SQLite file is:

```text
yallabudget.db
```

It is created in the working directory when the app runs.

The database has four tables:

### 8.1 `user`

Stores the single local user and PIN.

```text
CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY,
    username TEXT NOT NULL,
    pin TEXT NOT NULL
)
```

The app always uses `id = 1`, because this is a single-user offline app.

### 8.2 `budget_cycle`

Stores the active allowance cycle.

```text
CREATE TABLE IF NOT EXISTS budget_cycle (
    id INTEGER PRIMARY KEY,
    total_allowance REAL NOT NULL,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    remaining_balance REAL NOT NULL
)
```

The app also always uses `id = 1` for the current cycle.

### 8.3 `category`

Stores expense categories.

```text
CREATE TABLE IF NOT EXISTS category (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT
)
```

Default categories are inserted when the app starts.

### 8.4 `expense`

Stores logged transactions.

```text
CREATE TABLE IF NOT EXISTS expense (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amount REAL NOT NULL,
    category_id INTEGER NOT NULL,
    cycle_id INTEGER NOT NULL,
    timestamp TEXT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (cycle_id) REFERENCES budget_cycle(id) ON DELETE CASCADE
)
```

`category_id` links each expense to a category.

`cycle_id` links each expense to the current budget cycle.

`ON DELETE CASCADE` means if the budget cycle is deleted, its expenses are deleted too.

---

## 9. SQLiteManager Explained

`SQLiteManager` is the low-level database helper.

It owns the JDBC code.

Other managers use it instead of repeating connection code everywhere.

### 9.1 Database URL

```text
private static final String URL = "jdbc:sqlite:yallabudget.db";
```

This tells JDBC to use SQLite and store data in a file called `yallabudget.db`.

### 9.2 Private constructor

```text
private SQLiteManager() {
    // Utility class.
}
```

This prevents anyone from creating an object like:

```text
new SQLiteManager();
```

Why?

Because all methods in `SQLiteManager` are static helper methods. It is used like:

```text
SQLiteManager.createTables();
SQLiteManager.executeUpdate(...);
```

### 9.3 Connecting to SQLite

```text
public static Connection connect() throws SQLException {
    Connection connection = DriverManager.getConnection(URL);
    try (Statement statement = connection.createStatement()) {
        statement.execute("PRAGMA foreign_keys = ON");
    }
    return connection;
}
```

Line by line:

- `public static Connection connect()` means any manager can call this method without creating a `SQLiteManager` object.
- `throws SQLException` means database connection errors are possible.
- `DriverManager.getConnection(URL)` opens the SQLite database file.
- `PRAGMA foreign_keys = ON` enables foreign key behavior in SQLite.
- `return connection` gives the open connection back to the caller.

### 9.4 `createTables()`

This method creates all required tables if they do not exist.

The important part is:

```text
for (String sql : statements) {
    statement.execute(sql);
}
```

The app has an array of SQL table-creation statements. This loop runs each statement.

This fixed the earlier issue where SQL was built but not executed.

### 9.5 `executeUpdate()`

```text
public static int executeUpdate(String sql, Object... parameters)
```

This method is used for SQL commands that change data:

- `INSERT`
- `UPDATE`
- `DELETE`

`Object... parameters` means the method accepts any number of parameters.

Example:

```text
SQLiteManager.executeUpdate("DELETE FROM expense WHERE id = ?", expenseId);
```

The `?` is filled by `expenseId`.

This is safer than building SQL with string concatenation.

### 9.6 `executeQuery()`

```text
public static <T> List<T> executeQuery(String sql, ResultSetMapper<T> mapper, Object... parameters)
```

This method is used for SQL `SELECT` statements.

It returns a `List<T>` because a query can return many rows.

The `mapper` tells the method how to convert each database row into a Java object.

Example from `BudgetManager`:

```text
rs -> new BudgetCycle(
    rs.getInt("id"),
    rs.getDouble("total_allowance"),
    LocalDate.parse(rs.getString("start_date")),
    LocalDate.parse(rs.getString("end_date")),
    rs.getDouble("remaining_balance")
)
```

This means:

> For each row returned from SQLite, create a `BudgetCycle` object.

### 9.7 `exists()`

```text
public static boolean exists(String sql, Object... parameters)
```

This checks if a query returns at least one row.

Example:

```text
SQLiteManager.exists("SELECT 1 FROM user WHERE id = 1")
```

If a row exists, it returns `true`.

If no row exists, it returns `false`.

### 9.8 `bindParameters()`

```text
private static void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException
```

This fills the `?` placeholders in SQL.

The loop:

```text
for (int i = 0; i < parameters.length; i++) {
    statement.setObject(i + 1, parameters[i]);
}
```

SQL parameters start from index `1`, not `0`.

Java arrays start from `0`.

That is why it uses `i + 1`.

Example:

```text
UPDATE user SET pin = ? WHERE id = 1
```

If the parameter is `"1234"`, then `setObject(1, "1234")` fills the first `?`.

### 9.9 `ResultSetMapper<T>`

```text
@FunctionalInterface
public interface ResultSetMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}
```

This is a small interface used to convert database rows into objects.

A functional interface has one method, so we can use lambda expressions with it.

Example:

```text
rs -> new Category(rs.getInt("id"), rs.getString("name"), rs.getString("description"))
```

---

## 10. Managers Explained

Managers are the only layer that knows SQL table and column names.

### 10.1 UserManager

`UserManager` saves, loads, and updates the local user record.

```text
public boolean userExist() {
    return SQLiteManager.exists("SELECT 1 FROM user WHERE id = 1");
}
```

This checks whether the app already has a user.

```text
public void saveUser(User user) {
    String sql = "INSERT OR REPLACE INTO user(id, username, pin) VALUES(1, ?, ?)";
    SQLiteManager.executeUpdate(sql, user.getName(), user.getPIN());
}
```

This saves the user.

`INSERT OR REPLACE` means:

- If user `id = 1` does not exist, insert it.
- If user `id = 1` already exists, replace it.

```text
public User loadUser()
```

This reads the stored user from SQLite and returns a `User` object.

```text
return users.isEmpty() ? null : users.get(0);
```

This means:

- If no user was found, return `null`.
- Otherwise return the first user.

```text
public void updateUser(User user)
```

This updates the PIN for the existing user.

### 10.2 BudgetManager

`BudgetManager` saves and loads the current budget cycle.

```text
public void saveCycle(BudgetCycle cycle) {
    if (cycleExist()) {
        updateCycle(cycle);
    } else {
        insertCycle(cycle);
    }
}
```

This method decides whether to insert a new cycle or update the existing one.

```text
public void insertCycle(BudgetCycle cycle)
```

This inserts the first active cycle into the database.

```text
public void updateCycle(BudgetCycle cycle)
```

This updates the current cycle after expenses are added or deleted.

```text
public BudgetCycle loadCycle()
```

This loads the active budget cycle from the database.

Important: it uses `SELECT`, so it must use query logic, not update logic.

```text
public void deleteCycle()
```

This deletes the current cycle and its expenses.

It first deletes expenses:

```text
SQLiteManager.executeUpdate("DELETE FROM expense WHERE cycle_id = 1");
```

Then deletes the cycle:

```text
SQLiteManager.executeUpdate("DELETE FROM budget_cycle WHERE id = 1");
```

This supports the Reset Current Cycle feature.

```text
public void insertCategories()
```

This inserts default categories.

`INSERT OR IGNORE` prevents duplicate categories.

### 10.3 ExpenseManager

`ExpenseManager` saves, updates, deletes, and loads expenses.

```text
public void saveExpense(Expense expense)
```

This inserts an expense row into SQLite.

It saves:

- amount
- category id
- cycle id
- timestamp

```text
public void updateExpense(Expense expense)
```

This updates an existing expense.

It uses both `id` and `cycle_id` in the `WHERE` clause to avoid accidentally updating a transaction from another cycle.

```text
public void deleteExpense(Expense expense)
```

This deletes a selected expense.

```text
public List<Expense> getExpensesByCycle(int cycleId)
```

This loads all expenses for the current budget cycle.

The SQL joins `expense` and `category`:

```text
FROM expense e
JOIN category c ON e.category_id = c.id
```

Why join?

Because the `expense` table stores only `category_id`, but the UI needs the category name like `Food` or `Transportation`.

The manager creates a `Category` object first, then creates an `Expense` object that contains that category.

```text
private LocalDateTime parseTimestamp(String timestamp)
```

This converts the timestamp string from SQLite back into a Java `LocalDateTime`.

If the timestamp was stored only as a date, it converts it to the start of that day.

---

## 11. Services Explained

Services are the middle layer between controllers and managers.

They contain business rules.

### 11.1 UserService

`UserService` handles PIN logic.

```text
private static final int MAX_FAILED_ATTEMPTS = 3;
```

The user gets locked out after 3 wrong PIN attempts.

```text
private final UserManager userManager;
```

The service uses `UserManager` to save/load user data.

```text
private int failedAttempts;
private long lockedUntilMillis;
```

These track wrong attempts and lockout time.

Important note: these values are stored in memory. If the app fully restarts, the failed attempts reset.

```text
public void createUser(String username, String rawPin)
```

This validates the PIN, creates a default username if needed, hashes the PIN, and saves the user.

```text
String safeUsername = (username == null || username.isBlank()) ? "Student" : username.trim();
```

If the username is empty, use `Student`.

Otherwise, trim spaces from the username.

```text
userManager.saveUser(new User(safeUsername, hashPin(rawPin)));
```

The raw PIN is not saved directly. The hashed PIN is saved.

```text
public boolean authenticate(String rawPin)
```

This checks if the entered PIN is correct.

The method:

1. rejects empty input
2. rejects attempts during lockout
3. loads the user from SQLite
4. hashes the entered PIN
5. compares the calculated hash with the stored hash
6. resets failed attempts if correct
7. increases failed attempts if wrong
8. starts a 30-second lockout after 3 wrong attempts

```text
boolean valid = user.verifyPIN(hashPin(rawPin)) || user.getPIN().equals(rawPin);
```

The first part is the real secure check:

```text
user.verifyPIN(hashPin(rawPin))
```

The second part:

```text
user.getPIN().equals(rawPin)
```

is backward compatibility in case older data stored the raw PIN instead of a hash.

```text
private String hashPin(String pin)
```

This converts the PIN into a SHA-256 hash.

This is better than storing the plain PIN in the database.

### 11.2 BudgetService

`BudgetService` handles budget cycle rules.

```text
public BudgetCycle createCycle(double allowance, LocalDate start, LocalDate end)
```

This creates a new cycle after validating the amount and date range.

```text
if (!validateAmount(allowance))
```

Allowance must be greater than zero.

```text
if (!validateDateRange(start, end))
```

End date must be after start date.

```text
BudgetCycle cycle = new BudgetCycle(allowance, start, end);
```

Creates a new model object.

```text
budgetManager.saveCycle(cycle);
```

Saves it to SQLite.

```text
ensureDefaultCategories();
```

Makes sure categories exist after creating a cycle.

```text
public double applyRollover(BudgetCycle cycle)
```

This recalculates the daily limit based on today's date and the remaining balance.

It does not store a separate daily rollover row. Instead, the dashboard calculates the current safe limit dynamically.

```text
public boolean checkThreshold(BudgetCycle cycle)
```

This returns `true` when the user spent 80% or more of the total allowance.

### 11.3 ExpenseService

`ExpenseService` handles expense rules.

```text
public boolean validateExpense(double amount, Category category)
```

An expense is valid if:

- amount > 0
- category is selected

```text
public Expense addExpense(double amount, Category category)
```

This is called by the controller when the user submits the form.

It loads the current cycle, creates an `Expense`, and calls the overloaded method:

```text
addExpense(cycle, expense);
```

```text
public void addExpense(BudgetCycle cycle, Expense expense)
```

This method:

1. validates the cycle
2. validates the expense
3. subtracts the expense amount from the cycle balance
4. saves the expense
5. saves the updated cycle

```text
cycle.addExpense(expense);
```

This updates the object in memory by subtracting the expense from the remaining balance.

```text
expenseManager.insertExpense(expense);
```

This saves the transaction to SQLite.

```text
budgetManager.saveCycle(cycle);
```

This saves the updated remaining balance.

```text
private void recalculateBalanceFromExpenses(int cycleId)
```

This recalculates the remaining balance after editing or deleting expenses.

It reloads all expenses, sums them, and sets:

```text
remaining balance = total allowance - total spent
```

### 11.4 DashboardService

`DashboardService` prepares dashboard values.

```text
public double getRemainingBalance(BudgetCycle cycle)
```

Returns the cycle's remaining balance.

```text
public double getDailyLimit(BudgetCycle cycle)
```

Returns the calculated daily limit.

```text
public double getTotalSpending(BudgetCycle cycle)
```

Returns how much money has been spent.

```text
public Map<String, Double> calculateCategoryTotals(int cycleId)
```

This groups expenses by category name and sums each category.

Example result:

```text
Food -> 150.0
Transportation -> 70.0
Entertainment -> 30.0
```

```text
public List<ChartData> prepareChartData(int cycleId)
```

This converts the map into a list of `ChartData` objects so the controller can build a pie chart.

### 11.5 HistoryService

`HistoryService` handles history filtering and sorting.

```text
public List<Expense> getTransactions(int cycleId)
```

Loads all expenses for a cycle and sorts them by newest first.

```text
public List<Expense> sortTransactionsByDate(List<Expense> expenses)
```

Sorts expenses by timestamp descending.

```text
public List<Expense> filterByCategory(List<Expense> expenses, int categoryId)
```

Keeps only expenses that match a selected category.

```text
public List<Expense> filterByDateRange(List<Expense> expenses, LocalDate from, LocalDate to)
```

Keeps only expenses between the selected dates.

If `from` is empty, it does not filter by start date.

If `to` is empty, it does not filter by end date.

---

## 12. Models Explained

### 12.1 User

`User` represents the single local app user.

```text
private String userName;
private String pinHash;
```

The user has a name and stored PIN hash.

```text
public boolean verifyPIN(String calculatedHash)
```

This compares the stored hash to the calculated hash from the user's entered PIN.

### 12.2 BudgetCycle

`BudgetCycle` represents the current budget period.

```text
private int id;
private double totalAllowance;
private LocalDate startDate;
private LocalDate endDate;
private double remainingBalance;
private final List<Expense> expenses = new ArrayList<>();
```

It stores:

- cycle id
- total allowance
- start date
- end date
- remaining balance
- expenses added during the runtime object

```text
public int calculateRemainingDays()
```

This calculates how many days are left in the cycle.

It uses today's date.

```text
public double calculateDailyLimit()
```

This returns:

```text
remaining balance / remaining days
```

```text
public void updateRemainingBalance(double amount)
```

This subtracts an expense from the remaining balance.

It prevents the value from going below zero.

```text
public double getPercentageSpending()
```

This calculates spending percentage:

```text
(total spent / total allowance) * 100
```

This is used for the 80% warning.

### 12.3 Category

`Category` represents expense categories.

```text
private int id;
private String categoryName;
private String description;
```

```text
@Override
public String toString() {
    return categoryName;
}
```

This is important for the combo box.

When JavaFX displays a `Category` object in `ComboBox<Category>`, it calls `toString()` to know what text to show.

So the combo box displays `Food`, not something like:

```text
com.mazenfahim.YallaBudget.Model.Category@4f3f5b24
```

### 12.4 Expense

`Expense` represents one transaction.

```text
private int id;
private double amount;
private LocalDateTime timestamp;
private Category category;
private int cycleId;
```

It stores:

- database id
- amount spent
- timestamp
- selected category
- cycle id

```text
this.timestamp = LocalDateTime.now();
```

When a new expense is created, the timestamp is set to the current date and time automatically.

---

## 13. Controllers Explained

### 13.1 PinSetupController

This controls `PinSetupView.fxml`.

Important fields:

```text
@FXML private TextField usernameInput;
@FXML private PasswordField pinInput;
@FXML private PasswordField confirmPinInput;
@FXML private Label errorLabel;
```

These connect to FXML fields with matching `fx:id` values.

```text
private final UserService userService = new UserService();
private final BudgetService budgetService = new BudgetService();
```

The controller creates services to do real work.

It does not talk to SQLite directly.

```text
public void onCreatePinClicked()
```

This runs when the user clicks `Create PIN`.

Flow:

1. read username
2. read PIN
3. read confirm PIN
4. check that PINs are not empty
5. check that PINs match
6. call `userService.createUser(username, pin)`
7. navigate to dashboard if a cycle exists, otherwise navigate to setup

```text
navigateTo(budgetService.cycleExists() ? "DashboardView.fxml" : "SetupView.fxml");
```

This means:

- If a budget cycle already exists, open dashboard.
- If no budget cycle exists, open setup.

### 13.2 PinUnlockController

This controls `PinUnlockView.fxml`.

```text
public void onUnlockClicked()
```

This runs when the user clicks `Unlock`.

Flow:

1. check if the user is currently locked out
2. call `userService.authenticate(pinInput.getText())`
3. if correct, navigate to dashboard or setup
4. if wrong, show remaining attempts

```text
int attemptsLeft = Math.max(0, 3 - userService.getFailedAttempts());
```

This calculates how many tries are left.

`Math.max(0, ...)` prevents negative numbers.

### 13.3 SetupController

This controls `SetupView.fxml` and `BudgetSetupView.fxml`.

```text
public void initialize() {
    errorLabel.setText("");
    startDatePicker.setValue(LocalDate.now());
    endDatePicker.setValue(LocalDate.now().plusDays(30));
}
```

When the screen opens:

- clear the error label
- set start date to today
- set end date to 30 days from today

```text
public void onCreateCycleClicked()
```

This runs when the user clicks `Start Cycle`.

Flow:

1. read allowance from text field
2. convert it from String to double
3. read start and end dates
4. validate input using `validateInput()`
5. call `budgetService.createCycle(...)`
6. open dashboard

```text
double allowance = Double.parseDouble(allowanceInput.getText().trim());
```

This reads the text from the input field.

`trim()` removes spaces from the beginning and end.

Example:

```text
" 3000 " -> "3000"
```

`Double.parseDouble(...)` converts the text into a number.

If the user writes `abc`, Java throws `NumberFormatException`, and the controller shows an error.

```text
public void onStartCycleClicked() {
    onCreateCycleClicked();
}
```

This is an alias.

It exists because some diagrams/FXML versions may use `onStartCycleClicked`, while the current button uses `onCreateCycleClicked`.

Both do the same thing.

### 13.4 DashboardController

This controls `DashboardView.fxml`.

Important fields:

```text
@FXML private Label remainingBalanceLabel;
@FXML private Label dailyLimitLabel;
@FXML private Label totalSpendingLabel;
@FXML private PieChart categoryPieChart;
@FXML private Label thresholdWarningLabel;
```

These are the values shown on the dashboard.

```text
public void initialize() {
    openDashboard();
}
```

When dashboard opens, it loads all dashboard values automatically.

```text
BudgetCycle cycle = budgetService.getCurrentCycleData();
```

This loads the current cycle from SQLite.

```text
if (cycle == null) { ... return; }
```

If no active cycle exists, the dashboard shows placeholder values and stops.

```text
budgetService.applyRollover(cycle);
```

This recalculates the daily limit based on today and remaining balance.

```text
remainingBalanceLabel.setText(formatMoney(dashboardService.getRemainingBalance(cycle)));
```

This updates the label with formatted money.

```text
displayChart(cycle.getId());
```

This loads chart data and fills the pie chart.

```text
if (budgetService.checkThreshold(cycle))
```

If spending is 80% or more, show a warning.

Button methods:

```text
onAddExpenseClicked() -> ExpenseEntryView.fxml
onHistoryClicked()    -> HistoryView.fxml
onSettingsClicked()   -> SettingsView.fxml
onSetupClicked()      -> SetupView.fxml
```

### 13.5 ExpenseEntryController

This controls `ExpenseEntryView.fxml`.

```text
public void initialize()
```

When the screen opens, it:

1. clears error label
2. loads categories from the database
3. puts categories into the combo box

```text
categoryComboBox.setItems(FXCollections.observableArrayList(categories));
```

JavaFX combo boxes use observable lists, so this converts a normal Java `List<Category>` into a JavaFX-friendly list.

```text
public void onSubmitExpenseClicked()
```

This runs when user clicks `Save Expense`.

Flow:

1. parse amount
2. get selected category
3. validate using `ExpenseService`
4. add expense through `ExpenseService`
5. navigate back to dashboard

```text
Category selectedCategory = categoryComboBox.getValue();
```

This returns the selected category object.

```text
expenseService.addExpense(amount, selectedCategory);
```

This is where the real work happens:

- create expense
- save expense
- update remaining balance

### 13.6 HistoryController

This controls `HistoryView.fxml`.

It handles:

- showing transactions
- filtering by category
- filtering by date range
- deleting selected transaction

```text
private final Map<String, Integer> categoryNameToId = new HashMap<>();
```

The filter combo box stores category names as strings.

But filtering needs category IDs.

So this map connects them:

```text
"Food" -> 1
"Transportation" -> 2
```

```text
public void initialize()
```

When history opens, it:

1. creates table columns
2. loads category filter options
3. loads transaction history
4. disables delete button when no row is selected

```text
deleteButton.disableProperty().bind(expenseTableView.getSelectionModel().selectedItemProperty().isNull());
```

This is JavaFX binding.

It means:

> Disable the delete button when there is no selected transaction.

When the user selects a row, the button becomes enabled automatically.

```text
private void setupTableColumns()
```

This creates table columns in Java code instead of FXML.

Each column uses `setCellValueFactory` to tell JavaFX what value to display from each `Expense` object.

Example:

```text
amountColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f EGP", data.getValue().getAmount())));
```

This means:

> For each expense row, show its amount formatted as money.

```text
private void loadAndDisplayExpenses()
```

This is the main method for refreshing the history table.

Flow:

1. load current cycle
2. load all expenses for the cycle
3. apply category filter if selected
4. apply date range filter
5. sort by latest
6. put result into the table
7. show message if empty

```text
expenseTableView.setItems(FXCollections.observableArrayList(expenses));
```

This updates the table with the filtered expenses.

```text
public void onFilterChanged()
```

This is called when category/date filters change.

It simply reloads the table using the selected filters.

```text
public void onDeleteExpenseClicked()
```

This deletes the selected row.

Flow:

1. get selected expense
2. if none selected, show message
3. call `expenseService.deleteExpense(selectedExpense)`
4. reload table
5. show success message

### 13.7 SettingController

This controls `SettingsView.fxml`.

It handles:

- changing PIN
- resetting cycle
- going back to dashboard

```text
public void onChangePinClicked()
```

Flow:

1. read old PIN
2. read new PIN
3. read confirm PIN
4. check new PIN confirmation
5. call `userService.changePin(oldPin, newPin)`
6. clear fields
7. show success message

```text
public void onResetCycleClicked()
```

This opens a confirmation alert before deleting anything.

```text
Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
```

This creates a confirmation dialog.

```text
Optional<ButtonType> result = alert.showAndWait();
```

This shows the dialog and waits for the user's answer.

```text
if (result.isPresent() && result.get() == ButtonType.OK)
```

This checks if the user clicked OK.

If yes:

```text
budgetService.resetCurrentCycle();
navigateTo("SetupView.fxml");
```

It deletes the current cycle and returns to setup.

### 13.8 SettingsController

```text
public class SettingsController extends SettingController {
}
```

This class is only a compatibility wrapper.

The implementation lives in `SettingController`.

Why keep it?

Some diagrams or team members may use the plural name `SettingsController`, while the SDS may use `SettingController`.

This class allows both names to compile.

---

## 14. Full Feature Flows

### 14.1 First Launch and PIN Creation

```text
Launcher.main()
    -> Application.launch(YallaBudgetApplication.class)
        -> YallaBudgetApplication.start(stage)
            -> SQLiteManager.createTables()
            -> BudgetService.ensureDefaultCategories()
            -> UserService.userExists()
                -> UserManager.userExist()
                    -> SQLiteManager.exists(...)
            -> open PinSetupView.fxml if no user exists
```

When user creates PIN:

```text
PinSetupView.fxml button
    -> PinSetupController.onCreatePinClicked()
        -> UserService.createUser(username, pin)
            -> validate PIN
            -> hash PIN
            -> UserManager.saveUser(user)
                -> SQLiteManager.executeUpdate(...)
        -> BudgetService.cycleExists()
        -> navigate to SetupView.fxml or DashboardView.fxml
```

### 14.2 Returning User Unlock

```text
YallaBudgetApplication.start(stage)
    -> user exists
    -> open PinUnlockView.fxml
```

When user enters PIN:

```text
PinUnlockController.onUnlockClicked()
    -> UserService.authenticate(pin)
        -> UserManager.loadUser()
        -> hash entered PIN
        -> compare with stored hash
    -> if correct, navigate to dashboard/setup
    -> if wrong, show attempts left
```

### 14.3 Create Budget Cycle

```text
SetupView.fxml button
    -> SetupController.onCreateCycleClicked()
        -> read allowanceInput text
        -> parse amount
        -> read start/end dates
        -> BudgetService.validateAmount()
        -> BudgetService.validateDateRange()
        -> BudgetService.createCycle()
            -> BudgetManager.saveCycle()
                -> insert or update budget_cycle table
            -> BudgetManager.insertCategories()
        -> navigate to DashboardView.fxml
```

### 14.4 Add Expense

```text
ExpenseEntryView.fxml button
    -> ExpenseEntryController.onSubmitExpenseClicked()
        -> read amountInput
        -> get categoryComboBox selected value
        -> ExpenseService.validateExpense()
        -> ExpenseService.addExpense()
            -> BudgetManager.loadCycle()
            -> create Expense object
            -> BudgetCycle.addExpense()
                -> subtract amount from remaining balance
            -> ExpenseManager.insertExpense()
                -> insert into expense table
            -> BudgetManager.saveCycle()
                -> update remaining balance
        -> navigate to DashboardView.fxml
```

### 14.5 Dashboard Refresh

```text
DashboardView.fxml loads
    -> DashboardController.initialize()
        -> openDashboard()
            -> BudgetService.getCurrentCycleData()
                -> BudgetManager.loadCycle()
            -> DashboardService.getRemainingBalance()
            -> DashboardService.getDailyLimit()
            -> DashboardService.getTotalSpending()
            -> DashboardService.getChartData()
                -> ExpenseManager.getExpensesByCycle()
                -> aggregate by category
            -> BudgetService.checkThreshold()
            -> update labels and pie chart
```

### 14.6 History Filtering

```text
HistoryView.fxml loads
    -> HistoryController.initialize()
        -> setupTableColumns()
        -> populateCategoryFilter()
        -> showHistory()
            -> BudgetService.getCurrentCycleData()
            -> HistoryService.getTransactions(cycleId)
                -> ExpenseManager.getExpensesByCycle(cycleId)
                -> sort by date
            -> apply category filter
            -> apply date filter
            -> update TableView
```

### 14.7 Delete Expense

```text
HistoryController.onDeleteExpenseClicked()
    -> get selected Expense from TableView
    -> ExpenseService.deleteExpense(expense)
        -> ExpenseManager.deleteExpense(expense)
        -> recalculateBalanceFromExpenses(cycleId)
            -> BudgetManager.loadCycle()
            -> ExpenseManager.getExpensesByCycle(cycleId)
            -> sum all expenses
            -> update remaining balance
            -> BudgetManager.saveCycle(cycle)
    -> reload history table
```

### 14.8 Reset Cycle

```text
SettingsView.fxml button
    -> SettingController.onResetCycleClicked()
        -> show confirmation alert
        -> if OK:
            -> BudgetService.resetCurrentCycle()
                -> BudgetManager.deleteCycle()
                    -> delete expenses
                    -> delete budget cycle
            -> navigate to SetupView.fxml
```

---

## 15. Why Controllers Create Services Directly

Example:

```text
private final BudgetService budgetService = new BudgetService();
```

This means the controller owns a service object.

When the controller needs budget logic, it asks the service.

For this course project, this is simple and acceptable.

In larger professional applications, you might use dependency injection, but that would add complexity that is not needed here.

---

## 16. Why Services Create Managers

Example from `ExpenseService`:

```text
public ExpenseService() {
    this(new ExpenseManager(), new BudgetManager());
}
```

The default constructor creates the managers it needs.

This lets the controller simply write:

```text
new ExpenseService();
```

without worrying about manager objects.

There are also constructors like:

```text
public ExpenseService(ExpenseManager expenseManager, BudgetManager budgetManager)
```

These make the code easier to test or modify later, because you can pass custom manager objects if needed.

---

## 17. Why Managers Use `SQLiteManager`

Without `SQLiteManager`, every manager would repeat code like:

```text
Connection connection = DriverManager.getConnection(...);
PreparedStatement statement = connection.prepareStatement(...);
statement.setObject(...);
statement.executeUpdate();
```

Instead, we centralized that in one class.

So managers only focus on their SQL purpose.

Example:

```text
SQLiteManager.executeUpdate(sql, expense.getAmount(), expense.getCategory().getId(), ...);
```

This improves readability and reduces repeated code.

---

## 18. Why We Use `try/catch` in Controllers

Example:

```text
try {
    double amount = Double.parseDouble(amountInput.getText().trim());
    ...
} catch (NumberFormatException e) {
    showValidationError("Please enter a valid number.");
}
```

The user may type invalid input.

If the app does not catch this error, the app may crash.

Instead, the controller catches the exception and shows a friendly error message.

Common exceptions:

- `NumberFormatException`: user typed non-numeric text where a number is required.
- `IllegalArgumentException`: service rejected invalid business input.
- `IllegalStateException`: missing required state, like no active cycle.
- `IOException`: FXML navigation failed.

---

## 19. Why Some Methods Have Compatibility Names

In `BudgetCycle`, there are methods like:

```text
public double getTotal_Allowance()
public void UpdateRemainingBalance(Double amount)
public void AddExpense(Expense expense)
```

These names are not ideal Java style because Java methods usually use lower camelCase.

Better style:

```text
getTotalAllowance()
updateRemainingBalance(...)
addExpense(...)
```

They were kept so older code, diagrams, or previous class tables do not break immediately.

The cleaner methods still exist and should be preferred.

---

## 20. `module-info.java` Explained

```text
module com.mazenfahim.YallaBudget {
```

This declares the Java module name.

```text
requires javafx.controls;
requires javafx.fxml;
requires java.sql;
requires org.xerial.sqlitejdbc;
```

These lines declare dependencies:

- `javafx.controls`: buttons, labels, tables, pie charts, etc.
- `javafx.fxml`: loading FXML files.
- `java.sql`: JDBC database interfaces.
- `org.xerial.sqlitejdbc`: SQLite JDBC driver.

```text
opens com.mazenfahim.YallaBudget to javafx.fxml;
opens com.mazenfahim.YallaBudget.Controller to javafx.fxml;
```

This allows JavaFX reflection to access controller fields and methods marked with `@FXML`.

This is important because many controller fields are private.

JavaFX needs permission to inject them.

```text
exports com.mazenfahim.YallaBudget;
exports com.mazenfahim.YallaBudget.Controller;
exports com.mazenfahim.YallaBudget.Service;
exports com.mazenfahim.YallaBudget.Manager;
exports com.mazenfahim.YallaBudget.Model;
```

This makes these packages visible outside the module.

---

## 21. `pom.xml` Explained Briefly

`pom.xml` is Maven's project configuration file.

Important dependencies:

```text
<artifactId>javafx-controls</artifactId>
```

Needed for JavaFX controls like Button, Label, TextField, PieChart, TableView.

```text
<artifactId>javafx-fxml</artifactId>
```

Needed to load FXML files.

```text
<artifactId>sqlite-jdbc</artifactId>
```

Needed so Java can connect to SQLite.

Important plugin:

```text
<artifactId>javafx-maven-plugin</artifactId>
```

This lets you run the app with:

```text
mvn javafx:run
```

Main class:

```text
<mainClass>com.mazenfahim.YallaBudget/com.mazenfahim.YallaBudget.Launcher</mainClass>
```

This tells Maven:

```text
Module: com.mazenfahim.YallaBudget
Class:  com.mazenfahim.YallaBudget.Launcher
```

---

## 22. Common Questions

### Q1: Why do we use `allowanceInput.getScene().getWindow()`?

Because the controller needs the current window to replace its scene.

`allowanceInput` is just a known UI node on the current screen.

We use it to reach:

```text
TextField -> Scene -> Window/Stage
```

We are not converting the input into a stage.

### Q2: Could we use another field instead of `allowanceInput`?

Yes.

Any field that exists on the current screen works.

For example, in dashboard we use:

```text
remainingBalanceLabel.getScene().getWindow()
```

In expense screen we use:

```text
amountInput.getScene().getWindow()
```

The field is just a path to the current window.

### Q3: Why not create a new `Stage` every time?

Because that would open a new window for every screen.

We want one application window and multiple pages inside it.

So we use:

```text
stage.setScene(scene);
```

instead of:

```text
new Stage().show();
```

### Q4: Why do controllers not call SQL directly?

Because controllers should only control the UI.

If controllers also contain SQL, the code becomes messy and hard to maintain.

The clean flow is:

```text
Controller -> Service -> Manager -> SQLiteManager
```

### Q5: Why do services exist if managers already save data?

Managers only know database operations.

Services know business rules.

Example:

- Manager can insert an expense.
- Service knows that the amount must be positive and that the budget balance must be updated after inserting the expense.

### Q6: Why does `BudgetCycle` calculate daily limit?

Because daily limit is related directly to the cycle data:

```text
remaining balance / remaining days
```

So it makes sense for the `BudgetCycle` object to know how to calculate it.

### Q7: Why is the app using `id = 1` for user and budget cycle?

Because this is an offline single-user app.

There is only one local user and one active budget cycle.

So the implementation keeps things simple by always using row `1`.

---

## 23. How to Explain This Project to the TA

A good simple explanation:

> The application uses a layered architecture. JavaFX FXML files represent the views. Controllers handle user interaction and navigation. Services contain business logic such as validation, PIN authentication, daily limit calculation, threshold checks, and expense balance updates. Managers handle persistence and communicate with SQLite through a centralized `SQLiteManager`. Models represent the system entities such as `User`, `BudgetCycle`, `Expense`, and `Category`. The app is fully offline and stores all data locally in SQLite.

For feature mapping:

- PIN setup/unlock: `PinSetupController`, `PinUnlockController`, `UserService`, `UserManager`, `User`
- Budget setup: `SetupController`, `BudgetService`, `BudgetManager`, `BudgetCycle`
- Expense logging: `ExpenseEntryController`, `ExpenseService`, `ExpenseManager`, `Expense`, `Category`
- Dashboard: `DashboardController`, `DashboardService`, `BudgetService`, `ChartData`
- History/filter/delete: `HistoryController`, `HistoryService`, `ExpenseService`, `ExpenseManager`
- Reset/change PIN: `SettingController`, `BudgetService`, `UserService`

---

## 24. Suggested Reading Order for a New Developer

Do not start with all files randomly.

Use this order:

1. `YallaBudgetApplication.java` — understand startup.
2. `PinSetupView.fxml` + `PinSetupController.java` — understand FXML/controller connection.
3. `SetupView.fxml` + `SetupController.java` — understand input reading and validation.
4. `BudgetService.java` + `BudgetManager.java` + `BudgetCycle.java` — understand cycle creation.
5. `ExpenseEntryView.fxml` + `ExpenseEntryController.java` — understand expense input.
6. `ExpenseService.java` + `ExpenseManager.java` + `Expense.java` — understand saving expenses.
7. `DashboardController.java` + `DashboardService.java` — understand summaries and charts.
8. `HistoryController.java` + `HistoryService.java` — understand table, filters, and deletion.
9. `SQLiteManager.java` — understand how all SQL is executed.
10. `module-info.java` and `pom.xml` — understand project dependencies.

---

## 25. Final Mental Model

Always remember this chain:

```text
FXML field/button
    ↓
Controller @FXML field/method
    ↓
Service business rule
    ↓
Manager database operation
    ↓
SQLiteManager JDBC helper
    ↓
yallabudget.db SQLite file
```

And for navigation:

```text
Known UI field on current screen
    ↓ getScene()
Current scene
    ↓ getWindow()
Current application window / Stage
    ↓ setScene(newScene)
New screen appears
```

That is the core of the whole application.
