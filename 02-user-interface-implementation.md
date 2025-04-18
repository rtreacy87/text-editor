# User Interface Implementation

This guide will walk you through implementing the user interface for your text editor. We'll create a clean, functional UI that includes all the essential components of a text editor.

## Table of Contents
- [Overview of the UI Components](#overview-of-the-ui-components)
- [Creating the Main Window](#creating-the-main-window)
- [Implementing the Menu Bar](#implementing-the-menu-bar)
- [Adding a Text Area](#adding-a-text-area)
- [Creating a Status Bar](#creating-a-status-bar)
- [Designing Dialog Boxes](#designing-dialog-boxes)
- [Putting It All Together](#putting-it-all-together)
- [Testing the UI](#testing-the-ui)

## Overview of the UI Components

A typical text editor UI consists of several key components:

1. **Main Window**: The container for all other UI elements
2. **Menu Bar**: Contains dropdown menus for various commands
3. **Toolbar**: Provides quick access to common commands (optional for MVP)
4. **Text Area**: The main editing area where text is displayed and edited
5. **Status Bar**: Displays information about the document and editor state
6. **Dialog Boxes**: Pop-up windows for specific operations (open, save, find, etc.)

Let's implement each of these components one by one.

## Creating the Main Window

The main window serves as the container for all other UI elements. In JavaFX, this is represented by a `Stage` object.

### Step 1: Set Up the Basic Window

Create a new file `MainWindow.java` in the `src/main/java/com/texteditor/view` directory:

```java
package com.texteditor.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainWindow {
    private Stage stage;
    private BorderPane rootLayout;
    
    public MainWindow(Stage stage) {
        this.stage = stage;
        initializeUI();
    }
    
    private void initializeUI() {
        // Create the root layout (BorderPane is ideal for text editors)
        rootLayout = new BorderPane();
        
        // Create a scene with the root layout
        Scene scene = new Scene(rootLayout, 800, 600);
        
        // Configure the stage
        stage.setTitle("Text Editor");
        stage.setScene(scene);
        
        // Set minimum size constraints
        stage.setMinWidth(400);
        stage.setMinHeight(300);
    }
    
    public void show() {
        stage.show();
    }
    
    public BorderPane getRootLayout() {
        return rootLayout;
    }
}
```

### Step 2: Update the Main Application Class

Now, update the `TextEditorApp.java` file to use our new `MainWindow` class:

```java
package com.texteditor;

import com.texteditor.view.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class TextEditorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create and show the main window
        MainWindow mainWindow = new MainWindow(primaryStage);
        mainWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

## Implementing the Menu Bar

The menu bar provides access to all the commands and functions of the text editor.

### Step 1: Create the Menu Bar Class

Create a new file `EditorMenuBar.java` in the `src/main/java/com/texteditor/view` directory:

```java
package com.texteditor.view;

import javafx.scene.control.*;

public class EditorMenuBar extends MenuBar {
    
    public EditorMenuBar() {
        createFileMenu();
        createEditMenu();
        createViewMenu();
        createHelpMenu();
    }
    
    private void createFileMenu() {
        Menu fileMenu = new Menu("File");
        
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open...");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveAsItem = new MenuItem("Save As...");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem exitItem = new MenuItem("Exit");
        
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, separator, exitItem);
        
        this.getMenus().add(fileMenu);
    }
    
    private void createEditMenu() {
        Menu editMenu = new Menu("Edit");
        
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem deleteItem = new MenuItem("Delete");
        SeparatorMenuItem separator2 = new SeparatorMenuItem();
        MenuItem selectAllItem = new MenuItem("Select All");
        
        editMenu.getItems().addAll(undoItem, redoItem, separator1, 
                                  cutItem, copyItem, pasteItem, deleteItem, 
                                  separator2, selectAllItem);
        
        this.getMenus().add(editMenu);
    }
    
    private void createViewMenu() {
        Menu viewMenu = new Menu("View");
        
        CheckMenuItem wordWrapItem = new CheckMenuItem("Word Wrap");
        CheckMenuItem statusBarItem = new CheckMenuItem("Status Bar");
        statusBarItem.setSelected(true);
        
        viewMenu.getItems().addAll(wordWrapItem, statusBarItem);
        
        this.getMenus().add(viewMenu);
    }
    
    private void createHelpMenu() {
        Menu helpMenu = new Menu("Help");
        
        MenuItem aboutItem = new MenuItem("About");
        
        helpMenu.getItems().addAll(aboutItem);
        
        this.getMenus().add(helpMenu);
    }
}
```

### Step 2: Add Keyboard Shortcuts

Let's enhance our menu by adding keyboard shortcuts:

```java
// In createFileMenu()
newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

// In createEditMenu()
undoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
redoItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
selectAllItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
```

Don't forget to add the necessary imports:

```java
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
```

### Step 3: Update the Main Window to Include the Menu Bar

Now, let's update the `MainWindow` class to include our menu bar:

```java
// In the initializeUI() method of MainWindow.java
EditorMenuBar menuBar = new EditorMenuBar();
rootLayout.setTop(menuBar);
```

## Adding a Text Area

The text area is where the user will edit text. We'll use JavaFX's `TextArea` control for this.

### Step 1: Create the Editor Text Area Class

Create a new file `EditorTextArea.java` in the `src/main/java/com/texteditor/view` directory:

```java
package com.texteditor.view;

import javafx.scene.control.TextArea;

public class EditorTextArea extends TextArea {
    
    public EditorTextArea() {
        // Configure the text area
        this.setWrapText(false); // No word wrap by default
        this.setPrefRowCount(20); // Preferred number of visible rows
        this.setPrefColumnCount(80); // Preferred number of visible columns
        
        // Set a monospaced font for better text editing
        this.setStyle("-fx-font-family: 'monospace';");
    }
    
    public void setWordWrap(boolean wrap) {
        this.setWrapText(wrap);
    }
}
```

### Step 2: Update the Main Window to Include the Text Area

Now, let's update the `MainWindow` class to include our text area:

```java
// Add this as a field in MainWindow.java
private EditorTextArea textArea;

// In the initializeUI() method of MainWindow.java
textArea = new EditorTextArea();
rootLayout.setCenter(textArea);
```

## Creating a Status Bar

The status bar displays information about the document and editor state, such as cursor position, line count, etc.

### Step 1: Create the Status Bar Class

Create a new file `StatusBar.java` in the `src/main/java/com/texteditor/view` directory:

```java
package com.texteditor.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class StatusBar extends HBox {
    
    private Label positionLabel;
    private Label fileInfoLabel;
    
    public StatusBar() {
        // Configure the HBox
        this.setPadding(new Insets(2, 5, 2, 5));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        
        // Create labels for status information
        positionLabel = new Label("Ln 1, Col 1");
        fileInfoLabel = new Label("UTF-8");
        
        // Create a spacer to push the fileInfoLabel to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Add components to the status bar
        this.getChildren().addAll(positionLabel, spacer, fileInfoLabel);
    }
    
    public void updatePosition(int line, int column) {
        positionLabel.setText(String.format("Ln %d, Col %d", line, column));
    }
    
    public void updateFileInfo(String encoding) {
        fileInfoLabel.setText(encoding);
    }
}
```

### Step 2: Update the Main Window to Include the Status Bar

Now, let's update the `MainWindow` class to include our status bar:

```java
// Add this as a field in MainWindow.java
private StatusBar statusBar;

// In the initializeUI() method of MainWindow.java
statusBar = new StatusBar();
rootLayout.setBottom(statusBar);
```

## Designing Dialog Boxes

Dialog boxes are used for various operations like opening files, saving files, finding text, etc. Let's create a few essential dialogs.

### Step 1: Create a Base Dialog Class

Create a new file `BaseDialog.java` in the `src/main/java/com/texteditor/view/dialogs` directory:

```java
package com.texteditor.view.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class BaseDialog {
    
    protected Stage owner;
    
    public BaseDialog(Stage owner) {
        this.owner = owner;
    }
    
    protected Alert createAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(owner);
        return alert;
    }
    
    protected boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, header, content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    protected void showInformationDialog(String title, String header, String content) {
        Alert alert = createAlert(Alert.AlertType.INFORMATION, title, header, content);
        alert.showAndWait();
    }
    
    protected void showErrorDialog(String title, String header, String content) {
        Alert alert = createAlert(Alert.AlertType.ERROR, title, header, content);
        alert.showAndWait();
    }
}
```

### Step 2: Create an About Dialog

Create a new file `AboutDialog.java` in the `src/main/java/com/texteditor/view/dialogs` directory:

```java
package com.texteditor.view.dialogs;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AboutDialog extends BaseDialog {
    
    public AboutDialog(Stage owner) {
        super(owner);
    }
    
    public void show() {
        Alert alert = createAlert(
            Alert.AlertType.INFORMATION,
            "About Text Editor",
            "Text Editor",
            "A simple text editor created as a learning project.\nVersion 1.0"
        );
        alert.showAndWait();
    }
}
```

### Step 3: Create a Find Dialog

Create a new file `FindDialog.java` in the `src/main/java/com/texteditor/view/dialogs` directory:

```java
package com.texteditor.view.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;

public class FindDialog extends BaseDialog {
    
    private TextField findTextField;
    private CheckBox matchCaseCheckBox;
    
    public FindDialog(Stage owner) {
        super(owner);
    }
    
    public Optional<FindResult> show() {
        // Create the custom dialog
        Dialog<FindResult> dialog = new Dialog<>();
        dialog.setTitle("Find");
        dialog.setHeaderText("Find what:");
        dialog.initOwner(owner);
        
        // Set the button types
        ButtonType findButtonType = new ButtonType("Find", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(findButtonType, ButtonType.CANCEL);
        
        // Create the content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        findTextField = new TextField();
        findTextField.setPromptText("Text to find");
        matchCaseCheckBox = new CheckBox("Match case");
        
        grid.add(new Label("Find what:"), 0, 0);
        grid.add(findTextField, 1, 0);
        grid.add(matchCaseCheckBox, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the find field by default
        findTextField.requestFocus();
        
        // Convert the result to a FindResult when the find button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == findButtonType) {
                return new FindResult(findTextField.getText(), matchCaseCheckBox.isSelected());
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
    
    public static class FindResult {
        private final String searchText;
        private final boolean matchCase;
        
        public FindResult(String searchText, boolean matchCase) {
            this.searchText = searchText;
            this.matchCase = matchCase;
        }
        
        public String getSearchText() {
            return searchText;
        }
        
        public boolean isMatchCase() {
            return matchCase;
        }
    }
}
```

## Putting It All Together

Now let's update our `MainWindow` class to connect all the UI components and add event handlers.

### Step 1: Add Fields for Dialogs

```java
// Add these fields to MainWindow.java
private AboutDialog aboutDialog;
private FindDialog findDialog;
```

### Step 2: Initialize Dialogs

```java
// In the initializeUI() method of MainWindow.java
aboutDialog = new AboutDialog(stage);
findDialog = new FindDialog(stage);
```

### Step 3: Connect Menu Items to Actions

Let's update the `EditorMenuBar` class to accept a `MainWindow` reference and connect menu items to actions:

```java
// Add this field to EditorMenuBar.java
private MainWindow mainWindow;

// Update the constructor
public EditorMenuBar(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
    createFileMenu();
    createEditMenu();
    createViewMenu();
    createHelpMenu();
}

// In createHelpMenu()
aboutItem.setOnAction(event -> mainWindow.showAboutDialog());

// In createEditMenu()
MenuItem findItem = new MenuItem("Find...");
findItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
findItem.setOnAction(event -> mainWindow.showFindDialog());

// Add findItem to the editMenu
editMenu.getItems().add(findItem);
```

### Step 4: Add Methods to MainWindow

```java
// Add these methods to MainWindow.java
public void showAboutDialog() {
    aboutDialog.show();
}

public void showFindDialog() {
    findDialog.show().ifPresent(result -> {
        // Implement find functionality here
        System.out.println("Searching for: " + result.getSearchText() + 
                          " (Match case: " + result.isMatchCase() + ")");
    });
}
```

### Step 5: Update the MainWindow Constructor

```java
// Update the initializeUI() method in MainWindow.java
private void initializeUI() {
    // Create the root layout
    rootLayout = new BorderPane();
    
    // Create and add the menu bar
    EditorMenuBar menuBar = new EditorMenuBar(this);
    rootLayout.setTop(menuBar);
    
    // Create and add the text area
    textArea = new EditorTextArea();
    rootLayout.setCenter(textArea);
    
    // Create and add the status bar
    statusBar = new StatusBar();
    rootLayout.setBottom(statusBar);
    
    // Create a scene with the root layout
    Scene scene = new Scene(rootLayout, 800, 600);
    
    // Configure the stage
    stage.setTitle("Text Editor");
    stage.setScene(scene);
    
    // Set minimum size constraints
    stage.setMinWidth(400);
    stage.setMinHeight(300);
    
    // Initialize dialogs
    aboutDialog = new AboutDialog(stage);
    findDialog = new FindDialog(stage);
}
```

## Testing the UI

Now that we have implemented the basic UI components, let's test our application to make sure everything works as expected.

### Step 1: Run the Application

Run the `TextEditorApp` class to start the application. You should see a window with a menu bar, text area, and status bar.

### Step 2: Test Menu Items

Click on the "Help" menu and select "About" to test the About dialog. Click on the "Edit" menu and select "Find..." to test the Find dialog.

### Step 3: Test Text Editing

Type some text in the text area to make sure it works correctly.

## Common Issues and Troubleshooting

### UI Components Not Appearing

If some UI components are not appearing:

1. Check that you've added them to the correct container
2. Make sure the container is visible and has enough space
3. Check for any layout constraints that might be affecting visibility

### Menu Items Not Working

If menu items are not triggering actions:

1. Verify that you've set the `onAction` event handler
2. Check that the method being called exists and is accessible
3. Look for any exceptions in the console

### Dialog Issues

If dialogs are not working correctly:

1. Make sure you're passing the correct owner stage
2. Check that the dialog is being created properly
3. Verify that the result handling code is correct

## Next Steps

Now that you have implemented the basic user interface for your text editor, you're ready to move on to implementing the document management system. Continue to the next guide: [Document Management](03-document-management.md).

## Resources for Further Learning

- [JavaFX UI Controls](https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/ui_controls.htm)
- [JavaFX Layout Panes](https://docs.oracle.com/javase/8/javafx/layout-tutorial/index.html)
- [JavaFX CSS Reference](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html)
- [JavaFX Scene Builder](https://gluonhq.com/products/scene-builder/)
