package com.texteditor.model;

/**
 * TextBuffer is responsible for storing and manipulating the text content.
 * It provides methods for inserting, deleting, and retrieving text.
 */
public class TextBuffer {
    
    private StringBuilder buffer;
    
    /**
     * Creates a new empty text buffer.
     */
    public TextBuffer() {
        this.buffer = new StringBuilder();
    }
    
    /**
     * Creates a new text buffer with the specified initial text.
     *
     * @param initialText the initial text content
     */
    public TextBuffer(String initialText) {
        this.buffer = new StringBuilder(initialText != null ? initialText : "");
    }
    
    /**
     * Gets the entire text content.
     *
     * @return the text content
     */
    public String getText() {
        return buffer.toString();
    }
    
    /**
     * Gets a portion of the text content.
     *
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     * @return the text content in the specified range
     * @throws IndexOutOfBoundsException if the indices are out of bounds
     */
    public String getText(int start, int end) {
        if (start < 0 || end > buffer.length() || start > end) {
            throw new IndexOutOfBoundsException("Invalid text range: " + start + " to " + end);
        }
        return buffer.substring(start, end);
    }
    
    /**
     * Sets the entire text content.
     *
     * @param text the new text content
     */
    public void setText(String text) {
        buffer = new StringBuilder(text != null ? text : "");
    }
    
    /**
     * Inserts text at the specified position.
     *
     * @param position the position to insert at
     * @param text the text to insert
     * @throws IndexOutOfBoundsException if the position is out of bounds
     */
    public void insert(int position, String text) {
        if (position < 0 || position > buffer.length()) {
            throw new IndexOutOfBoundsException("Invalid insert position: " + position);
        }
        if (text != null) {
            buffer.insert(position, text);
        }
    }
    
    /**
     * Deletes text in the specified range.
     *
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     * @throws IndexOutOfBoundsException if the indices are out of bounds
     */
    public void delete(int start, int end) {
        if (start < 0 || end > buffer.length() || start > end) {
            throw new IndexOutOfBoundsException("Invalid delete range: " + start + " to " + end);
        }
        buffer.delete(start, end);
    }
    
    /**
     * Replaces text in the specified range.
     *
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     * @param text the replacement text
     * @throws IndexOutOfBoundsException if the indices are out of bounds
     */
    public void replace(int start, int end, String text) {
        if (start < 0 || end > buffer.length() || start > end) {
            throw new IndexOutOfBoundsException("Invalid replace range: " + start + " to " + end);
        }
        buffer.replace(start, end, text != null ? text : "");
    }
    
    /**
     * Gets the length of the text content.
     *
     * @return the length of the text content
     */
    public int length() {
        return buffer.length();
    }
    
    /**
     * Gets the character at the specified index.
     *
     * @param index the index
     * @return the character at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public char charAt(int index) {
        if (index < 0 || index >= buffer.length()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        return buffer.charAt(index);
    }
    
    /**
     * Finds the index of the specified string, starting from the specified index.
     *
     * @param str the string to find
     * @param fromIndex the index to start searching from
     * @return the index of the first occurrence of the specified string,
     *         or -1 if the string is not found
     */
    public int indexOf(String str, int fromIndex) {
        if (str == null) {
            throw new NullPointerException("Search string cannot be null");
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        return buffer.indexOf(str, fromIndex);
    }
    
    /**
     * Finds the last index of the specified string, searching backward from the specified index.
     *
     * @param str the string to find
     * @param fromIndex the index to start searching backward from
     * @return the index of the last occurrence of the specified string,
     *         or -1 if the string is not found
     */
    public int lastIndexOf(String str, int fromIndex) {
        if (str == null) {
            throw new NullPointerException("Search string cannot be null");
        }
        if (fromIndex >= buffer.length()) {
            fromIndex = buffer.length() - 1;
        }
        return buffer.lastIndexOf(str, fromIndex);
    }
}
