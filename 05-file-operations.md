# File Operations

This guide will walk you through implementing file operations for your text editor. File operations allow users to create, open, save, and manage files.

## Table of Contents
- [Overview of File Operations](#overview-of-file-operations)
- [Creating a File Controller](#creating-a-file-controller)
- [Implementing New File Functionality](#implementing-new-file-functionality)
- [Adding Open File Functionality](#adding-open-file-functionality)
- [Implementing Save and Save As](#implementing-save-and-save-as)
- [Managing Recent Files](#managing-recent-files)
- [Handling Unsaved Changes](#handling-unsaved-changes)
- [Testing File Operations](#testing-file-operations)

## Overview of File Operations

File operations are essential for any text editor. They allow users to work with files on disk. The core file operations include:

1. **New File**: Creating a new, empty document
2. **Open File**: Opening an existing file from disk
3. **Save File**: Saving the current document to disk
4. **Save As**: Saving the current document with a new name or location
5. **Recent Files**: Keeping track of recently opened files

Let's implement each of these operations.

## Creating a File Controller

The file controller will handle all file operations. It will interact with the document model and the file system.

### Step 1: Create the File Controller

Create a new file `FileController.java` in the `src/main/java/com/texteditor/controller` directory:

```java
package com.texteditor.controller;

import com.texteditor.model.Document;
import com.texteditor.view.dialogs.BaseDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

public class FileController {
    
    private Document document;
    private DocumentController documentController;
    private Stage stage;
    private List<String> recentFiles;
    private static final int MAX_RECENT_FILES = 5;
    private static final String RECENT_FILES_KEY = "recentFiles";
    
    public FileController(DocumentController documentController, Stage stage) {
        this.documentController = documentController;
        this.document = documentController.getDocument();
        this.stage = stage;
        this.recentFiles = loadRecentFiles();
    }
    
    public void newFile() {
        // Check for unsaved changes
        if (checkUnsavedChanges()) {
            documentController.newDocument();
        }
    }
    
    public void openFile() {
        // Check for unsaved changes
        if (!checkUnsavedChanges()) {
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        configureFileChooser(fileChooser);
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }
    
    public void openFile(File file) {
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            document.setText(content);
            document.setFile(file);
            document.resetModified();
            addRecentFile(file.getAbsolutePath());
        } catch (IOException e) {
            showErrorDialog("Error Opening File", "Could not open file: " + file.getName(), e.getMessage());
        }
    }
    
    public void saveFile() {
        if (document.getFile() == null) {
            saveFileAs();
        } else {
            saveFile(document.getFile());
        }
    }
    
    public void saveFileAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File As");
        configureFileChooser(fileChooser);
        
        if (document.getFile() != null) {
            fileChooser.setInitialDirectory(document.getFile().getParentFile());
            fileChooser.setInitialFileName(document.getFile().getName());
        }
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveFile(file);
        }
    }
    
    private void saveFile(File file) {
        try {
            Files.writeString(file.toPath(), document.getText(), StandardCharsets.UTF_8);
            document.setFile(file);
            document.resetModified();
            addRecentFile(file.getAbsolutePath());
        } catch (IOException e) {
            showErrorDialog("Error Saving File", "Could not save file: " + file.getName(), e.getMessage());
        }
    }
    
    public List<String> getRecentFiles() {
        return new ArrayList<>(recentFiles);
    }
    
    public void openRecentFile(String filePath) {
        // Check for unsaved changes
        if (!checkUnsavedChanges()) {
            return;
        }
        
        File file = new File(filePath);
        if (file.exists()) {
            openFile(file);
        } else {
            showErrorDialog("Error Opening Recent File", "File not found: " + filePath, "The file may have been moved or deleted.");
            recentFiles.remove(filePath);
            saveRecentFiles();
        }
    }
    
    public void clearRecentFiles() {
        recentFiles.clear();
        saveRecentFiles();
    }
    
    private boolean checkUnsavedChanges() {
        if (document.isModified()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("Save changes to " + document.getFileName() + "?");
            alert.setContentText("Your changes will be lost if you don't save them.");
            alert.initOwner(stage);
            
            ButtonType saveButton = new ButtonType("Save");
            ButtonType dontSaveButton = new ButtonType("Don't Save");
            ButtonType cancelButton = ButtonType.CANCEL;
            
            alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    saveFile();
                    return !document.isModified(); // Only continue if save was successful
                } else if (result.get() == dontSaveButton) {
                    return true; // Continue without saving
                } else {
                    return false; // Cancel the operation
                }
            }
            return false;
        }
        return true; // No unsaved changes, continue
    }
    
    private void configureFileChooser(FileChooser fileChooser) {
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // Set initial directory to the last used directory or user home
        if (document.getFile() != null) {
            fileChooser.setInitialDirectory(document.getFile().getParentFile());
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
    }
    
    private void addRecentFile(String filePath) {
        // Remove if already exists (to move it to the top)
        recentFiles.remove(filePath);
        
        // Add to the beginning of the list
        recentFiles.add(0, filePath);
        
        // Trim the list if it exceeds the maximum size
        while (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.remove(recentFiles.size() - 1);
        }
        
        saveRecentFiles();
    }
    
    private List<String> loadRecentFiles() {
        List<String> files = new ArrayList<>();
        Preferences prefs = Preferences.userNodeForPackage(FileController.class);
        String recentFilesStr = prefs.get(RECENT_FILES_KEY, "");
        
        if (!recentFilesStr.isEmpty()) {
            String[] paths = recentFilesStr.split("\\|");
            for (String path : paths) {
                if (!path.isEmpty()) {
                    files.add(path);
                }
            }
        }
        
        return files;
    }
    
    private void saveRecentFiles() {
        Preferences prefs = Preferences.userNodeForPackage(FileController.class);
        StringBuilder sb = new StringBuilder();
        
        for (String file : recentFiles) {
            sb.append(file).append("|");
        }
        
        prefs.put(RECENT_FILES_KEY, sb.toString());
    }
    
    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
```

### Step 2: Update the Main Window to Use the File Controller

Now, let's update the `MainWindow` class to use our file controller:

```java
// Add this field to MainWindow.java
private FileController fileController;

// In the initializeUI() method of MainWindow.java, after creating the document controller
fileController = new FileController(documentController, stage);
```

### Step 3: Add a Getter for the File Controller

Add this method to the `MainWindow` class:

```java
public FileController getFileController() {
    return fileController;
}
```

## Implementing New File Functionality

The new file functionality allows users to create a new, empty document.

### Step 1: Connect Menu Items to New File Method

Let's update the `EditorMenuBar` class to connect the "New" menu item to the file controller:

```java
// Update the constructor to accept a FileController
public EditorMenuBar(MainWindow mainWindow, EditController editController, FindController findController, FileController fileController) {
    this.mainWindow = mainWindow;
    this.editController = editController;
    this.findController = findController;
    this.fileController = fileController;
    createFileMenu();
    createEditMenu();
    createViewMenu();
    createHelpMenu();
}

// In createFileMenu()
newItem.setOnAction(event -> fileController.newFile());
```

### Step 2: Update the Main Window Constructor

```java
// Update the initializeUI() method in MainWindow.java
private void initializeUI() {
    // ... existing code ...
    
    // Create controllers
    document = new Document();
    documentController = new DocumentController(document, textArea, statusBar);
    editController = new EditController(documentController, textArea);
    findController = new FindController(document, textArea, stage);
    fileController = new FileController(documentController, stage);
    
    // Create and add the menu bar
    EditorMenuBar menuBar = new EditorMenuBar(this, editController, findController, fileController);
    rootLayout.setTop(menuBar);
    
    // ... existing code ...
}
```

## Adding Open File Functionality

The open file functionality allows users to open an existing file from disk.

### Step 1: Connect Menu Items to Open File Method

Let's update the `EditorMenuBar` class to connect the "Open" menu item to the file controller:

```java
// In createFileMenu()
openItem.setOnAction(event -> fileController.openFile());
```

### Step 2: Add Drag and Drop Support

Let's add drag and drop support to the text area to allow users to open files by dragging them onto the editor:

```java
// Add this method to MainWindow.java
private void setupDragAndDrop() {
    textArea.setOnDragOver(event -> {
        if (event.getGestureSource() != textArea && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    });
    
    textArea.setOnDragDropped(event -> {
        boolean success = false;
        if (event.getDragboard().hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            if (!files.isEmpty()) {
                fileController.openFile(files.get(0));
                success = true;
            }
        }
        event.setDropCompleted(success);
        event.consume();
    });
}

// Call this method in the initializeUI() method of MainWindow.java, after creating the text area
setupDragAndDrop();
```

## Implementing Save and Save As

The save functionality allows users to save the current document to disk, while save as allows them to save it with a new name or location.

### Step 1: Connect Menu Items to Save Methods

Let's update the `EditorMenuBar` class to connect the "Save" and "Save As" menu items to the file controller:

```java
// In createFileMenu()
saveItem.setOnAction(event -> fileController.saveFile());
saveAsItem.setOnAction(event -> fileController.saveFileAs());
```

## Managing Recent Files

The recent files functionality keeps track of recently opened files and allows users to quickly open them again.

### Step 1: Add Recent Files Menu

Let's update the `EditorMenuBar` class to add a recent files menu:

```java
// Add this field to EditorMenuBar.java
private Menu recentFilesMenu;

// In createFileMenu(), after adding the saveAsItem
recentFilesMenu = new Menu("Recent Files");
updateRecentFilesMenu();
SeparatorMenuItem separator1 = new SeparatorMenuItem();
fileMenu.getItems().addAll(separator1, recentFilesMenu, new SeparatorMenuItem());

// Add this method to EditorMenuBar.java
public void updateRecentFilesMenu() {
    recentFilesMenu.getItems().clear();
    
    List<String> recentFiles = fileController.getRecentFiles();
    if (recentFiles.isEmpty()) {
        MenuItem noRecentFilesItem = new MenuItem("No Recent Files");
        noRecentFilesItem.setDisable(true);
        recentFilesMenu.getItems().add(noRecentFilesItem);
    } else {
        for (String filePath : recentFiles) {
            File file = new File(filePath);
            MenuItem item = new MenuItem(file.getName());
            item.setOnAction(event -> fileController.openRecentFile(filePath));
            recentFilesMenu.getItems().add(item);
        }
        
        recentFilesMenu.getItems().add(new SeparatorMenuItem());
        MenuItem clearRecentFilesItem = new MenuItem("Clear Recent Files");
        clearRecentFilesItem.setOnAction(event -> {
            fileController.clearRecentFiles();
            updateRecentFilesMenu();
        });
        recentFilesMenu.getItems().add(clearRecentFilesItem);
    }
}
```

### Step 2: Update Recent Files Menu When Files Change

Let's update the `FileController` class to notify the menu bar when recent files change:

```java
// Add this field to FileController.java
private List<RecentFilesListener> recentFilesListeners = new ArrayList<>();

// Add these methods to FileController.java
public void addRecentFilesListener(RecentFilesListener listener) {
    recentFilesListeners.add(listener);
}

public void removeRecentFilesListener(RecentFilesListener listener) {
    recentFilesListeners.remove(listener);
}

private void notifyRecentFilesChanged() {
    for (RecentFilesListener listener : recentFilesListeners) {
        listener.onRecentFilesChanged();
    }
}

// Update the addRecentFile, clearRecentFiles, and loadRecentFiles methods to call notifyRecentFilesChanged()
private void addRecentFile(String filePath) {
    // ... existing code ...
    
    saveRecentFiles();
    notifyRecentFilesChanged();
}

public void clearRecentFiles() {
    recentFiles.clear();
    saveRecentFiles();
    notifyRecentFilesChanged();
}

// Add this interface to FileController.java
public interface RecentFilesListener {
    void onRecentFilesChanged();
}
```

### Step 3: Register the Menu Bar as a Recent Files Listener

Let's update the `EditorMenuBar` class to implement the `RecentFilesListener` interface:

```java
// Update the class declaration
public class EditorMenuBar extends MenuBar implements FileController.RecentFilesListener {
    
    // ... existing code ...
    
    // In the constructor, after initializing fields
    fileController.addRecentFilesListener(this);
    
    // Implement the onRecentFilesChanged method
    @Override
    public void onRecentFilesChanged() {
        updateRecentFilesMenu();
    }
}
```

## Handling Unsaved Changes

When the user tries to close the application or open a new file while there are unsaved changes, we should prompt them to save their work.

### Step 1: Add Window Close Handler

Let's update the `MainWindow` class to add a window close handler:

```java
// In the initializeUI() method of MainWindow.java, after configuring the stage
stage.setOnCloseRequest(event -> {
    if (!fileController.checkUnsavedChanges()) {
        event.consume();
    }
});
```

### Step 2: Make the checkUnsavedChanges Method Public

Update the `FileController` class to make the `checkUnsavedChanges` method public:

```java
public boolean checkUnsavedChanges() {
    // ... existing code ...
}
```

### Step 3: Add Exit Menu Item

Let's update the `EditorMenuBar` class to add an exit menu item:

```java
// In createFileMenu(), after adding the recentFilesMenu
MenuItem exitItem = new MenuItem("Exit");
exitItem.setOnAction(event -> {
    if (fileController.checkUnsavedChanges()) {
        Platform.exit();
    }
});
fileMenu.getItems().add(exitItem);
```

## Testing File Operations

Now that we have implemented the file operations, let's test them to make sure everything works as expected.

### Step 1: Test New File

1. Run the application
2. Type some text
3. Click on "File > New" to create a new file
4. Verify that you are prompted to save changes if you've modified the document
5. Verify that a new, empty document is created

### Step 2: Test Open File

1. Create a text file on your computer
2. Click on "File > Open" to open the file
3. Verify that the file content is loaded correctly
4. Try dragging and dropping a file onto the editor
5. Verify that the file is opened correctly

### Step 3: Test Save and Save As

1. Make some changes to the document
2. Click on "File > Save" to save the file
3. Verify that the file is saved correctly
4. Click on "File > Save As" to save the file with a new name
5. Verify that the file is saved with the new name

### Step 4: Test Recent Files

1. Open several files
2. Click on "File > Recent Files" to see the list of recently opened files
3. Click on a recent file to open it
4. Click on "Clear Recent Files" to clear the list
5. Verify that the recent files list is cleared

### Step 5: Test Unsaved Changes

1. Make some changes to the document
2. Try to close the application
3. Verify that you are prompted to save changes
4. Try to create a new file or open an existing file
5. Verify that you are prompted to save changes

## Common Issues and Troubleshooting

### File Not Opening

If files are not opening correctly:

1. Check that you're using the correct character encoding
2. Make sure the file exists and is readable
3. Verify that the file content is being loaded correctly

### File Not Saving

If files are not saving correctly:

1. Check that you have write permissions for the file location
2. Make sure the file path is valid
3. Verify that the file content is being written correctly

### Recent Files Not Working

If recent files are not working correctly:

1. Check that you're saving and loading the recent files list correctly
2. Make sure the file paths are valid
3. Verify that the recent files menu is being updated

## Next Steps

Now that you have implemented the file operations for your text editor, you're ready to move on to testing and debugging. Continue to the next guide: [Testing and Debugging](06-testing-and-debugging.md).

## Resources for Further Learning

- [Java NIO File I/O](https://docs.oracle.com/javase/tutorial/essential/io/fileio.html)
- [JavaFX FileChooser](https://docs.oracle.com/javase/8/javafx/api/javafx/stage/FileChooser.html)
- [Java Preferences API](https://docs.oracle.com/javase/8/docs/api/java/util/prefs/Preferences.html)
- [JavaFX Drag and Drop](https://docs.oracle.com/javase/8/javafx/events-tutorial/drag-drop.htm)
