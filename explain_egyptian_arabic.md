# شرح مشروع YallaBudget / Masroofy

الملف ده بيشرح Architecture بتاعة تطبيق JavaFX Maven الحالي، وبيوضح إزاي الـ screens والـ controllers والـ services والـ managers والـ models وملفات FXML وقاعدة بيانات SQLite شغالين مع بعض.

هدف الملف إن أي teammate جديد على المشروع يفهم الكود من غير ما يفضل يخمن كل layer معمولة ليه وإزاي بتكلم اللي بعدها.

---

## 1. الفكرة العامة

`YallaBudget` هو تطبيق budgeting بيشتغل offline. المستخدم بيعمل PIN محلي، يعمل budget cycle، يسجل expenses، يشوف dashboard statistics، يراجع history، يعمل filters للـ expenses، يحذف expenses، ويعمل reset للـ current cycle.

المشروع حاليًا ماشي بالـ layered structure دي:

```text
Controller  ->  Service  ->  Manager  ->  SQLite Database
     |             |            |
     |             |            +-- runs SQL queries and updates
     |             +--------------- contains business rules and calculations
     +----------------------------- handles JavaFX UI events only

Model classes are shared data objects used by all layers.
```

مثال بسيط:

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

يعني باختصار:

- ملفات `FXML` بتوصف شكل الشاشة والـ layout.
- الـ `Controllers` بتتعامل مع clicks وبتقرأ وتكتب في fields اللي على الشاشة.
- الـ `Services` فيها application rules والـ business logic.
- الـ `Managers` فيها database operations.
- الـ `Models` بتمثل objects حقيقية في التطبيق زي `BudgetCycle`, `Expense`, `Category`, و `User`.

---

## 2. Project Structure

ده شكل المشروع بعد التنظيم:

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

## 3. مصطلحات مهمة قبل قراءة الكود

### 3.1 JavaFX `Stage`

الـ `Stage` هو window بتاع التطبيق.

اعتبره نفس نافذة البرنامج اللي فيها title bar وclose button وminimize button والمحتوى اللي جوه.

في المشروع ده غالبًا بنفضل مستخدمين نفس الـ `Stage`، ولما بنتنقل من صفحة لصفحة بنغير المحتوى اللي جواه بس.

### 3.2 JavaFX `Scene`

الـ `Scene` هو المحتوى اللي جوه الـ window.

الـ `Stage` بيحتوي على `Scene`.

```text
Stage = the window
Scene = the current page/screen shown inside the window
FXML = the layout used to build the scene
```

### 3.3 JavaFX `Node`

الـ `Node` هو أي visual element موجود على الشاشة.

أمثلة:

- `TextField`
- `PasswordField`
- `Label`
- `Button`
- `TableView`
- `PieChart`

فـ `allowanceInput` مش window. ده مجرد text field موجود جوه الـ current scene.

### 3.4 FXML

`FXML` هو XML file بيوصف شكل الـ UI layout.

مثال:

```text
<TextField fx:id="allowanceInput" promptText="Total allowance" />
```

ده معناه إن الشاشة فيها text field والـ ID بتاعه هو `allowanceInput`.

الـ controller يقدر يوصل للـ field ده علشان عنده:

```text
@FXML
private TextField allowanceInput;
```

لازم الاسمين يبقوا نفس بعض بالظبط:

```text
FXML fx:id="allowanceInput"
Controller field: private TextField allowanceInput;
```

### 3.5 `@FXML`

`@FXML` بتقول لـ JavaFX:

> الـ field أو method ده مربوط بحاجة موجودة في ملف FXML.

من غير `@FXML`، JavaFX ممكن مايعرفش يحقن field أو ينادي method من الـ FXML.

مثال:

```text
@FXML
private TextField allowanceInput;
```

JavaFX بيقرأ الـ FXML، يلاقي `fx:id="allowanceInput"`، يعمل text field، وبعدين يحط الـ text field اللي اتعمل ده جوه المتغير ده في Java.

### 3.6 `initialize()`

Method اسمها `initialize()` في controller بتتنادي automatic من JavaFX بعد ما ملف FXML يتحمل وبعد ما كل fields اللي عليها `@FXML` تتربط.

مثال:

```text
@FXML
public void initialize() {
    errorLabel.setText("");
}
```

دي بتشتغل لما الشاشة تفتح، مش لما button يتضغط.

### 3.7 `onAction`

في FXML، الـ button ممكن ينادي method موجودة في controller.

مثال:

```text
<Button text="Start Cycle" onAction="#onCreateCycleClicked" />
```

ده معناه:

> لما المستخدم يضغط على الزرار ده، نادي `onCreateCycleClicked()` من الـ controller.

لازم الـ controller يبقى فيه:

```text
@FXML
public void onCreateCycleClicked() {
    // button logic here
}
```

---

## 4. أكتر جزء بيلخبط: `navigateTo()`

أنت سألت تحديدًا عن الكود ده:

```text
private void navigateTo(String fxmlFile) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
    Scene scene = new Scene(loader.load(), 900, 680);
    Stage stage = (Stage) allowanceInput.getScene().getWindow();
    stage.setScene(scene);
}
```

خلينا نشرحه line by line.

### 4.1 Method header

```text
private void navigateTo(String fxmlFile) throws IOException
```

- `private` معناها إن الـ helper method دي يقدر يستخدمها الـ controller ده بس.
- `void` معناها إن الـ method مش بترجع value.
- `navigateTo` هو اسم الـ method.
- `String fxmlFile` معناها إننا بنبعت اسم ملف FXML اللي عايزين نفتحه.
- `throws IOException` معناها إن تحميل ملف FXML ممكن يفشل، فـ Java بتجبرنا نتعامل مع الاحتمال ده.

مثال call:

```text
navigateTo("DashboardView.fxml");
```

ده بيقول للـ method افتح شاشة الـ dashboard.

### 4.2 تحميل ملف FXML

```text
FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
```

السطر ده بيعمل object من `FXMLLoader`.

`FXMLLoader` مسؤول إنه يقرأ ملف FXML ويحوله لـ JavaFX UI objects حقيقية.

الـ path ده:

```text
"/com/mazenfahim/YallaBudget/" + fxmlFile
```

معناه:

```text
Look inside src/main/resources/com/mazenfahim/YallaBudget/
```

لو `fxmlFile` هي:

```text
DashboardView.fxml
```

يبقى الـ full resource path هيبقى:

```text
/com/mazenfahim/YallaBudget/DashboardView.fxml
```

السطر ده لسه مش بيعرض الشاشة. هو بس بيجهز loader للملف.

### 4.3 إنشاء Scene جديدة

```text
Scene scene = new Scene(loader.load(), 900, 680);
```

`loader.load()` بيقرأ الـ FXML ويعمل root UI node منه.

مثلاً أغلب ملفات FXML عندنا بتبدأ بـ:

