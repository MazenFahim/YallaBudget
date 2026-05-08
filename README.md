# YallaBudget

A JavaFX desktop app for tracking budget cycles, categories, and expenses. Data is stored locally in SQLite.

## What this app does
- Set up a budget cycle with a total allowance
- Track expenses by category
- View dashboard summaries and history
- Persist data locally in SQLite

## Tech stack and tools (detected)
- Java 21 (source/target set in `pom.xml`)
- Maven (with Maven Wrapper: `mvnw`, `mvnw.cmd`)
- JavaFX 21.0.6 (`javafx-controls`, `javafx-fxml`)
- SQLite JDBC 3.47.1.0 (`org.xerial:sqlite-jdbc`)
- JUnit 5 (test dependencies)
- Plugins: `javafx-maven-plugin`, `maven-compiler-plugin`, `maven-javadoc-plugin`

## Quick start (Windows PowerShell)

From the project root `D:\Training\applications\YallaBudget`:

```powershell
# Run the app
.\mvnw.cmd clean javafx:run
```

If you have Maven installed globally, you can use:

```powershell
mvn clean javafx:run
```

## Build

```powershell
.\mvnw.cmd clean package
```

## Database
- The app uses a local SQLite database file named `yallabudget.db` in the working directory.
- See `SQLiteDatabase_README.md` for schema details and database behavior.

## Project layout
- `src/main/java/com/mazenfahim/YallaBudget` — application code
- `src/main/java/com/mazenfahim/YallaBudget/controller` — JavaFX controllers
- `src/main/java/com/mazenfahim/YallaBudget/model` — domain and database models
- `src/main/resources/com/mazenfahim/YallaBudget` — FXML views and CSS
- `pom.xml` — dependencies, plugins, build configuration
