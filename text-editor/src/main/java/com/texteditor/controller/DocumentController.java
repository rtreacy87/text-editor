package com.texteditor.controller;

import com.texteditor.model.Cursor;
import com.texteditor.model.Document;
import com.texteditor.view.EditorTextArea;
import com.texteditor.view.StatusBar;
import javafx.application.Platform;
import javafx.scene.control.IndexRange;

/**
 * DocumentController manages the interaction between the document model and the UI.
 */
public class DocumentController implements Document.DocumentListener, Cursor.CursorListener {

    private Document document;
    private EditorTextArea textArea;
    private StatusBar statusBar;

    // Flags to prevent infinite loops when updating
    private boolean updatingTextArea = false;
    private boolean updatingCursor = false;

    /**
     * Creates a new document controller.
     *
     * @param document the document model
     * @param textArea the text area view
     * @param statusBar the status bar view
     */
    public DocumentController(Document document, EditorTextArea textArea, StatusBar statusBar) {
        if (document == null || textArea == null || statusBar == null) {
            throw new NullPointerException("Document, TextArea, and StatusBar cannot be null");
        }

        this.document = document;
        this.textArea = textArea;
        this.statusBar = statusBar;

        // Register listeners
        document.addListener(this);
        document.getCursor().addListener(this);

        // Set up text area listeners
        setupTextAreaListeners();
    }

    /**
     * Sets up listeners for the text area.
     */
    private void setupTextAreaListeners() {
        // Listen for text changes
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!updatingTextArea) {
                updatingTextArea = true;
                document.setText(newValue);
                updatingTextArea = false;
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

    /**
     * Updates the text area content from the document.
     */
    private void updateTextAreaContent() {
        if (!updatingTextArea) {
            updatingTextArea = true;
            Platform.runLater(() -> {
                textArea.setText(document.getText());
                updatingTextArea = false;
            });
        }
    }

    /**
     * Updates the cursor position in the text area.
     */
    private void updateCursorPosition() {
        if (!updatingCursor) {
            updatingCursor = true;
            Platform.runLater(() -> {
                textArea.positionCaret(document.getCursor().getPosition());
                updatingCursor = false;
            });
        }
    }

    /**
     * Updates the text selection in the text area.
     */
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

    /**
     * Updates the window title based on the document state.
     */
    private void updateWindowTitle() {
        Platform.runLater(() -> {
            String title = document.getFileName();
            if (document.isModified()) {
                title += " *";
            }
            title += " - Text Editor";
            if (textArea.getScene() != null && textArea.getScene().getWindow() instanceof javafx.stage.Stage) {
                ((javafx.stage.Stage) textArea.getScene().getWindow()).setTitle(title);
            }
        });
    }

    /**
     * Updates the status bar with current document information.
     */
    private void updateStatusBar() {
        Platform.runLater(() -> {
            // Calculate line and column
            int position = textArea.getCaretPosition();
            int line = textArea.getLineNumberAt(position);
            int column = textArea.getColumnNumberAt(position);

            statusBar.updatePosition(line, column);
            statusBar.updateFileInfo(document.getCharset().name());
        });
    }

    /**
     * Gets the document model.
     *
     * @return the document model
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Creates a new empty document.
     */
    public void newDocument() {
        document.setText("");
        document.setFile(null);
        document.resetModified();
        document.getCursor().setPosition(0);
    }
}
