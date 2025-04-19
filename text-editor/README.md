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

## License

This project is open source and available under the MIT License.
