package com.texteditor.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * StatusBar displays information about the document and editor state.
 */
public class StatusBar extends HBox {
    
    private Label positionLabel;
    private Label fileInfoLabel;
    
    /**
     * Creates a new status bar.
     */
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
    
    /**
     * Updates the cursor position display.
     *
     * @param line the line number (1-based)
     * @param column the column number (1-based)
     */
    public void updatePosition(int line, int column) {
        if (line < 1) line = 1;
        if (column < 1) column = 1;
        positionLabel.setText(String.format("Ln %d, Col %d", line, column));
    }
    
    /**
     * Updates the file information display.
     *
     * @param encoding the character encoding
     */
    public void updateFileInfo(String encoding) {
        if (encoding == null || encoding.isEmpty()) {
            encoding = "UTF-8";
        }
        fileInfoLabel.setText(encoding);
    }
}