```text
<AnchorPane ...>
```

فـ `loader.load()` بيرجع الـ `AnchorPane` ده ومعاه كل العناصر اللي جواه.

بعد كده:

```text
new Scene(loader.load(), 900, 680)
```

بتحط الـ layout اللي جاي من FXML جوه JavaFX scene جديدة.

`900` هو العرض.

`680` هو الارتفاع.

يعني السطر ده معناه:

> ابنِ صفحة جديدة من ملف FXML وخلي حجمها 900 في 680 pixels.

### 4.4 السطر اللي كان ملخبط

```text
Stage stage = (Stage) allowanceInput.getScene().getWindow();
```

السطر ده **مش** معناه إن `allowanceInput` اتحول لـ `Stage`.

`allowanceInput` لسه `TextField` عادي جدًا.

إحنا بس بنستخدمه كطريق نوصل بيه للـ current window.

السلسلة شغالة كده:

```text
allowanceInput
    ↓ getScene()
current Scene that contains this text field
    ↓ getWindow()
current Window that contains this scene
    ↓ cast to Stage
JavaFX Stage object
```

يعني المعنى:

> ابدأ من field موجود أكيد على الشاشة الحالية، هات الـ scene اللي هو جواها، وبعدين هات الـ window اللي الـ scene دي جواه.

ليه بنعمل كده؟

علشان جوه الـ controller، JavaFX مش بيديك automatic variable اسمه `stage`.

لكن أي UI control ظاهر على الشاشة، زي `allowanceInput`، هو أصلًا عايش جوه current screen. فبنستخدمه علشان نوصل للـ current window.

كأننا بنسأل:

> يا `allowanceInput`، أنت موجود جوه أنهي page؟ والـ page دي متعرضة جوه أنهي window؟

إحنا مش بنحول الـ input field لـ stage. إحنا بس بنستخدمه كنقطة بداية علشان نوصل للـ stage.

في controller تاني ممكن نستخدم field تاني:

```text
Stage stage = (Stage) pinInput.getScene().getWindow();
```

أو:

```text
Stage stage = (Stage) remainingBalanceLabel.getScene().getWindow();
```

اسم الـ field مش مهم قوي، المهم يكون field موجود فعلًا في الشاشة الحالية ومتربط من FXML.

### 4.5 تغيير الشاشة

```text
stage.setScene(scene);
```

السطر ده بيقول للـ window الحالية:

> بدل الشاشة الحالية بالـ scene الجديدة دي.

التطبيق مش بيفتح window تانية. هو بيستخدم نفس الـ window وبيبدل المحتوى اللي جواها.

---

## 5. إزاي FXML بيرتبط بالـ Controller

هناخد `SetupView.fxml` و `SetupController.java` كمثال.

### 5.1 أول سطر في FXML root

```text
<AnchorPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="com.mazenfahim.YallaBudget.Controller.SetupController"
            stylesheets="@css/home.css"
            prefHeight="680.0" prefWidth="900.0"
            styleClass="app-root">
```

الأجزاء المهمة:

```text
fx:controller="com.mazenfahim.YallaBudget.Controller.SetupController"
```

ده بيقول لـ JavaFX:

> الشاشة دي بيتحكم فيها `SetupController`.

فلما FXML يتحمل، JavaFX بيعمل object من `SetupController` automatically.

```text
stylesheets="@css/home.css"
```

ده بيربط الشاشة بملف CSS.

```text
styleClass="app-root"
```

ده بيطبق CSS class اسمها `.app-root` من `home.css` على الـ root container.

### 5.2 ربط fields

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

الأسماء هنا هي الجسر بين FXML و Java:

```text
allowanceInput in FXML  -> allowanceInput in Java
startDatePicker in FXML -> startDatePicker in Java
endDatePicker in FXML   -> endDatePicker in Java
errorLabel in FXML      -> errorLabel in Java
```

### 5.3 ربط الـ Button

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

لما المستخدم يضغط على الزرار، JavaFX بينادي `onCreateCycleClicked()`.

---

## 6. Application Startup Flow

التطبيق بيبدأ من `Launcher.java`.

### 6.1 `Launcher.java`

```text
package com.mazenfahim.YallaBudget;
```

الملف ده تابع للـ main application package.

```text
import javafx.application.Application;
```

ده بيعمل import لـ class اسمها `Application` من JavaFX، ودي مطلوبة علشان نشغل JavaFX apps.

```text
public class Launcher {
```

ده تعريف class اسمها `Launcher`.

```text
public static void main(String[] args) {
```

دي نقطة البداية الطبيعية لأي Java program.

```text
Application.launch(YallaBudgetApplication.class, args);
```

السطر ده بيشغل JavaFX وبيقوله إن `YallaBudgetApplication` هي الـ real application class.

ليه محتاجين `Launcher` لوحده؟

علشان مشاريع JavaFX Maven كتير بتشتغل بشكل أضمن لما `main()` تكون في launcher class عادي، والـ launcher يشغل JavaFX `Application` class.

### 6.2 `YallaBudgetApplication.java`

ده الـ main JavaFX application class.

```text
public class YallaBudgetApplication extends Application
```

ده معناه إن الـ class دي JavaFX app.

```text
private static final int WINDOW_WIDTH = 900;
private static final int WINDOW_HEIGHT = 680;
```

دي constants لحجم الـ window.

- `private` معناها إن الـ class دي بس تقدر تستخدمهم.
- `static` معناها إنهم تابعين للـ class نفسه، مش object معين.
- `final` معناها إن القيمة مش هتتغير.

```text
@Override
public void start(Stage stage) throws IOException
```

JavaFX بينادي `start()` automatic بعد ما التطبيق يشتغل.

الـ parameter اسمها `stage` وهي main app window.

```text
SQLiteManager.createTables();
```

قبل ما نفتح أي screen، التطبيق بيتأكد إن كل SQLite tables موجودة.

لو ملف database مش موجود، SQLite هينشئه.

```text
BudgetService budgetService = new BudgetService();
```

ده بيعمل service مسؤولة عن budget rules.

```text
budgetService.ensureDefaultCategories();
```

ده بيدخل default categories زي Food وTransportation وEntertainment وغيره.

بيستخدم `INSERT OR IGNORE`، فمش هيكرر categories لو موجودة قبل كده.

```text
UserService userService = new UserService();
```

ده بيعمل service مسؤولة عن PIN authentication.

```text
String initialView = userService.userExists() ? "PinUnlockView.fxml" : "PinSetupView.fxml";
```

ده بيحدد أول screen:

- لو فيه user موجود قبل كده، افتح PIN unlock screen.
- لو مفيش user، افتح PIN setup screen.

ده اسمه ternary operator. معناه نفس:

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

ده بيجهز تحميل أول FXML screen.

بما إن `initialView` هي بس `PinUnlockView.fxml` أو `PinSetupView.fxml`، JavaFX هيدور relative للـ package resource path.

