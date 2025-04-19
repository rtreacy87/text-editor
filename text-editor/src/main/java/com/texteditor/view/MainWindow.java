package com.texteditor.view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * MainWindow is the main application window for the text editor.
 */
public class MainWindow {
    
    private Stage stage;
    private BorderPane rootLayout;
    private EditorMenuBar menuBar;
    private EditorTextArea textArea;
    private StatusBar statusBar;
    private LineNumberArea lineNumberArea;
    private boolean lineNumbersVisible = true;
    private boolean wordWrapEnabled = false;
    
    /**
     * Creates a new main window.
     *
     * @param stage the JavaFX stage
     */
    public MainWindow(Stage stage) {
        this.stage = stage;
        initializeUI();
    }
    
    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        // Create the root layout (BorderPane is ideal for text editors)
        rootLayout = new BorderPane();
        
        // Create and add the menu bar
        menuBar = new EditorMenuBar();
        rootLayout.setTop(menuBar);
        
        // Create and add the text area
        textArea = new EditorTextArea();
        
        // Create and add the line number area
        lineNumberArea = new LineNumberArea(textArea);
        
        // Create a container for the text area and line numbers
        HBox textAreaContainer = new HBox();
        textAreaContainer.getChildren().addAll(lineNumberArea, textArea);
        HBox.setHgrow(textArea, Priority.ALWAYS);
        
        rootLayout.setCenter(textAreaContainer);
        
        // Create and add the status bar
        statusBar = new StatusBar();
        rootLayout.setBottom(statusBar);
        
        // Create a scene with the root layout
        Scene scene = new Scene(rootLayout, 800, 600);
        
        // Configure the stage
        stage.setTitle("Text Editor");
        stage.setScene(scene);
        
        // Set minimum size constraints
        stage.setMinWidth(400);
        stage.setMinHeight(300);
    }
    
    /**
     * Shows the window.
     */
    public void show() {
        stage.show();
    }
    
    /**
     * Toggles the visibility of line numbers.
     */
    public void toggleLineNumbers() {
        lineNumbersVisible = !lineNumbersVisible;
        lineNumberArea.setVisible(lineNumbersVisible);
        lineNumberArea.setManaged(lineNumbersVisible);
    }
    
    /**
     * Toggles word wrap.
     */
    public void toggleWordWrap() {
        wordWrapEnabled = !wordWrapEnabled;
        textArea.setWordWrap(wordWrapEnabled);
    }
    
    /**
     * Gets the root layout.
     *
     * @return the root layout
     */
    public BorderPane getRootLayout() {
        return rootLayout;
    }
    
    /**
     * Gets the menu bar.
     *
     * @return the menu bar
     */
    public EditorMenuBar getMenuBar() {
        return menuBar;
    }
    
    /**
     * Gets the text area.
     *
     * @return the text area
     */
    public EditorTextArea getTextArea() {
        return textArea;
    }
    
    /**
     * Gets the status bar.
     *
     * @return the status bar
     */
    public StatusBar getStatusBar() {
        return statusBar;
    }
    
    /**
     * Gets the stage.
     *
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }
}
