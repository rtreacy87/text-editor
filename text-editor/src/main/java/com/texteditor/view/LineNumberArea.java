package com.texteditor.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * LineNumberArea displays line numbers next to the text area.
 */
public class LineNumberArea extends VBox {
    
    private EditorTextArea textArea;
    
    /**
     * Creates a new line number area.
     *
     * @param textArea the text area to display line numbers for
     */
    public LineNumberArea(EditorTextArea textArea) {
        if (textArea == null) {
            throw new NullPointerException("TextArea cannot be null");
        }
        
        this.textArea = textArea;
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");
        
        // Set the same font as the text area for alignment
        this.setStyle(this.getStyle() + " -fx-font-family: 'monospace';");
        
        updateLineNumbers();
        
        // Listen for text changes in the text area
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers();
        });
    }
    
    /**
     * Updates the line numbers based on the current text content.
     */
    public void updateLineNumbers() {
        this.getChildren().clear();
        
        int lineCount = textArea.getLineCount();
        
        for (int i = 1; i <= lineCount; i++) {
            Label lineLabel = new Label(String.valueOf(i));
            lineLabel.setStyle("-fx-text-fill: #808080;");
            this.getChildren().add(lineLabel);
        }
    }
}