```text
Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
```

ده بيحمل الـ FXML ويحطه جوه JavaFX scene.

```text
stage.setTitle("YallaBudget");
```

ده بيحط title للـ window.

```text
stage.setScene(scene);
```

ده بيحط أول screen جوه الـ window.

```text
stage.show();
```

ده أخيرًا بيعرض الـ window للمستخدم.

---

## 7. مسؤولية كل Layer

### 7.1 Controller Layer

الـ Controllers مسؤولة عن الـ UI.

المفروض تعمل:

- تقرأ input من FXML fields
- تتعامل مع button clicks
- تعرض validation errors
- تنادي services
- تعمل navigation بين screens

المفروض ما تعملش:

- تكتب SQL directly
- تحتوي database code
- تحتوي calculations أو business logic معقدة

مثال:

```text
double allowance = Double.parseDouble(allowanceInput.getText().trim());
budgetService.createCycle(allowance, start, end);
```

الـ controller قرأ الـ text field ونادى service.

### 7.2 Service Layer

الـ Services مسؤولة عن business logic.

المفروض تعمل:

- validate values
- calculate daily limit
- check threshold alerts
- تقرر يحصل إيه عند add/delete expenses
- تنسق بين managers

مثال:

```text
public boolean validateAmount(double amount) {
    return amount > 0;
}
```

### 7.3 Manager Layer

الـ Managers مسؤولة عن persistence، يعني حفظ وقراءة البيانات.

المفروض تعمل:

- تشغل database insert/update/delete/select operations
- تحول database rows لـ model objects
- تستخدم `SQLiteManager`

مثال:

```text
public BudgetCycle loadCycle() {
    String sql = "SELECT id, total_allowance, start_date, end_date, remaining_balance FROM budget_cycle WHERE id = 1";
    ...
}
```

### 7.4 Model Layer

الـ Models بتمثل data حقيقية.

المفروض تعمل:

- تخزن object attributes
- توفر getters/setters
- تحتوي object behavior بسيط جدًا

مثال:

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

ملف SQLite اسمه:

```text
yallabudget.db
```

بيتعمل في working directory لما التطبيق يشتغل.

قاعدة البيانات فيها 4 tables:

### 8.1 جدول `user`

بيخزن user المحلي والـ PIN.

```text
CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY,
    username TEXT NOT NULL,
    pin TEXT NOT NULL
)
```

التطبيق دايمًا بيستخدم `id = 1`، لأنه offline single-user app.

### 8.2 جدول `budget_cycle`

بيخزن active allowance cycle.

```text
CREATE TABLE IF NOT EXISTS budget_cycle (
    id INTEGER PRIMARY KEY,
    total_allowance REAL NOT NULL,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    remaining_balance REAL NOT NULL
)
```

برضه التطبيق دايمًا بيستخدم `id = 1` للـ current cycle.

### 8.3 جدول `category`

بيخزن expense categories.

```text
CREATE TABLE IF NOT EXISTS category (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT
)
```

Default categories بتتضاف لما التطبيق يبدأ.

### 8.4 جدول `expense`

بيخزن transactions اللي المستخدم سجلها.

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

`category_id` بيربط كل expense بـ category.

`cycle_id` بيربط كل expense بالـ current budget cycle.

`ON DELETE CASCADE` معناها لو budget cycle اتحذفت، الـ expenses المرتبطة بيها تتحذف معاها.

---

## 9. شرح `SQLiteManager`

`SQLiteManager` هو low-level database helper.

هو اللي ماسك JDBC code.

باقي managers بتستخدمه بدل ما نكرر connection code في كل مكان.

### 9.1 Database URL

```text
private static final String URL = "jdbc:sqlite:yallabudget.db";
```

ده بيقول لـ JDBC يستخدم SQLite ويخزن البيانات في file اسمه `yallabudget.db`.

### 9.2 Private constructor

```text
private SQLiteManager() {
    // Utility class.
}
```

ده بيمنع أي حد يعمل object زي:

```text
new SQLiteManager();
```

ليه؟

علشان كل methods في `SQLiteManager` هي static helper methods. بنستخدمه كده:

```text
SQLiteManager.createTables();
SQLiteManager.executeUpdate(...);
```

### 9.3 الاتصال بـ SQLite

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

- `public static Connection connect()` معناها أي manager يقدر ينادي method دي من غير ما يعمل `SQLiteManager` object.
- `throws SQLException` معناها إن database connection errors ممكن تحصل.
- `DriverManager.getConnection(URL)` بيفتح SQLite database file.
- `PRAGMA foreign_keys = ON` بتفعل foreign key behavior في SQLite.
- `return connection` بترجع الـ open connection للـ caller.

### 9.4 `createTables()`

الـ method دي بتنشئ كل required tables لو مش موجودة.

الجزء المهم:

```text
for (String sql : statements) {
    statement.execute(sql);
}
```

التطبيق عنده array فيها SQL table-creation statements. الـ loop دي بتشغل كل statement.

ده صلح المشكلة القديمة إن SQL كان بيتبني لكن ما بيتنفذش.

### 9.5 `executeUpdate()`

```text
public static int executeUpdate(String sql, Object... parameters)
```

الـ method دي بتستخدم مع SQL commands اللي بتغير data:

- `INSERT`
- `UPDATE`
- `DELETE`

`Object... parameters` معناها إن method تقدر تستقبل أي عدد من parameters.

مثال:

```text
SQLiteManager.executeUpdate("DELETE FROM expense WHERE id = ?", expenseId);
```

علامة `?` بتتملي بـ `expenseId`.

ده أأمن من إننا نبني SQL عن طريق string concatenation.

### 9.6 `executeQuery()`

```text
public static <T> List<T> executeQuery(String sql, ResultSetMapper<T> mapper, Object... parameters)
```

الـ method دي بتستخدم مع SQL `SELECT` statements.

بترجع `List<T>` لأن الـ query ممكن ترجع rows كتير.

الـ `mapper` بيقول للـ method تحول كل database row لـ Java object إزاي.

مثال من `BudgetManager`:

```text
rs -> new BudgetCycle(
    rs.getInt("id"),
    rs.getDouble("total_allowance"),
    LocalDate.parse(rs.getString("start_date")),
    LocalDate.parse(rs.getString("end_date")),
    rs.getDouble("remaining_balance")
)
```

ده معناه:

> لكل row راجع من SQLite، اعمل `BudgetCycle` object.

### 9.7 `exists()`

```text
public static boolean exists(String sql, Object... parameters)
```

دي بتشوف هل query رجعت row واحدة على الأقل ولا لأ.

مثال:

```text
SQLiteManager.exists("SELECT 1 FROM user WHERE id = 1")
```

لو row موجودة، بترجع `true`.

لو مفيش row، بترجع `false`.

### 9.8 `bindParameters()`

