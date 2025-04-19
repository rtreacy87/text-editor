# Text Editor

A simple text editor built with Java and JavaFX.

## Features

- Create, open, save, and edit text files
- Cut, copy, paste, and delete text
- Line numbering
- Word wrap
- Status bar with cursor position and file information
- Recent files menu

## Requirements

- Java 17 or later
- JavaFX 21.0.2 or later

## Running the Application

### Using the Run Script

The easiest way to run the application is to use the provided run script:

```bash
./run.sh
```

This script will automatically use the bundled JavaFX SDK to run the application.

### Using Maven

You can also run the application using Maven:

```bash
mvn clean javafx:run
```

Note: This requires Maven to be installed on your system.

### Building an Executable JAR

To build an executable JAR file:

```bash
mvn clean package
```

The JAR file will be created in the `target` directory.

## Development

### Project Structure

The project follows the Model-View-Controller (MVC) architecture:

- **Model**: Contains the data and business logic
  - `Document`: Represents a text document
  - `TextBuffer`: Stores and manipulates text
  - `Cursor`: Manages cursor position and selection

- **View**: Contains the user interface components
  - `MainWindow`: The main application window
  - `EditorTextArea`: The text editing component
  - `EditorMenuBar`: The menu bar
  - `StatusBar`: Displays information about the document
  - `LineNumberArea`: Displays line numbers

- **Controller**: Contains the application logic
  - `DocumentController`: Manages the document model
  - `FileController`: Handles file operations

### Building from Source

To build the project from source:

```bash
mvn clean compile
```

## License

This project is open source and available under the MIT License.
