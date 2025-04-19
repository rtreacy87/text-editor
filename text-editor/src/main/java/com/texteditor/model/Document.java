package com.texteditor.model;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Document represents a text document in the editor.
 * It manages the text content, cursor, file association, and modification state.
 */
public class Document {
    
    private TextBuffer textBuffer;
    private Cursor cursor;
    private File file;
    private boolean modified;
    private Charset charset;
    private List<DocumentListener> listeners;
    
    /**
     * Creates a new empty document.
     */
    public Document() {
        this.textBuffer = new TextBuffer();
        this.cursor = new Cursor();
        this.modified = false;
        this.charset = StandardCharsets.UTF_8;
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Gets the entire text content of the document.
     *
     * @return the text content
     */
    public String getText() {
        return textBuffer.getText();
    }
    
    /**
     * Sets the entire text content of the document.
     *
     * @param text the new text content
     */
    public void setText(String text) {
        textBuffer.setText(text);
        setModified(true);
        notifyContentChanged();
    }
    
    /**
     * Inserts text at the specified position.
     *
     * @param position the position to insert at
     * @param text the text to insert
     */
    public void insertText(int position, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        textBuffer.insert(position, text);
        setModified(true);
        notifyContentChanged();
    }
    
    /**
     * Deletes text in the specified range.
     *
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     */
    public void deleteText(int start, int end) {
        if (start >= end) {
            return;
        }
        
        textBuffer.delete(start, end);
        setModified(true);
        notifyContentChanged();
    }
    
    /**
     * Checks if the document has been modified since the last save.
     *
     * @return true if the document has been modified, false otherwise
     */
    public boolean isModified() {
        return modified;
    }
    
    /**
     * Sets the modification state of the document.
     *
     * @param modified the new modification state
     */
    public void setModified(boolean modified) {
        boolean oldValue = this.modified;
        this.modified = modified;
        if (oldValue != modified) {
            notifyModificationStateChanged();
        }
    }
    
    /**
     * Gets the file associated with this document.
     *
     * @return the associated file, or null if the document is not associated with a file
     */
    public File getFile() {
        return file;
    }
    
    /**
     * Sets the file associated with this document.
     *
     * @param file the file to associate with this document
     */
    public void setFile(File file) {
        this.file = file;
        notifyFileChanged();
    }
    
    /**
     * Gets the character encoding used for this document.
     *
     * @return the character encoding
     */
    public Charset getCharset() {
        return charset;
    }
    
    /**
     * Sets the character encoding used for this document.
     *
     * @param charset the character encoding
     */
    public void setCharset(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("Charset cannot be null");
        }
        this.charset = charset;
        notifyFileChanged();
    }
    
    /**
     * Gets the cursor for this document.
     *
     * @return the cursor
     */
    public Cursor getCursor() {
        return cursor;
    }
    
    /**
     * Checks if this document is new (not associated with a file).
     *
     * @return true if the document is new, false otherwise
     */
    public boolean isNew() {
        return file == null;
    }
    
    /**
     * Gets the filename of the associated file.
     *
     * @return the filename, or "Untitled" if the document is not associated with a file
     */
    public String getFileName() {
        return file != null ? file.getName() : "Untitled";
    }
    
    /**
     * Gets the file path of the associated file.
     *
     * @return the file path, or an empty string if the document is not associated with a file
     */
    public String getFilePath() {
        return file != null ? file.getAbsolutePath() : "";
    }
    
    /**
     * Resets the modification state to unmodified.
     */
    public void resetModified() {
        setModified(false);
    }
    
    /**
     * Adds a document listener.
     *
     * @param listener the listener to add
     */
    public void addListener(DocumentListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a document listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(DocumentListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies all listeners that the content has changed.
     */
    private void notifyContentChanged() {
        for (DocumentListener listener : listeners) {
            listener.onContentChanged(this);
        }
    }
    
    /**
     * Notifies all listeners that the modification state has changed.
     */
    private void notifyModificationStateChanged() {
        for (DocumentListener listener : listeners) {
            listener.onModificationStateChanged(this);
        }
    }
    
    /**
     * Notifies all listeners that the file association has changed.
     */
    private void notifyFileChanged() {
        for (DocumentListener listener : listeners) {
            listener.onFileChanged(this);
        }
    }
    
    /**
     * Interface for listening to document changes.
     */
    public interface DocumentListener {
        /**
         * Called when the document content changes.
         *
         * @param document the document that changed
         */
        void onContentChanged(Document document);
        
        /**
         * Called when the document modification state changes.
         *
         * @param document the document that changed
         */
        void onModificationStateChanged(Document document);
        
        /**
         * Called when the document file association changes.
         *
         * @param document the document that changed
         */
        void onFileChanged(Document document);
    }
}