```text
private static void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException
```

دي بتملى أماكن الـ `?` في SQL.

الـ loop:

```text
for (int i = 0; i < parameters.length; i++) {
    statement.setObject(i + 1, parameters[i]);
}
```

SQL parameters بتبدأ من index `1`، مش `0`.

لكن Java arrays بتبدأ من `0`.

علشان كده بنستخدم `i + 1`.

مثال:

```text
UPDATE user SET pin = ? WHERE id = 1
```

لو parameter هي `1234`، فـ `setObject(1, "1234")` بتملى أول `?`.

### 9.9 `ResultSetMapper<T>`

```text
@FunctionalInterface
public interface ResultSetMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}
```

دي interface صغيرة بتستخدم لتحويل database rows لـ objects.

`functional interface` يعني فيها method واحدة، فممكن نستخدم معاها lambda expressions.

مثال:

```text
rs -> new Category(rs.getInt("id"), rs.getString("name"), rs.getString("description"))
```

---

## 10. شرح الـ Managers

الـ Managers هي الـ layer الوحيدة اللي المفروض تعرف أسماء tables وcolumns في database.

### 10.1 `UserManager`

`UserManager` بيحفظ ويقرأ ويحدث local user record.

```text
public boolean userExist() {
    return SQLiteManager.exists("SELECT 1 FROM user WHERE id = 1");
}
```

ده بيتأكد هل التطبيق عنده user بالفعل ولا لأ.

```text
public void saveUser(User user) {
    String sql = "INSERT OR REPLACE INTO user(id, username, pin) VALUES(1, ?, ?)";
    SQLiteManager.executeUpdate(sql, user.getName(), user.getPIN());
}
```

ده بيحفظ الـ user.

`INSERT OR REPLACE` معناها:

- لو user `id = 1` مش موجود، اعمله insert.
- لو user `id = 1` موجود، استبدله.

```text
public User loadUser()
```

دي بتقرأ الـ stored user من SQLite وترجع `User` object.

```text
return users.isEmpty() ? null : users.get(0);
```

ده معناه:

- لو مفيش user اتلاقى، رجع `null`.
- غير كده رجع أول user.

```text
public void updateUser(User user)
```

دي بتحدث الـ PIN للـ existing user.

### 10.2 `BudgetManager`

`BudgetManager` بيحفظ ويقرأ current budget cycle.

```text
public void saveCycle(BudgetCycle cycle) {
    if (cycleExist()) {
        updateCycle(cycle);
    } else {
        insertCycle(cycle);
    }
}
```

الـ method دي بتقرر تعمل insert لـ cycle جديدة ولا update للـ existing cycle.

```text
public void insertCycle(BudgetCycle cycle)
```

دي بتدخل أول active cycle في database.

```text
public void updateCycle(BudgetCycle cycle)
```

دي بتحدث current cycle بعد ما expenses تتضاف أو تتحذف.

```text
public BudgetCycle loadCycle()
```

دي بتحمل active budget cycle من database.

مهم: بما إنها بتستخدم `SELECT`، فلازم تستخدم query logic مش update logic.

```text
public void deleteCycle()
```

دي بتحذف current cycle والـ expenses بتاعتها.

الأول بتحذف expenses:

```text
SQLiteManager.executeUpdate("DELETE FROM expense WHERE cycle_id = 1");
```

وبعدين بتحذف cycle:

```text
SQLiteManager.executeUpdate("DELETE FROM budget_cycle WHERE id = 1");
```

ده بيدعم feature اسمها Reset Current Cycle.

```text
public void insertCategories()
```

دي بتضيف default categories.

`INSERT OR IGNORE` بتمنع تكرار categories.

### 10.3 `ExpenseManager`

`ExpenseManager` بيحفظ ويحدث ويحذف ويحمل expenses.

```text
public void saveExpense(Expense expense)
```

دي بتدخل expense row في SQLite.

بتحفظ:

- amount
- category id
- cycle id
- timestamp

```text
public void updateExpense(Expense expense)
```

دي بتحدث expense موجود.

بتستخدم `id` و `cycle_id` في الـ `WHERE` clause علشان ما تحدثش transaction بالغلط من cycle تانية.

```text
public void deleteExpense(Expense expense)
```

دي بتحذف expense محددة.

```text
public List<Expense> getExpensesByCycle(int cycleId)
```

دي بتحمل كل expenses الخاصة بالـ current budget cycle.

الـ SQL بيعمل join بين `expense` و `category`:

```text
FROM expense e
JOIN category c ON e.category_id = c.id
```

ليه بنعمل join؟

علشان جدول `expense` بيخزن `category_id` بس، لكن الـ UI محتاج category name زي `Food` أو `Transportation`.

فالـ manager بيعمل `Category` object الأول، وبعدين يعمل `Expense` object جواه الـ category دي.

```text
private LocalDateTime parseTimestamp(String timestamp)
```

دي بتحول timestamp string اللي جاي من SQLite لـ Java `LocalDateTime`.

لو الـ timestamp متخزن كـ date بس، بتحوله لبداية اليوم.

---

## 11. شرح الـ Services

الـ Services هي الـ middle layer بين controllers و managers.

هي اللي فيها business rules.

### 11.1 `UserService`

`UserService` بيتعامل مع PIN logic.

```text
private static final int MAX_FAILED_ATTEMPTS = 3;
```

المستخدم بيتعمله lockout بعد 3 محاولات PIN غلط.

```text
private final UserManager userManager;
```

الـ service بتستخدم `UserManager` علشان تحفظ وتقرأ user data.

```text
private int failedAttempts;
private long lockedUntilMillis;
```

دول بيتابعوا عدد المحاولات الغلط ووقت انتهاء الـ lockout.

ملاحظة مهمة: القيم دي متخزنة في memory. لو التطبيق اتقفل بالكامل واتفتح تاني، failed attempts بتتصفر.

```text
public void createUser(String username, String rawPin)
```

دي بتعمل validate للـ PIN، تعمل default username لو محتاج، تعمل hash للـ PIN، وبعدين تحفظ الـ user.

```text
String safeUsername = (username == null || username.isBlank()) ? "Student" : username.trim();
```

لو username فاضي، استخدم `Student`.

غير كده شيل spaces من أول وآخر username.

```text
userManager.saveUser(new User(safeUsername, hashPin(rawPin)));
```

الـ raw PIN مش بيتحفظ directly. اللي بيتحفظ هو الـ hashed PIN.

```text
public boolean authenticate(String rawPin)
```

دي بتتأكد إن الـ PIN اللي المستخدم دخله صح.

الـ method بتعمل الآتي:

1. ترفض empty input
2. ترفض المحاولة لو المستخدم في lockout
3. تحمل الـ user من SQLite
4. تعمل hash للـ entered PIN
5. تقارن الـ calculated hash بالـ stored hash
6. تصفر failed attempts لو صح
7. تزود failed attempts لو غلط
8. تعمل 30-second lockout بعد 3 محاولات غلط

