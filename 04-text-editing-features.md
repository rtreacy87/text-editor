# Text Editing Features

This guide will walk you through implementing essential text editing features for your text editor. These features will make your text editor more functional and user-friendly.

## Table of Contents
- [Overview of Text Editing Features](#overview-of-text-editing-features)
- [Implementing Basic Text Operations](#implementing-basic-text-operations)
- [Creating an Undo/Redo System](#creating-an-undoredo-system)
- [Adding Find and Replace Functionality](#adding-find-and-replace-functionality)
- [Implementing Line Numbering](#implementing-line-numbering)
- [Adding Word Wrap](#adding-word-wrap)
- [Testing Text Editing Features](#testing-text-editing-features)

## Overview of Text Editing Features

Text editing features are the core functionality of any text editor. They allow users to manipulate text efficiently. The essential text editing features include:

1. **Basic Text Operations**: Cut, copy, paste, delete, and select all
2. **Undo/Redo**: The ability to undo and redo changes
3. **Find and Replace**: Searching for text and optionally replacing it
4. **Line Numbering**: Displaying line numbers for easier navigation
5. **Word Wrap**: Wrapping text to fit within the window

Let's implement each of these features.

## Implementing Basic Text Operations

Basic text operations include cut, copy, paste, delete, and select all. JavaFX's `TextArea` already provides these operations, but we need to connect them to our menu items.

### Step 1: Create an Edit Controller

Create a new file `EditController.java` in the `src/main/java/com/texteditor/controller` directory:

```java
package com.texteditor.controller;

import com.texteditor.model.Document;
import com.texteditor.view.EditorTextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class EditController {
    
    private Document document;
    private EditorTextArea textArea;
    private DocumentController documentController;
    
    public EditController(DocumentController documentController, EditorTextArea textArea) {
        this.documentController = documentController;
        this.document = documentController.getDocument();
        this.textArea = textArea;
    }
    
    public void cut() {
        copy();
        deleteSelected();
    }
    
    public void copy() {
        String selectedText = textArea.getSelectedText();
        if (!selectedText.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(selectedText);
            clipboard.setContent(content);
        }
    }
    
    public void paste() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            String text = clipboard.getString();
            if (textArea.getSelectedText().isEmpty()) {
                int caretPosition = textArea.getCaretPosition();
                document.insertText(caretPosition, text);
            } else {
                replaceSelected(text);
            }
        }
    }
    
    public void deleteSelected() {
        if (!textArea.getSelectedText().isEmpty()) {
            int start = textArea.getSelection().getStart();
            int end = textArea.getSelection().getEnd();
            document.deleteText(start, end);
        }
    }
    
    public void replaceSelected(String text) {
        if (!textArea.getSelectedText().isEmpty()) {
            int start = textArea.getSelection().getStart();
            int end = textArea.getSelection().getEnd();
            document.deleteText(start, end);
            document.insertText(start, text);
        }
    }
    
    public void selectAll() {
        textArea.selectAll();
    }
}
```

### Step 2: Update the Main Window to Use the Edit Controller

Now, let's update the `MainWindow` class to use our edit controller:

```java
// Add this field to MainWindow.java
private EditController editController;

// In the initializeUI() method of MainWindow.java, after creating the document controller
editController = new EditController(documentController, textArea);
```

### Step 3: Connect Menu Items to Edit Controller Methods

Now, let's update the `EditorMenuBar` class to connect menu items to edit controller methods:

```java
// Update the constructor to accept an EditController
public EditorMenuBar(MainWindow mainWindow, EditController editController) {
    this.mainWindow = mainWindow;
    this.editController = editController;
    createFileMenu();
    createEditMenu();
    createViewMenu();
    createHelpMenu();
}

// In createEditMenu()
cutItem.setOnAction(event -> editController.cut());
copyItem.setOnAction(event -> editController.copy());
pasteItem.setOnAction(event -> editController.paste());
deleteItem.setOnAction(event -> editController.deleteSelected());
selectAllItem.setOnAction(event -> editController.selectAll());
```

### Step 4: Update the Main Window Constructor

```java
// Update the initializeUI() method in MainWindow.java
private void initializeUI() {
    // ... existing code ...
    
    // Create controllers
    document = new Document();
    documentController = new DocumentController(document, textArea, statusBar);
    editController = new EditController(documentController, textArea);
    
    // Create and add the menu bar
    EditorMenuBar menuBar = new EditorMenuBar(this, editController);
    rootLayout.setTop(menuBar);
    
    // ... existing code ...
}
```

## Creating an Undo/Redo System

The undo/redo system allows users to undo and redo changes to the document. We'll implement this using the Command pattern.

### Step 1: Create the Command Interface

Create a new file `Command.java` in the `src/main/java/com/texteditor/model/command` directory:

```java
package com.texteditor.model.command;

public interface Command {
    void execute();
    void undo();
    String getDescription();
}
```

### Step 2: Create Concrete Command Classes

Create a new file `InsertTextCommand.java` in the `src/main/java/com/texteditor/model/command` directory:

```java
package com.texteditor.model.command;

import com.texteditor.model.Document;

public class InsertTextCommand implements Command {
    
    private Document document;
    private int position;
    private String text;
    
    public InsertTextCommand(Document document, int position, String text) {
        this.document = document;
        this.position = position;
        this.text = text;
    }
    
    @Override
    public void execute() {
        document.insertText(position, text);
    }
    
    @Override
    public void undo() {
        document.deleteText(position, position + text.length());
    }
    
    @Override
    public String getDescription() {
        return "Insert text";
    }
}
```

Create a new file `DeleteTextCommand.java` in the `src/main/java/com/texteditor/model/command` directory:

```java
package com.texteditor.model.command;

import com.texteditor.model.Document;

public class DeleteTextCommand implements Command {
    
    private Document document;
    private int start;
    private int end;
    private String deletedText;
    
    public DeleteTextCommand(Document document, int start, int end) {
        this.document = document;
        this.start = start;
        this.end = end;
        this.deletedText = document.getText().substring(start, end);
    }
    
    @Override
    public void execute() {
        document.deleteText(start, end);
    }
    
    @Override
    public void undo() {
        document.insertText(start, deletedText);
    }
    
    @Override
    public String getDescription() {
        return "Delete text";
    }
}
```

### Step 3: Create the Command Manager

Create a new file `CommandManager.java` in the `src/main/java/com/texteditor/model/command` directory:

```java
package com.texteditor.model.command;

import java.util.Stack;

public class CommandManager {
    
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private int maxUndoLevels;
    
    public CommandManager(int maxUndoLevels) {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.maxUndoLevels = maxUndoLevels;
    }
    
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
        
        // Limit the undo stack size
        if (undoStack.size() > maxUndoLevels) {
            undoStack.remove(0);
        }
    }
    
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }
    
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    public String getUndoDescription() {
        return canUndo() ? undoStack.peek().getDescription() : "";
    }
    
    public String getRedoDescription() {
        return canRedo() ? redoStack.peek().getDescription() : "";
    }
    
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
```

### Step 4: Update the Document Class to Use the Command Manager

Now, let's update the `Document` class to use our command manager:

```java
// Add this field to Document.java
private CommandManager commandManager;

// Update the constructor
public Document() {
    this.textBuffer = new TextBuffer();
    this.cursor = new Cursor();
    this.modified = false;
    this.charset = StandardCharsets.UTF_8;
    this.listeners = new ArrayList<>();
    this.commandManager = new CommandManager(100); // 100 undo levels
}

// Add getter for command manager
public CommandManager getCommandManager() {
    return commandManager;
}

// Update the insertText method
public void insertText(int position, String text) {
    InsertTextCommand command = new InsertTextCommand(this, position, text);
    commandManager.executeCommand(command);
}

// Update the deleteText method
public void deleteText(int start, int end) {
    DeleteTextCommand command = new DeleteTextCommand(this, start, end);
    commandManager.executeCommand(command);
}

// Add methods for undo and redo
public void undo() {
    if (commandManager.canUndo()) {
        commandManager.undo();
    }
}

public void redo() {
    if (commandManager.canRedo()) {
        commandManager.redo();
    }
}

public boolean canUndo() {
    return commandManager.canUndo();
}

public boolean canRedo() {
    return commandManager.canRedo();
}
```

### Step 5: Update the Edit Controller to Include Undo and Redo

Now, let's update the `EditController` class to include undo and redo methods:

```java
// Add these methods to EditController.java
public void undo() {
    document.undo();
}

public void redo() {
    document.redo();
}

public boolean canUndo() {
    return document.canUndo();
}

public boolean canRedo() {
    return document.canRedo();
}
```

### Step 6: Connect Menu Items to Undo and Redo Methods

Now, let's update the `EditorMenuBar` class to connect menu items to undo and redo methods:

```java
// In createEditMenu()
undoItem.setOnAction(event -> editController.undo());
redoItem.setOnAction(event -> editController.redo());

// Add this method to EditorMenuBar.java
public void updateUndoRedoState() {
    undoItem.setDisable(!editController.canUndo());
    redoItem.setDisable(!editController.canRedo());
}
```

## Adding Find and Replace Functionality

Find and replace functionality allows users to search for text and optionally replace it.

### Step 1: Create a Find Controller

Create a new file `FindController.java` in the `src/main/java/com/texteditor/controller` directory:

```java
package com.texteditor.controller;

import com.texteditor.model.Document;
import com.texteditor.view.EditorTextArea;
import com.texteditor.view.dialogs.FindDialog;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindController {
    
    private Document document;
    private EditorTextArea textArea;
    private Stage owner;
    private FindDialog findDialog;
    private FindDialog.FindResult lastFindResult;
    private int lastFindPosition = -1;
    
    public FindController(Document document, EditorTextArea textArea, Stage owner) {
        this.document = document;
        this.textArea = textArea;
        this.owner = owner;
        this.findDialog = new FindDialog(owner);
    }
    
    public void showFindDialog() {
        Optional<FindDialog.FindResult> result = findDialog.show();
        result.ifPresent(findResult -> {
            lastFindResult = findResult;
            findNext();
        });
    }
    
    public void findNext() {
        if (lastFindResult == null) {
            showFindDialog();
            return;
        }
        
        String text = document.getText();
        String searchText = lastFindResult.getSearchText();
        boolean matchCase = lastFindResult.isMatchCase();
        
        if (!matchCase) {
            text = text.toLowerCase();
            searchText = searchText.toLowerCase();
        }
        
        int startPosition = lastFindPosition + 1;
        if (startPosition >= text.length() || startPosition < 0) {
            startPosition = 0;
        }
        
        int foundPosition = text.indexOf(searchText, startPosition);
        
        if (foundPosition == -1 && startPosition > 0) {
            // Wrap around to the beginning
            foundPosition = text.indexOf(searchText, 0);
        }
        
        if (foundPosition != -1) {
            textArea.selectRange(foundPosition, foundPosition + searchText.length());
            textArea.requestFocus();
            lastFindPosition = foundPosition;
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Find");
            alert.setHeaderText(null);
            alert.setContentText("Cannot find \"" + lastFindResult.getSearchText() + "\"");
            alert.initOwner(owner);
            alert.showAndWait();
            lastFindPosition = -1;
        }
    }
    
    public void showReplaceDialog() {
        // Implement replace dialog
    }
}
```

### Step 2: Update the Main Window to Use the Find Controller

Now, let's update the `MainWindow` class to use our find controller:

```java
// Add this field to MainWindow.java
private FindController findController;

// In the initializeUI() method of MainWindow.java, after creating the edit controller
findController = new FindController(document, textArea, stage);
```

### Step 3: Connect Menu Items to Find Controller Methods

Now, let's update the `EditorMenuBar` class to connect menu items to find controller methods:

```java
// Update the constructor to accept a FindController
public EditorMenuBar(MainWindow mainWindow, EditController editController, FindController findController) {
    this.mainWindow = mainWindow;
    this.editController = editController;
    this.findController = findController;
    createFileMenu();
    createEditMenu();
    createViewMenu();
    createHelpMenu();
}

// In createEditMenu()
findItem.setOnAction(event -> findController.showFindDialog());
```

### Step 4: Update the Main Window Constructor

```java
// Update the initializeUI() method in MainWindow.java
private void initializeUI() {
    // ... existing code ...
    
    // Create controllers
    document = new Document();
    documentController = new DocumentController(document, textArea, statusBar);
    editController = new EditController(documentController, textArea);
    findController = new FindController(document, textArea, stage);
    
    // Create and add the menu bar
    EditorMenuBar menuBar = new EditorMenuBar(this, editController, findController);
    rootLayout.setTop(menuBar);
    
    // ... existing code ...
}
```

## Implementing Line Numbering

Line numbering helps users navigate through the document by showing line numbers.

### Step 1: Create a Line Number Area

Create a new file `LineNumberArea.java` in the `src/main/java/com/texteditor/view` directory:

```java
package com.texteditor.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LineNumberArea extends VBox {
    
    private EditorTextArea textArea;
    
    public LineNumberArea(EditorTextArea textArea) {
        this.textArea = textArea;
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");
        
        // Set the same font as the text area for alignment
        this.setFont(Font.font("monospace"));
        
        updateLineNumbers();
        
        // Listen for text changes in the text area
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers();
        });
    }
    
    public void updateLineNumbers() {
        this.getChildren().clear();
        
        String text = textArea.getText();
        int lineCount = 1;
        
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lineCount++;
            }
        }
        
        for (int i = 1; i <= lineCount; i++) {
            Label lineLabel = new Label(String.valueOf(i));
            lineLabel.setStyle("-fx-text-fill: #808080;");
            this.getChildren().add(lineLabel);
        }
    }
}
```

### Step 2: Update the Main Window to Include Line Numbers

Now, let's update the `MainWindow` class to include line numbers:

```java
// Add this field to MainWindow.java
private LineNumberArea lineNumberArea;
private boolean lineNumbersVisible = true;

// In the initializeUI() method of MainWindow.java, after creating the text area
lineNumberArea = new LineNumberArea(textArea);
HBox textAreaContainer = new HBox();
textAreaContainer.getChildren().addAll(lineNumberArea, textArea);
HBox.setHgrow(textArea, Priority.ALWAYS);
rootLayout.setCenter(textAreaContainer);

// Add this method to MainWindow.java
public void toggleLineNumbers() {
    lineNumbersVisible = !lineNumbersVisible;
    lineNumberArea.setVisible(lineNumbersVisible);
    lineNumberArea.setManaged(lineNumbersVisible);
}
```

### Step 3: Connect Menu Items to Toggle Line Numbers

Now, let's update the `EditorMenuBar` class to connect menu items to toggle line numbers:

```java
// In createViewMenu()
lineNumbersItem.setSelected(true);
lineNumbersItem.setOnAction(event -> mainWindow.toggleLineNumbers());
```

## Adding Word Wrap

Word wrap allows text to wrap to the next line when it reaches the edge of the text area.

### Step 1: Update the Text Area to Support Word Wrap

The `EditorTextArea` class already has a method to set word wrap. Let's update the `MainWindow` class to toggle word wrap:

```java
// Add this field to MainWindow.java
private boolean wordWrapEnabled = false;

// Add this method to MainWindow.java
public void toggleWordWrap() {
    wordWrapEnabled = !wordWrapEnabled;
    textArea.setWordWrap(wordWrapEnabled);
}
```

### Step 2: Connect Menu Items to Toggle Word Wrap

Now, let's update the `EditorMenuBar` class to connect menu items to toggle word wrap:

```java
// In createViewMenu()
wordWrapItem.setSelected(false);
wordWrapItem.setOnAction(event -> mainWindow.toggleWordWrap());
```

## Testing Text Editing Features

Now that we have implemented the text editing features, let's test them to make sure everything works as expected.

### Step 1: Test Basic Text Operations

1. Run the application
2. Type some text
3. Select some text and use the Edit menu to cut, copy, paste, and delete
4. Use the keyboard shortcuts (Ctrl+X, Ctrl+C, Ctrl+V, Delete) to perform the same operations

### Step 2: Test Undo/Redo

1. Make some changes to the text
2. Use the Edit menu to undo and redo changes
3. Use the keyboard shortcuts (Ctrl+Z, Ctrl+Y) to perform the same operations

### Step 3: Test Find

1. Type some text with repeated words
2. Use the Edit menu to open the Find dialog
3. Enter a search term and click Find
4. Use the keyboard shortcut (Ctrl+F) to open the Find dialog

### Step 4: Test Line Numbering and Word Wrap

1. Type multiple lines of text
2. Use the View menu to toggle line numbers
3. Use the View menu to toggle word wrap
4. Observe how the text display changes

## Common Issues and Troubleshooting

### Undo/Redo Not Working

If undo/redo is not working correctly:

1. Check that you've implemented the `Command` interface correctly
2. Make sure the `CommandManager` is being used properly
3. Verify that the `undo` and `redo` methods are being called

### Find Not Working

If find is not working correctly:

1. Check that you're handling case sensitivity correctly
2. Make sure the search is wrapping around when it reaches the end of the document
3. Verify that the selection is being set correctly

### Line Numbers Not Updating

If line numbers are not updating correctly:

1. Check that you're listening for text changes in the text area
2. Make sure the `updateLineNumbers` method is being called
3. Verify that the line count calculation is correct

## Next Steps

Now that you have implemented the text editing features for your text editor, you're ready to move on to implementing file operations. Continue to the next guide: [File Operations](05-file-operations.md).

## Resources for Further Learning

- [Command Pattern](https://en.wikipedia.org/wiki/Command_pattern)
- [JavaFX TextArea Documentation](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TextArea.html)
- [Regular Expressions in Java](https://docs.oracle.com/javase/tutorial/essential/regex/)
- [JavaFX Layout Panes](https://docs.oracle.com/javase/8/javafx/layout-tutorial/index.html)
