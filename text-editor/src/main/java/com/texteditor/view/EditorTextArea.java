package com.texteditor.view;

import javafx.scene.control.TextArea;

/**
 * EditorTextArea is a specialized TextArea for the text editor.
 * It provides additional functionality specific to text editing.
 */
public class EditorTextArea extends TextArea {
    
    /**
     * Creates a new EditorTextArea.
     */
    public EditorTextArea() {
        // Configure the text area
        this.setWrapText(false); // No word wrap by default
        this.setPrefRowCount(20); // Preferred number of visible rows
        this.setPrefColumnCount(80); // Preferred number of visible columns
        
        // Set a monospaced font for better text editing
        this.setStyle("-fx-font-family: 'monospace';");
    }
    
    /**
     * Enables or disables word wrap.
     *
     * @param wrap true to enable word wrap, false to disable
     */
    public void setWordWrap(boolean wrap) {
        this.setWrapText(wrap);
    }
    
    /**
     * Gets the line number at the specified position.
     *
     * @param position the position in the text
     * @return the line number (1-based)
     */
    public int getLineNumberAt(int position) {
        if (position < 0 || position > getText().length()) {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }
        
        String text = getText();
        int line = 1;
        
        for (int i = 0; i < position; i++) {
            if (text.charAt(i) == '\n') {
                line++;
            }
        }
        
        return line;
    }
    
    /**
     * Gets the column number at the specified position.
     *
     * @param position the position in the text
     * @return the column number (1-based)
     */
    public int getColumnNumberAt(int position) {
        if (position < 0 || position > getText().length()) {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }
        
        String text = getText();
        int column = 1;
        
        // Find the start of the line
        int lineStart = position;
        while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
            lineStart--;
        }
        
        // Calculate the column
        column = position - lineStart + 1;
        
        return column;
    }
    
    /**
     * Gets the total number of lines in the text.
     *
     * @return the number of lines
     */
    public int getLineCount() {
        String text = getText();
        int lines = 1;
        
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lines++;
            }
        }
        
        return lines;
    }
}