```text
boolean valid = user.verifyPIN(hashPin(rawPin)) || user.getPIN().equals(rawPin);
```

أول جزء هو الـ secure check الحقيقي:

```text
user.verifyPIN(hashPin(rawPin))
```

الجزء التاني:

```text
user.getPIN().equals(rawPin)
```

ده معمول backward compatibility لو كان فيه data قديمة مخزنة PIN raw من غير hash.

```text
private String hashPin(String pin)
```

دي بتحول الـ PIN لـ SHA-256 hash.

ده أحسن من تخزين PIN plain text في database.

### 11.2 `BudgetService`

`BudgetService` بيتعامل مع budget cycle rules.

```text
public BudgetCycle createCycle(double allowance, LocalDate start, LocalDate end)
```

دي بتعمل cycle جديدة بعد ما تعمل validate للـ amount والـ date range.

```text
if (!validateAmount(allowance))
```

الـ allowance لازم يكون أكبر من zero.

```text
if (!validateDateRange(start, end))
```

End date لازم يكون بعد start date.

```text
BudgetCycle cycle = new BudgetCycle(allowance, start, end);
```

ده بيعمل model object جديد.

```text
budgetManager.saveCycle(cycle);
```

ده بيحفظه في SQLite.

```text
ensureDefaultCategories();
```

ده بيتأكد إن categories موجودة بعد ما cycle تتعمل.

```text
public double applyRollover(BudgetCycle cycle)
```

دي بتعيد حساب الـ daily limit بناءً على تاريخ النهارده والـ remaining balance.

هي مش بتخزن daily rollover row لوحده. بدل كده، الـ dashboard بيحسب current safe limit dynamically.

```text
public boolean checkThreshold(BudgetCycle cycle)
```

دي بترجع `true` لما المستخدم يكون صرف 80% أو أكتر من total allowance.

### 11.3 `ExpenseService`

`ExpenseService` بيتعامل مع expense rules.

```text
public boolean validateExpense(double amount, Category category)
```

الـ expense تبقى valid لو:

- amount > 0
- category selected

```text
public Expense addExpense(double amount, Category category)
```

دي controller بيناديها لما المستخدم يعمل submit للفورم.

هي بتحمل current cycle، تعمل `Expense`، وبعدين تنادي overloaded method:

```text
addExpense(cycle, expense);
```

```text
public void addExpense(BudgetCycle cycle, Expense expense)
```

الـ method دي بتعمل:

1. validate للـ cycle
2. validate للـ expense
3. تخصم expense amount من cycle balance
4. تحفظ الـ expense
5. تحفظ الـ updated cycle

```text
cycle.addExpense(expense);
```

ده بيحدث object في memory عن طريق خصم expense من remaining balance.

```text
expenseManager.insertExpense(expense);
```

ده بيحفظ transaction في SQLite.

```text
budgetManager.saveCycle(cycle);
```

ده بيحفظ الـ updated remaining balance.

```text
private void recalculateBalanceFromExpenses(int cycleId)
```

دي بتعيد حساب remaining balance بعد editing أو deleting expenses.

بتعيد تحميل كل expenses، تجمعهم، وتحط:

```text
remaining balance = total allowance - total spent
```

### 11.4 `DashboardService`

`DashboardService` بيجهز values بتاعة dashboard.

```text
public double getRemainingBalance(BudgetCycle cycle)
```

بترجع remaining balance من cycle.

```text
public double getDailyLimit(BudgetCycle cycle)
```

بترجع calculated daily limit.

```text
public double getTotalSpending(BudgetCycle cycle)
```

بترجع كام فلوس اتصرفت.

```text
public Map<String, Double> calculateCategoryTotals(int cycleId)
```

دي بتجمع expenses حسب category name وتحسب total لكل category.

مثال result:

```text
Food -> 150.0
Transportation -> 70.0
Entertainment -> 30.0
```

```text
public List<ChartData> prepareChartData(int cycleId)
```

دي بتحول الـ map لـ list من `ChartData` objects علشان controller يبني pie chart.

### 11.5 `HistoryService`

`HistoryService` بيتعامل مع history filtering و sorting.

```text
public List<Expense> getTransactions(int cycleId)
```

بتحمل كل expenses لـ cycle وترتبهم من الأحدث للأقدم.

```text
public List<Expense> sortTransactionsByDate(List<Expense> expenses)
```

بترتب expenses حسب timestamp descending.

```text
public List<Expense> filterByCategory(List<Expense> expenses, int categoryId)
```

بتخلي بس expenses اللي category بتاعتها match الـ selected category.

```text
public List<Expense> filterByDateRange(List<Expense> expenses, LocalDate from, LocalDate to)
```

بتخلي بس expenses اللي بين الـ selected dates.

لو `from` فاضي، مش بتعمل filter من ناحية start date.

لو `to` فاضي، مش بتعمل filter من ناحية end date.

---

## 12. شرح الـ Models

### 12.1 `User`

`User` بيمثل local app user الوحيد.

```text
private String userName;
private String pinHash;
```

الـ user عنده name وstored PIN hash.

```text
public boolean verifyPIN(String calculatedHash)
```

دي بتقارن الـ stored hash بالـ calculated hash اللي جاي من PIN اللي المستخدم دخله.

### 12.2 `BudgetCycle`

`BudgetCycle` بيمثل current budget period.

```text
private int id;
private double totalAllowance;
private LocalDate startDate;
private LocalDate endDate;
private double remainingBalance;
private final List<Expense> expenses = new ArrayList<>();
```

بيخزن:

- cycle id
- total allowance
- start date
- end date
- remaining balance
- expenses اللي اتضافت جوه runtime object

```text
public int calculateRemainingDays()
```

دي بتحسب كام يوم باقي في cycle.

بتستخدم تاريخ النهارده.

```text
public double calculateDailyLimit()
```

دي بترجع:

```text
remaining balance / remaining days
```

```text
public void updateRemainingBalance(double amount)
```

دي بتخصم expense من remaining balance.

وبتمنع القيمة إنها تنزل تحت zero.

```text
public double getPercentageSpending()
```

دي بتحسب spending percentage:

```text
(total spent / total allowance) * 100
```

دي بتستخدم في 80% warning.

### 12.3 `Category`

`Category` بيمثل expense categories.

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

دي مهمة للـ combo box.

لما JavaFX يعرض `Category` object في `ComboBox<Category>`، بينادي `toString()` علشان يعرف يعرض إيه.

فـ combo box هيعرض `Food` بدل ما يعرض حاجة زي:

```text
com.mazenfahim.YallaBudget.Model.Category@4f3f5b24
```

### 12.4 `Expense`

`Expense` بيمثل transaction واحدة.

```text
private int id;
private double amount;
private LocalDateTime timestamp;
private Category category;
private int cycleId;
```

