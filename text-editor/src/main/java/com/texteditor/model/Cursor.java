package com.texteditor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Cursor represents the cursor position and selection in a document.
 * It provides methods for managing cursor position and text selection.
 */
public class Cursor {
    
    private int position;
    private int selectionStart;
    private int selectionEnd;
    private List<CursorListener> listeners;
    
    /**
     * Creates a new cursor at position 0 with no selection.
     */
    public Cursor() {
        this.position = 0;
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Gets the current cursor position.
     *
     * @return the cursor position
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Sets the cursor position and clears any selection.
     *
     * @param position the new cursor position
     */
    public void setPosition(int position) {
        this.position = Math.max(0, position);
        clearSelection();
        notifyPositionChanged();
    }
    
    /**
     * Checks if there is a text selection.
     *
     * @return true if there is a selection, false otherwise
     */
    public boolean hasSelection() {
        return selectionStart != -1 && selectionEnd != -1;
    }
    
    /**
     * Gets the start position of the selection.
     *
     * @return the selection start position, or -1 if there is no selection
     */
    public int getSelectionStart() {
        return selectionStart;
    }
    
    /**
     * Gets the end position of the selection.
     *
     * @return the selection end position, or -1 if there is no selection
     */
    public int getSelectionEnd() {
        return selectionEnd;
    }
    
    /**
     * Sets the text selection range.
     * The cursor position will be set to the end of the selection.
     *
     * @param start the start position of the selection
     * @param end the end position of the selection
     */
    public void setSelection(int start, int end) {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Selection positions cannot be negative");
        }
        
        this.selectionStart = Math.min(start, end);
        this.selectionEnd = Math.max(start, end);
        this.position = end;
        notifySelectionChanged();
    }
    
    /**
     * Clears the current selection.
     */
    public void clearSelection() {
        if (hasSelection()) {
            this.selectionStart = -1;
            this.selectionEnd = -1;
            notifySelectionChanged();
        }
    }
    
    /**
     * Gets the selected text from the document.
     *
     * @param document the document to get the text from
     * @return the selected text, or an empty string if there is no selection
     */
    public String getSelectedText(Document document) {
        if (document == null) {
            throw new NullPointerException("Document cannot be null");
        }
        
        if (hasSelection()) {
            return document.getText().substring(selectionStart, selectionEnd);
        }
        return "";
    }
    
    /**
     * Adds a cursor listener.
     *
     * @param listener the listener to add
     */
    public void addListener(CursorListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a cursor listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(CursorListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies all listeners that the cursor position has changed.
     */
    private void notifyPositionChanged() {
        for (CursorListener listener : listeners) {
            listener.onPositionChanged(this);
        }
    }
    
    /**
     * Notifies all listeners that the selection has changed.
     */
    private void notifySelectionChanged() {
        for (CursorListener listener : listeners) {
            listener.onSelectionChanged(this);
        }
    }
    
    /**
     * Interface for listening to cursor changes.
     */
    public interface CursorListener {
        /**
         * Called when the cursor position changes.
         *
         * @param cursor the cursor that changed
         */
        void onPositionChanged(Cursor cursor);
        
        /**
         * Called when the selection changes.
         *
         * @param cursor the cursor that changed
         */
        void onSelectionChanged(Cursor cursor);
    }
}
