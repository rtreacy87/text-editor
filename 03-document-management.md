# Document Management

This guide will walk you through implementing the document management system for your text editor. The document management system is responsible for handling the text content, tracking changes, and managing the document state.

## Table of Contents
- [Understanding Document Management](#understanding-document-management)
- [Creating the Document Model](#creating-the-document-model)
- [Implementing Text Buffer](#implementing-text-buffer)
- [Managing Cursor and Selection](#managing-cursor-and-selection)
- [Handling Document State](#handling-document-state)
- [Connecting to the UI](#connecting-to-the-ui)
- [Testing Document Management](#testing-document-management)

## Understanding Document Management

Document management is a critical part of any text editor. It involves:

1. **Text Storage**: How the text content is stored and accessed
2. **Text Manipulation**: How text is added, removed, and modified
3. **Cursor Management**: Tracking the cursor position and text selection
4. **State Tracking**: Monitoring whether the document has been modified
5. **File Association**: Linking the document to a file on disk

Let's implement each of these components.

## Creating the Document Model

The Document model is the core of our text editor. It represents the text content and provides methods to manipulate it.

### Step 1: Create the Document Class

Create a new file `Document.java` in the `src/main/java/com/texteditor/model` directory:

```java
package com.texteditor.model;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Document {
    
    private TextBuffer textBuffer;
    private File file;
    private boolean modified;
    private Charset charset;
    private List<DocumentListener> listeners;
    
    public Document() {
        this.textBuffer = new TextBuffer();
        this.modified = false;
        this.charset = StandardCharsets.UTF_8;
        this.listeners = new ArrayList<>();
    }
    
    public String getText() {
        return textBuffer.getText();
    }
    
    public void setText(String text) {
        textBuffer.setText(text);
        setModified(true);
        notifyContentChanged();
    }
    
    public void insertText(int position, String text) {
        textBuffer.insert(position, text);
        setModified(true);
        notifyContentChanged();
    }
    
    public void deleteText(int start, int end) {
        textBuffer.delete(start, end);
        setModified(true);
        notifyContentChanged();
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void setModified(boolean modified) {
        boolean oldValue = this.modified;
        this.modified = modified;
        if (oldValue != modified) {
            notifyModificationStateChanged();
        }
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
        notifyFileChanged();
    }
    
    public Charset getCharset() {
        return charset;
    }
    
    public void setCharset(Charset charset) {
        this.charset = charset;
        notifyFileChanged();
    }
    
    public void addListener(DocumentListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(DocumentListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyContentChanged() {
        for (DocumentListener listener : listeners) {
            listener.onContentChanged(this);
        }
    }
    
    private void notifyModificationStateChanged() {
        for (DocumentListener listener : listeners) {
            listener.onModificationStateChanged(this);
        }
    }
    
    private void notifyFileChanged() {
        for (DocumentListener listener : listeners) {
            listener.onFileChanged(this);
        }
    }
    
    public interface DocumentListener {
        void onContentChanged(Document document);
        void onModificationStateChanged(Document document);
        void onFileChanged(Document document);
    }
}
```

### Step 2: Create a Document Listener Interface

We've already defined the `DocumentListener` interface as an inner interface in the `Document` class. This interface allows other components to be notified when the document changes.

## Implementing Text Buffer

The text buffer is responsible for storing and manipulating the text content. For a simple text editor, we can use a `StringBuilder` to store the text.

### Step 1: Create the TextBuffer Class

Create a new file `TextBuffer.java` in the `src/main/java/com/texteditor/model` directory:

```java
package com.texteditor.model;

public class TextBuffer {
    
    private StringBuilder buffer;
    
    public TextBuffer() {
        this.buffer = new StringBuilder();
    }
    
    public TextBuffer(String initialText) {
        this.buffer = new StringBuilder(initialText);
    }
    
    public String getText() {
        return buffer.toString();
    }
    
    public String getText(int start, int end) {
        return buffer.substring(start, end);
    }
    
    public void setText(String text) {
        buffer = new StringBuilder(text);
    }
    
    public void insert(int position, String text) {
        buffer.insert(position, text);
    }
    
    public void delete(int start, int end) {
        buffer.delete(start, end);
    }
    
    public void replace(int start, int end, String text) {
        buffer.replace(start, end, text);
    }
    
    public int length() {
        return buffer.length();
    }
    
    public char charAt(int index) {
        return buffer.charAt(index);
    }
    
    public int indexOf(String str, int fromIndex) {
        return buffer.indexOf(str, fromIndex);
    }
    
    public int lastIndexOf(String str, int fromIndex) {
        return buffer.lastIndexOf(str, fromIndex);
    }
}
```

## Managing Cursor and Selection

Now, let's implement cursor and selection management. This will allow us to track the cursor position and text selection.

### Step 1: Create the Cursor Class

Create a new file `Cursor.java` in the `src/main/java/com/texteditor/model` directory:

```java
package com.texteditor.model;

import java.util.ArrayList;
import java.util.List;

public class Cursor {
    
    private int position;
    private int selectionStart;
    private int selectionEnd;
    private List<CursorListener> listeners;
    
    public Cursor() {
        this.position = 0;
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.listeners = new ArrayList<>();
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = Math.max(0, position);
        clearSelection();
        notifyPositionChanged();
    }
    
    public boolean hasSelection() {
        return selectionStart != -1 && selectionEnd != -1;
    }
    
    public int getSelectionStart() {
        return selectionStart;
    }
    
    public int getSelectionEnd() {
        return selectionEnd;
    }
    
    public void setSelection(int start, int end) {
        this.selectionStart = Math.min(start, end);
        this.selectionEnd = Math.max(start, end);
        this.position = end;
        notifySelectionChanged();
    }
    
    public void clearSelection() {
        if (hasSelection()) {
            this.selectionStart = -1;
            this.selectionEnd = -1;
            notifySelectionChanged();
        }
    }
    
    public String getSelectedText(Document document) {
        if (hasSelection()) {
            return document.getText().substring(selectionStart, selectionEnd);
        }
        return "";
    }
    
    public void addListener(CursorListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(CursorListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyPositionChanged() {
        for (CursorListener listener : listeners) {
            listener.onPositionChanged(this);
        }
    }
    
    private void notifySelectionChanged() {
        for (CursorListener listener : listeners) {
            listener.onSelectionChanged(this);
        }
    }
    
    public interface CursorListener {
        void onPositionChanged(Cursor cursor);
        void onSelectionChanged(Cursor cursor);
    }
}
```

### Step 2: Update the Document Class to Include a Cursor

Now, let's update the `Document` class to include a cursor:

```java
// Add this field to Document.java
private Cursor cursor;

// Update the constructor
public Document() {
    this.textBuffer = new TextBuffer();
    this.cursor = new Cursor();
    this.modified = false;
    this.charset = StandardCharsets.UTF_8;
    this.listeners = new ArrayList<>();
}

// Add getter for cursor
public Cursor getCursor() {
    return cursor;
}
```

## Handling Document State

The document state includes whether the document has been modified and its association with a file on disk. We've already implemented most of this in the `Document` class, but let's add a few more methods to handle document state.

### Step 1: Add Methods to Check Document State

Add these methods to the `Document` class:

```java
public boolean isNew() {
    return file == null;
}

public String getFileName() {
    return file != null ? file.getName() : "Untitled";
}

public String getFilePath() {
    return file != null ? file.getAbsolutePath() : "";
}

public void resetModified() {
    setModified(false);
}
```

## Connecting to the UI

Now, let's connect our document model to the UI. We'll create a document controller that will act as an intermediary between the document model and the UI.

### Step 1: Create the Document Controller

Create a new file `DocumentController.java` in the `src/main/java/com/texteditor/controller` directory:

```java
package com.texteditor.controller;

import com.texteditor.model.Cursor;
import com.texteditor.model.Document;
import com.texteditor.view.EditorTextArea;
import com.texteditor.view.StatusBar;
import javafx.application.Platform;
import javafx.scene.control.IndexRange;

public class DocumentController implements Document.DocumentListener, Cursor.CursorListener {
    
    private Document document;
    private EditorTextArea textArea;
    private StatusBar statusBar;
    
    public DocumentController(Document document, EditorTextArea textArea, StatusBar statusBar) {
        this.document = document;
        this.textArea = textArea;
        this.statusBar = statusBar;
        
        // Register listeners
        document.addListener(this);
        document.getCursor().addListener(this);
        
        // Set up text area listeners
        setupTextAreaListeners();
    }
    
    private void setupTextAreaListeners() {
        // Listen for text changes
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!updatingTextArea) {
                document.setText(newValue);
            }
        });
        
        // Listen for caret position changes
        textArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            if (!updatingCursor) {
                updatingCursor = true;
                document.getCursor().setPosition(newValue.intValue());
                updatingCursor = false;
            }
        });
        
        // Listen for selection changes
        textArea.selectionProperty().addListener((observable, oldValue, newValue) -> {
            if (!updatingCursor && newValue.getLength() > 0) {
                updatingCursor = true;
                document.getCursor().setSelection(newValue.getStart(), newValue.getEnd());
                updatingCursor = false;
            }
        });
    }
    
    // Flags to prevent infinite loops when updating
    private boolean updatingTextArea = false;
    private boolean updatingCursor = false;
    
    // Document.DocumentListener methods
    
    @Override
    public void onContentChanged(Document document) {
        updateTextAreaContent();
        updateStatusBar();
    }
    
    @Override
    public void onModificationStateChanged(Document document) {
        updateWindowTitle();
    }
    
    @Override
    public void onFileChanged(Document document) {
        updateWindowTitle();
        updateStatusBar();
    }
    
    // Cursor.CursorListener methods
    
    @Override
    public void onPositionChanged(Cursor cursor) {
        updateCursorPosition();
        updateStatusBar();
    }
    
    @Override
    public void onSelectionChanged(Cursor cursor) {
        updateTextSelection();
    }
    
    // Update methods
    
    private void updateTextAreaContent() {
        if (!updatingTextArea) {
            updatingTextArea = true;
            Platform.runLater(() -> {
                textArea.setText(document.getText());
                updatingTextArea = false;
            });
        }
    }
    
    private void updateCursorPosition() {
        if (!updatingCursor) {
            updatingCursor = true;
            Platform.runLater(() -> {
                textArea.positionCaret(document.getCursor().getPosition());
                updatingCursor = false;
            });
        }
    }
    
    private void updateTextSelection() {
        if (!updatingCursor && document.getCursor().hasSelection()) {
            updatingCursor = true;
            Platform.runLater(() -> {
                int start = document.getCursor().getSelectionStart();
                int end = document.getCursor().getSelectionEnd();
                textArea.selectRange(start, end);
                updatingCursor = false;
            });
        }
    }
    
    private void updateWindowTitle() {
        Platform.runLater(() -> {
            String title = document.getFileName();
            if (document.isModified()) {
                title += " *";
            }
            title += " - Text Editor";
            textArea.getScene().getWindow().setTitle(title);
        });
    }
    
    private void updateStatusBar() {
        Platform.runLater(() -> {
            // Calculate line and column
            String text = textArea.getText();
            int position = textArea.getCaretPosition();
            
            int line = 1;
            int column = 1;
            
            for (int i = 0; i < position; i++) {
                if (i < text.length() && text.charAt(i) == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
            }
            
            statusBar.updatePosition(line, column);
            statusBar.updateFileInfo(document.getCharset().name());
        });
    }
    
    // Public methods
    
    public Document getDocument() {
        return document;
    }
    
    public void newDocument() {
        document.setText("");
        document.setFile(null);
        document.resetModified();
        document.getCursor().setPosition(0);
    }
}
```

### Step 2: Update the Main Window to Use the Document Controller

Now, let's update the `MainWindow` class to use our document controller:

```java
// Add these fields to MainWindow.java
private Document document;
private DocumentController documentController;

// In the initializeUI() method of MainWindow.java, after creating the text area and status bar
document = new Document();
documentController = new DocumentController(document, textArea, statusBar);
```

## Testing Document Management

Now that we have implemented the document management system, let's test it to make sure everything works as expected.

### Step 1: Update the Main Application

Let's update the `TextEditorApp` class to create a new document when the application starts:

```java
@Override
public void start(Stage primaryStage) {
    // Create and show the main window
    MainWindow mainWindow = new MainWindow(primaryStage);
    
    // Create a new document
    mainWindow.getDocumentController().newDocument();
    
    // Show the window
    mainWindow.show();
}
```

### Step 2: Add a Getter for the Document Controller

Add this method to the `MainWindow` class:

```java
public DocumentController getDocumentController() {
    return documentController;
}
```

### Step 3: Run the Application

Run the `TextEditorApp` class to start the application. You should see a window with an empty text area. Try typing some text and observe how the status bar updates with the cursor position.

## Common Issues and Troubleshooting

### Infinite Update Loops

If you notice that the application is freezing or behaving erratically, it might be due to infinite update loops between the document model and the UI. Make sure you're using the `updatingTextArea` and `updatingCursor` flags correctly to prevent these loops.

### Text Not Updating

If the text in the text area is not updating when the document changes:

1. Check that you've registered the document listener correctly
2. Make sure the `onContentChanged` method is being called
3. Verify that the `updateTextAreaContent` method is working correctly

### Cursor Position Issues

If the cursor position is not updating correctly:

1. Check that you've registered the cursor listener correctly
2. Make sure the `onPositionChanged` method is being called
3. Verify that the `updateCursorPosition` method is working correctly

## Next Steps

Now that you have implemented the document management system for your text editor, you're ready to move on to implementing text editing features. Continue to the next guide: [Text Editing Features](04-text-editing-features.md).

## Resources for Further Learning

- [Java StringBuilder Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html)
- [JavaFX TextArea Documentation](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TextArea.html)
- [Observer Pattern](https://en.wikipedia.org/wiki/Observer_pattern)
- [Model-View-Controller Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)