بيخزن:

- database id
- amount spent
- timestamp
- selected category
- cycle id

```text
this.timestamp = LocalDateTime.now();
```

لما expense جديدة تتعمل، الـ timestamp بيتحط current date and time automatic.

---

## 13. شرح الـ Controllers

### 13.1 `PinSetupController`

ده بيتحكم في `PinSetupView.fxml`.

Important fields:

```text
@FXML private TextField usernameInput;
@FXML private PasswordField pinInput;
@FXML private PasswordField confirmPinInput;
@FXML private Label errorLabel;
```

دول مربوطين بـ FXML fields بنفس أسماء `fx:id`.

```text
private final UserService userService = new UserService();
private final BudgetService budgetService = new BudgetService();
```

الـ controller بيعمل services علشان تعمل الشغل الحقيقي.

هو ما بيتكلمش مع SQLite directly.

```text
public void onCreatePinClicked()
```

دي بتشتغل لما المستخدم يضغط `Create PIN`.

Flow:

1. اقرأ username
2. اقرأ PIN
3. اقرأ confirm PIN
4. اتأكد إن PIN مش فاضي
5. اتأكد إن PINs match
6. نادي `userService.createUser(username, pin)`
7. روح dashboard لو cycle موجودة، أو setup لو مفيش cycle

```text
navigateTo(budgetService.cycleExists() ? "DashboardView.fxml" : "SetupView.fxml");
```

ده معناه:

- لو budget cycle موجودة، افتح dashboard.
- لو مفيش budget cycle، افتح setup.

### 13.2 `PinUnlockController`

ده بيتحكم في `PinUnlockView.fxml`.

```text
public void onUnlockClicked()
```

دي بتشتغل لما المستخدم يضغط `Unlock`.

Flow:

1. اتأكد هل المستخدم في lockout ولا لأ
2. نادي `userService.authenticate(pinInput.getText())`
3. لو صح، روح dashboard أو setup
4. لو غلط، اعرض remaining attempts

```text
int attemptsLeft = Math.max(0, 3 - userService.getFailedAttempts());
```

ده بيحسب كام محاولة فاضلة.

`Math.max(0, ...)` بتمنع الرقم يبقى negative.

### 13.3 `SetupController`

ده بيتحكم في `SetupView.fxml` و `BudgetSetupView.fxml`.

```text
public void initialize() {
    errorLabel.setText("");
    startDatePicker.setValue(LocalDate.now());
    endDatePicker.setValue(LocalDate.now().plusDays(30));
}
```

لما الشاشة تفتح:

- امسح error label
- خلي start date النهارده
- خلي end date بعد 30 يوم من النهارده

```text
public void onCreateCycleClicked()
```

دي بتشتغل لما المستخدم يضغط `Start Cycle`.

Flow:

1. اقرأ allowance من text field
2. حوله من String لـ double
3. اقرأ start و end dates
4. اعمل validate باستخدام `validateInput()`
5. نادي `budgetService.createCycle(...)`
6. افتح dashboard

```text
double allowance = Double.parseDouble(allowanceInput.getText().trim());
```

ده بيقرأ text من input field.

`trim()` بتشيل spaces من أول وآخر النص.

مثال:

```text
" 3000 " -> "3000"
```

`Double.parseDouble(...)` بتحول النص لرقم.

لو المستخدم كتب `abc`، Java هترمي `NumberFormatException`، والـ controller هيعرض error.

```text
public void onStartCycleClicked() {
    onCreateCycleClicked();
}
```

دي alias.

موجودة علشان بعض diagrams أو FXML versions ممكن تستخدم `onStartCycleClicked`، بينما الزرار الحالي ممكن يستخدم `onCreateCycleClicked`.

الاتنين بيعملوا نفس الحاجة.

### 13.4 `DashboardController`

ده بيتحكم في `DashboardView.fxml`.

Important fields:

```text
@FXML private Label remainingBalanceLabel;
@FXML private Label dailyLimitLabel;
@FXML private Label totalSpendingLabel;
@FXML private PieChart categoryPieChart;
@FXML private Label thresholdWarningLabel;
```

دي القيم اللي بتظهر على dashboard.

```text
public void initialize() {
    openDashboard();
}
```

لما dashboard تفتح، بيحمل كل dashboard values automatically.

```text
BudgetCycle cycle = budgetService.getCurrentCycleData();
```

ده بيحمل current cycle من SQLite.

```text
if (cycle == null) { ... return; }
```

لو مفيش active cycle، dashboard هتعرض placeholder values وتوقف.

```text
budgetService.applyRollover(cycle);
```

دي بتعيد حساب daily limit بناءً على النهارده والـ remaining balance.

```text
remainingBalanceLabel.setText(formatMoney(dashboardService.getRemainingBalance(cycle)));
```

ده بيحدث label بقيمة money formatted.

```text
displayChart(cycle.getId());
```

ده بيحمل chart data ويملى pie chart.

```text
if (budgetService.checkThreshold(cycle))
```

لو spending 80% أو أكتر، اعرض warning.

Button methods:

```text
onAddExpenseClicked() -> ExpenseEntryView.fxml
onHistoryClicked()    -> HistoryView.fxml
onSettingsClicked()   -> SettingsView.fxml
onSetupClicked()      -> SetupView.fxml
```

### 13.5 `ExpenseEntryController`

ده بيتحكم في `ExpenseEntryView.fxml`.

```text
public void initialize()
```

لما الشاشة تفتح، بيعمل:

1. يمسح error label
2. يحمل categories من database
3. يحط categories في combo box

```text
categoryComboBox.setItems(FXCollections.observableArrayList(categories));
```

JavaFX combo boxes بتستخدم observable lists، فالسطر ده بيحول Java `List<Category>` عادية لـ JavaFX-friendly list.

```text
public void onSubmitExpenseClicked()
```

دي بتشتغل لما المستخدم يضغط `Save Expense`.

Flow:

1. parse amount
2. هات selected category
3. validate باستخدام `ExpenseService`
4. add expense من خلال `ExpenseService`
5. ارجع dashboard

```text
Category selectedCategory = categoryComboBox.getValue();
```

ده بيرجع selected category object.

```text
expenseService.addExpense(amount, selectedCategory);
```

هنا الشغل الحقيقي بيحصل:

- create expense
- save expense
- update remaining balance

### 13.6 `HistoryController`

ده بيتحكم في `HistoryView.fxml`.

بيتعامل مع:

- عرض transactions
- filter by category
- filter by date range
- delete selected transaction

```text
private final Map<String, Integer> categoryNameToId = new HashMap<>();
```

الـ filter combo box بتخزن category names كـ strings.

لكن filtering محتاج category IDs.

فـ map دي بتربطهم:

```text
"Food" -> 1
"Transportation" -> 2
```

```text
public void initialize()
```

لما history تفتح، بيعمل:

