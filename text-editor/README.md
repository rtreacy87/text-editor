# Text Editor

A simple text editor built with Java and JavaFX, following the Model-View-Controller (MVC) architecture.

## Features

- Create, open, save, and edit text files
- Cut, copy, paste, and delete text
- Line numbering
- Word wrap toggle
- Status bar with cursor position and file information
- Recent files menu
- File modification tracking

## Requirements

- Java 17 or later
- JavaFX 21.0.2 or later

## Running the Application

### Using the Run Script (Recommended for macOS)

The easiest way to run the application on macOS is to use the provided run script:

```bash
./run.sh
```

This script will automatically use the bundled JavaFX SDK to run the application, avoiding compatibility issues with JavaFX on macOS.

### Using Maven

You can also run the application using Maven:

```bash
mvn clean javafx:run
```

Note: This requires Maven to be installed on your system. On macOS, this method may encounter JavaFX compatibility issues.

### Building an Executable JAR

To build an executable JAR file:

```bash
mvn clean package
```

The JAR file will be created in the `target` directory. The application uses a Launcher class as the main entry point for the executable JAR.

## Project Structure

The project follows the Model-View-Controller (MVC) architecture:

### Model

Contains the data and business logic:

- `Document`: Represents a text document with methods for managing content, file association, and modification state
- `TextBuffer`: Stores and manipulates text content with methods for inserting, deleting, and retrieving text
- `Cursor`: Manages cursor position and text selection

### View

Contains the user interface components:

- `MainWindow`: The main application window that contains all other UI components
- `EditorTextArea`: A specialized text area for editing text with methods for line and column tracking
- `EditorMenuBar`: The menu bar with File, Edit, View, and Help menus
- `StatusBar`: Displays information about cursor position and file encoding
- `LineNumberArea`: Displays line numbers next to the text area

### Controller

Contains the application logic:

- `DocumentController`: Manages the interaction between the document model and the UI
- `FileController`: Handles file operations like opening, saving, and tracking recent files

## Module System

The application uses the Java Platform Module System (JPMS) with a `module-info.java` file that defines the required dependencies and exports.

## Development

### Building from Source

To build the project from source:

```bash
mvn clean compile
```

### JavaFX Compatibility

The application includes special handling for JavaFX compatibility on macOS:

1. A bundled JavaFX SDK (version 21.0.2) is included in the project
2. The run script sets up the correct module path and options for running the application
3. Special JVM options are used to allow access to internal JavaFX classes

## Java Basics Wiki

This section explains fundamental Java concepts using examples from the text editor codebase.

### Access Modifiers

#### Public vs Private

**Public** members are accessible from any other class, while **private** members are only accessible within the same class.

Example from `TextBuffer.java`:
```java
// Public method - can be called from any class
public String getText() {
    return buffer.toString();
}

// Private field - only accessible within TextBuffer class
private StringBuilder buffer;
```

#### Protected

**Protected** members are accessible within the same package and by subclasses.

#### Package-Private (Default)

When no access modifier is specified, the member has "package-private" access, meaning it's only accessible within the same package.

### Method Return Types

#### void

**void** indicates that a method doesn't return any value.

Example from `Document.java`:
```java
// This method performs an action but doesn't return any value
public void setModified(boolean modified) {
    boolean oldValue = this.modified;
    this.modified = modified;
    if (oldValue != modified) {
        notifyModificationStateChanged();
    }
}
```

#### Return Types

Methods can return values of any type (primitive or object).

Example from `Document.java`:
```java
// Returns a String value
public String getFileName() {
    return file != null ? file.getName() : "Untitled";
}

// Returns a boolean value
public boolean isModified() {
    return modified;
}
```

### static Keyword

**static** members belong to the class itself rather than to instances of the class.

Example from `TextEditorApp.java`:
```java
// Static method - called on the class, not on an instance
public static void main(String[] args) {
    launch(args);
}
```

### Importing Packages

Imports allow you to use classes from other packages without fully qualifying their names.

Example from `MainWindow.java`:
```java
// Import specific classes
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

// Import all classes from a package (not recommended for large packages)
import javafx.stage.*;
```

To use a class, you must either:
1. Import it explicitly
2. Use its fully qualified name (e.g., `java.io.File` instead of just `File`)
3. Use a class from the `java.lang` package, which is imported automatically

### Naming Conventions

#### Classes and Interfaces
- Use PascalCase (capitalize first letter of each word)
- Examples: `Document`, `TextBuffer`, `DocumentController`

#### Methods and Variables
- Use camelCase (first word lowercase, capitalize first letter of subsequent words)
- Examples: `getText()`, `setModified()`, `isNew()`

#### Constants
- Use UPPER_SNAKE_CASE (all uppercase with underscores)
- Example from `FileController.java`:
```java
private static final int MAX_RECENT_FILES = 5;
private static final String RECENT_FILES_KEY = "recentFiles";
```

#### Packages
- Use all lowercase, with dots separating hierarchy levels
- Examples: `com.texteditor`, `com.texteditor.model`

### Identifying Imported Functions

To determine where a method comes from:

1. **Instance Methods**: Look at the object's type
   ```java
   // The setText method comes from the TextBuffer class
   textBuffer.setText(text);
   ```

2. **Static Methods**: Look at the class name
   ```java
   // The launch method comes from the Application class
   Application.launch(args);
   ```

3. **Inherited Methods**: Check the class hierarchy
   ```java
   // The setTitle method is inherited from the Stage class in JavaFX
   stage.setTitle("Text Editor");
   ```

4. **IDE Support**: Most IDEs allow you to Ctrl+click (or Cmd+click) on a method name to navigate to its definition

### Object-Oriented Concepts

#### Classes and Objects

A **class** is a blueprint, while an **object** is an instance of a class.

Example from `TextEditorApp.java`:
```java
// Document is a class, document is an object instance
document = new Document();
```

#### Inheritance

**Inheritance** allows a class to inherit properties and methods from another class.

Example from `EditorTextArea.java`:
```java
// EditorTextArea inherits from TextArea
public class EditorTextArea extends TextArea {
    // ...
}
```

#### Interfaces

An **interface** defines a contract that classes can implement.

Example from `Document.java`:
```java
// DocumentListener is an interface
public interface DocumentListener {
    void onContentChanged(Document document);
    void onModificationStateChanged(Document document);
    void onFileChanged(Document document);
}
```

### Exception Handling

Java uses try-catch blocks to handle exceptions.

Example from `FileController.java`:
```java
try {
    String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
    document.setText(content);
    document.setFile(file);
    document.resetModified();
    addRecentFile(file.getAbsolutePath());
} catch (IOException e) {
    showErrorDialog("Error Opening File", "Could not open file: " + file.getName(), e.getMessage());
}
```

## License

This project is open source and available under the MIT License.