1. يعمل table columns
2. يحمل category filter options
3. يحمل transaction history
4. يعطل delete button لما مفيش row selected

```text
deleteButton.disableProperty().bind(expenseTableView.getSelectionModel().selectedItemProperty().isNull());
```

دي JavaFX binding.

معناها:

> خلي delete button disabled لما مفيش selected transaction.

لما المستخدم يختار row، الزرار يتفعل automatic.

```text
private void setupTableColumns()
```

دي بتعمل table columns في Java code بدل FXML.

كل column بتستخدم `setCellValueFactory` علشان تقول لـ JavaFX يعرض value إيه من كل `Expense` object.

مثال:

```text
amountColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f EGP", data.getValue().getAmount())));
```

ده معناه:

> لكل expense row، اعرض amount formatted كفلوس.

```text
private void loadAndDisplayExpenses()
```

دي الـ main method لتحديث history table.

Flow:

1. load current cycle
2. load all expenses for the cycle
3. apply category filter لو selected
4. apply date range filter
5. sort by latest
6. حط النتيجة في table
7. اعرض message لو فاضية

```text
expenseTableView.setItems(FXCollections.observableArrayList(expenses));
```

ده بيحدث table بالـ filtered expenses.

```text
public void onFilterChanged()
```

دي بتتنادى لما category/date filters يتغيروا.

ببساطة بتعيد تحميل table باستخدام الـ selected filters.

```text
public void onDeleteExpenseClicked()
```

دي بتحذف selected row.

Flow:

1. هات selected expense
2. لو مفيش selected، اعرض message
3. نادي `expenseService.deleteExpense(selectedExpense)`
4. reload table
5. اعرض success message

### 13.7 `SettingController`

ده بيتحكم في `SettingsView.fxml`.

بيتعامل مع:

- تغيير PIN
- reset cycle
- الرجوع للـ dashboard

```text
public void onChangePinClicked()
```

Flow:

1. اقرأ old PIN
2. اقرأ new PIN
3. اقرأ confirm PIN
4. اتأكد إن new PIN confirmation صح
5. نادي `userService.changePin(oldPin, newPin)`
6. امسح fields
7. اعرض success message

```text
public void onResetCycleClicked()
```

دي بتفتح confirmation alert قبل ما تحذف أي حاجة.

```text
Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
```

ده بيعمل confirmation dialog.

```text
Optional<ButtonType> result = alert.showAndWait();
```

ده بيعرض dialog ويستنى إجابة المستخدم.

```text
if (result.isPresent() && result.get() == ButtonType.OK)
```

ده بيتأكد إن المستخدم ضغط OK.

لو آه:

```text
budgetService.resetCurrentCycle();
navigateTo("SetupView.fxml");
```

هيحذف current cycle ويرجع setup.

### 13.8 `SettingsController`

```text
public class SettingsController extends SettingController {
}
```

الـ class دي مجرد compatibility wrapper.

الـ implementation الحقيقي موجود في `SettingController`.

ليه نخليها؟

علشان بعض diagrams أو team members ممكن يستخدموا الاسم plural `SettingsController`، بينما الـ SDS ممكن يستخدم `SettingController`.

الـ class دي بتخلي الاسمين compile.

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

لما المستخدم يعمل PIN:

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

لما المستخدم يدخل PIN:

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

## 15. ليه Controllers بتعمل Services directly؟

مثال:

```text
private final BudgetService budgetService = new BudgetService();
```

ده معناه إن الـ controller عنده service object.

لما الـ controller يحتاج budget logic، بيسأل الـ service.

بالنسبة لـ course project ده، الأسلوب ده بسيط ومقبول.

في applications احترافية أكبر، ممكن نستخدم dependency injection، بس ده هيضيف complexity مش محتاجينها هنا.

---

## 16. ليه Services بتعمل Managers؟

مثال من `ExpenseService`:

```text
public ExpenseService() {
    this(new ExpenseManager(), new BudgetManager());
}
```

الـ default constructor بيعمل managers اللي محتاجها.

ده بيخلي الـ controller يكتب ببساطة:

```text
new ExpenseService();
```

من غير ما يقلق بخصوص manager objects.

فيه كمان constructors زي:

```text
public ExpenseService(ExpenseManager expenseManager, BudgetManager budgetManager)
```

دي بتخلي الكود أسهل في testing أو modification بعدين، لأنك ممكن تبعت custom manager objects لو احتجت.

---

## 17. ليه Managers بتستخدم `SQLiteManager`؟

من غير `SQLiteManager`، كل manager هيكرر كود زي:

```text
Connection connection = DriverManager.getConnection(...);
PreparedStatement statement = connection.prepareStatement(...);
statement.setObject(...);
statement.executeUpdate();
```

بدل كده، جمعنا الكود ده في class واحدة.

فـ managers تركز على هدف الـ SQL بتاعها بس.

مثال:

```text
SQLiteManager.executeUpdate(sql, expense.getAmount(), expense.getCategory().getId(), ...);
```

ده بيخلي الكود أوضح وبيقلل التكرار.

---

## 18. ليه بنستخدم `try/catch` في Controllers؟

مثال:

```text
try {
    double amount = Double.parseDouble(amountInput.getText().trim());
    ...
} catch (NumberFormatException e) {
    showValidationError("Please enter a valid number.");
}
```

المستخدم ممكن يكتب input غلط.

لو التطبيق ما مسكش الخطأ ده، ممكن ي crash.

بدل كده، الـ controller بيمسك exception ويعرض friendly error message.

Common exceptions:

- `NumberFormatException`: المستخدم كتب text مش رقم في مكان مطلوب فيه رقم.
- `IllegalArgumentException`: الـ service رفض input مخالف للـ business rules.
- `IllegalStateException`: فيه required state مش موجود، زي إن مفيش active cycle.
- `IOException`: navigation لملف FXML فشلت.

---

## 19. ليه بعض methods ليها compatibility names؟

في `BudgetCycle` فيه methods زي:

```text
public double getTotal_Allowance()
public void UpdateRemainingBalance(Double amount)
public void AddExpense(Expense expense)
```

الأسماء دي مش أفضل Java style، لأن Java methods غالبًا بتستخدم lower camelCase.

الأفضل:

```text
getTotalAllowance()
updateRemainingBalance(...)
addExpense(...)
```

لكن اتسابوا علشان old code أو diagrams أو previous class tables ما يتكسروش فجأة.

الـ cleaner methods موجودة برضه ويفضل استخدامها.

---

## 20. شرح `module-info.java`

```text
module com.mazenfahim.YallaBudget {
```

ده بيعلن اسم Java module.

```text
requires javafx.controls;
requires javafx.fxml;
requires java.sql;
requires org.xerial.sqlitejdbc;
```

السطور دي بتعلن dependencies:

- `javafx.controls`: buttons, labels, tables, pie charts, etc.
- `javafx.fxml`: تحميل ملفات FXML.
- `java.sql`: JDBC database interfaces.
- `org.xerial.sqlitejdbc`: SQLite JDBC driver.

```text
opens com.mazenfahim.YallaBudget to javafx.fxml;
opens com.mazenfahim.YallaBudget.Controller to javafx.fxml;
```

ده بيسمح لـ JavaFX reflection يوصل لـ controller fields و methods اللي عليها `@FXML`.

ده مهم لأن كتير من controller fields بتكون `private`.

JavaFX محتاج permission علشان يعمل injection ليهم.

```text
exports com.mazenfahim.YallaBudget;
exports com.mazenfahim.YallaBudget.Controller;
exports com.mazenfahim.YallaBudget.Service;
exports com.mazenfahim.YallaBudget.Manager;
exports com.mazenfahim.YallaBudget.Model;
```

ده بيخلي packages دي visible برا الـ module.

---

## 21. شرح مختصر لـ `pom.xml`

`pom.xml` هو Maven project configuration file.

Important dependencies:

```text
<artifactId>javafx-controls</artifactId>
```

مطلوبة لـ JavaFX controls زي Button, Label, TextField, PieChart, TableView.

```text
<artifactId>javafx-fxml</artifactId>
```

مطلوبة لتحميل ملفات FXML.

```text
<artifactId>sqlite-jdbc</artifactId>
```

مطلوبة علشان Java تتصل بـ SQLite.

Important plugin:

```text
<artifactId>javafx-maven-plugin</artifactId>
```

ده بيخليك تشغل التطبيق بـ:

```text
mvn javafx:run
```

Main class:

```text
<mainClass>com.mazenfahim.YallaBudget/com.mazenfahim.YallaBudget.Launcher</mainClass>
```

ده بيقول لـ Maven:

```text
Module: com.mazenfahim.YallaBudget
Class:  com.mazenfahim.YallaBudget.Launcher
```

---

## 22. Common Questions

### Q1: ليه بنستخدم `allowanceInput.getScene().getWindow()`؟

علشان الـ controller محتاج current window علشان يغير الـ scene بتاعتها.

`allowanceInput` مجرد known UI node موجود على current screen.

بنستخدمه علشان نوصل لـ:

```text
TextField -> Scene -> Window/Stage
```

إحنا مش بنحول input لـ stage.

### Q2: ينفع نستخدم field تاني بدل `allowanceInput`؟

آه ينفع.

أي field موجود في current screen يشتغل.

مثلاً في dashboard بنستخدم:

```text
remainingBalanceLabel.getScene().getWindow()
```

وفي expense screen بنستخدم:

```text
amountInput.getScene().getWindow()
```

الـ field هو مجرد طريق للـ current window.

### Q3: ليه ما نعملش `Stage` جديدة كل مرة؟

علشان ده هيفتح window جديدة لكل screen.

إحنا عايزين application window واحدة وصفحات متعددة جواها.

فبنستخدم:

```text
stage.setScene(scene);
```

بدل:

```text
new Stage().show();
```

### Q4: ليه controllers ما بتكلمش SQL directly؟

علشان controllers المفروض تتحكم في UI بس.

لو controller كمان فيه SQL، الكود هيبقى messy وصعب maintenance.

الـ clean flow هو:

```text
Controller -> Service -> Manager -> SQLiteManager
```

### Q5: ليه services موجودة لو managers أصلًا بتحفظ data؟

Managers تعرف database operations بس.

Services تعرف business rules.

مثال:

- Manager يقدر يدخل expense.
- Service تعرف إن amount لازم يبقى positive وإن budget balance لازم يتحدث بعد إدخال expense.

### Q6: ليه `BudgetCycle` بيحسب daily limit؟

علشان daily limit مرتبط directly بـ cycle data:

```text
remaining balance / remaining days
```

فمنطقي إن `BudgetCycle` object يبقى عارف يحسبه.

### Q7: ليه التطبيق بيستخدم `id = 1` للـ user والـ budget cycle؟

علشان ده offline single-user app.

فيه local user واحد و active budget cycle واحدة.

فـ implementation مبسط عن طريق استخدام row `1` دايمًا.

---

## 23. إزاي تشرح المشروع للـ TA

شرح بسيط كويس:

> التطبيق بيستخدم layered architecture. ملفات JavaFX FXML بتمثل الـ views. الـ Controllers بتتعامل مع user interaction والـ navigation. الـ Services فيها business logic زي validation و PIN authentication و daily limit calculation و threshold checks و expense balance updates. الـ Managers مسؤولة عن persistence وبتتكلم مع SQLite من خلال `SQLiteManager` مركزي. الـ Models بتمثل system entities زي `User`, `BudgetCycle`, `Expense`, و `Category`. التطبيق fully offline وبيخزن كل البيانات locally في SQLite.

Feature mapping:

- PIN setup/unlock: `PinSetupController`, `PinUnlockController`, `UserService`, `UserManager`, `User`
- Budget setup: `SetupController`, `BudgetService`, `BudgetManager`, `BudgetCycle`
- Expense logging: `ExpenseEntryController`, `ExpenseService`, `ExpenseManager`, `Expense`, `Category`
- Dashboard: `DashboardController`, `DashboardService`, `BudgetService`, `ChartData`
- History/filter/delete: `HistoryController`, `HistoryService`, `ExpenseService`, `ExpenseManager`
- Reset/change PIN: `SettingController`, `BudgetService`, `UserService`

---

## 24. ترتيب قراءة مقترح لأي Developer جديد

ما تبدأش تفتح كل الملفات عشوائي.

امشي بالترتيب ده:

1. `YallaBudgetApplication.java` — افهم startup.
2. `PinSetupView.fxml` + `PinSetupController.java` — افهم connection بين FXML و controller.
3. `SetupView.fxml` + `SetupController.java` — افهم input reading و validation.
4. `BudgetService.java` + `BudgetManager.java` + `BudgetCycle.java` — افهم cycle creation.
5. `ExpenseEntryView.fxml` + `ExpenseEntryController.java` — افهم expense input.
6. `ExpenseService.java` + `ExpenseManager.java` + `Expense.java` — افهم saving expenses.
7. `DashboardController.java` + `DashboardService.java` — افهم summaries والـ charts.
8. `HistoryController.java` + `HistoryService.java` — افهم table والـ filters والـ deletion.
9. `SQLiteManager.java` — افهم كل SQL بيتنفذ إزاي.
10. `module-info.java` و `pom.xml` — افهم project dependencies.

---

## 25. Final Mental Model

دايمًا افتكر السلسلة دي:

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

وبالنسبة للـ navigation:

```text
Known UI field on current screen
    ↓ getScene()
Current scene
    ↓ getWindow()
Current application window / Stage
    ↓ setScene(newScene)
New screen appears
```

ده هو core بتاع التطبيق كله.
