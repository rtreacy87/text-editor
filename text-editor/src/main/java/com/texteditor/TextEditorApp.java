package com.texteditor;

import com.texteditor.controller.DocumentController;
import com.texteditor.controller.FileController;
import com.texteditor.model.Document;
import com.texteditor.view.MainWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Main application class for the text editor.
 * This class sets up the JavaFX application and creates the initial UI.
 */
public class TextEditorApp extends Application {

    private MainWindow mainWindow;
    private Document document;
    private DocumentController documentController;
    private FileController fileController;

    @Override
    public void start(Stage primaryStage) {
        // Create the main window
        mainWindow = new MainWindow(primaryStage);

        // Create the document model
        document = new Document();

        // Create controllers
        documentController = new DocumentController(document, mainWindow.getTextArea(), mainWindow.getStatusBar());
        fileController = new FileController(documentController, primaryStage);

        // Set up window close handler
        primaryStage.setOnCloseRequest(event -> {
            if (!fileController.checkUnsavedChanges()) {
                event.consume();
            }
        });

        // Connect menu items to controllers
        setupMenuHandlers();

        // Create a new document
        documentController.newDocument();

        // Show the window
        mainWindow.show();
    }

    /**
     * Sets up handlers for menu items.
     */
    private void setupMenuHandlers() {
        // File menu
        mainWindow.getMenuBar().getMenus().get(0).getItems().get(0).setOnAction(event -> fileController.newFile()); // New
        mainWindow.getMenuBar().getMenus().get(0).getItems().get(1).setOnAction(event -> fileController.openFile()); // Open
        mainWindow.getMenuBar().getMenus().get(0).getItems().get(2).setOnAction(event -> fileController.saveFile()); // Save
        mainWindow.getMenuBar().getMenus().get(0).getItems().get(3).setOnAction(event -> fileController.saveFileAs()); // Save As
        mainWindow.getMenuBar().getMenus().get(0).getItems().get(7).setOnAction(event -> { // Exit
            if (fileController.checkUnsavedChanges()) {
                Platform.exit();
            }
        });

        // View menu
        mainWindow.getMenuBar().getMenus().get(2).getItems().get(0).setOnAction(event -> mainWindow.toggleWordWrap()); // Word Wrap
        mainWindow.getMenuBar().getMenus().get(2).getItems().get(2).setOnAction(event -> mainWindow.toggleLineNumbers()); // Line Numbers

        // Add recent files listener
        fileController.addRecentFilesListener(() -> {
            mainWindow.getMenuBar().updateRecentFilesMenu(fileController.getRecentFiles());
        });

        // Initialize recent files menu
        mainWindow.getMenuBar().updateRecentFilesMenu(fileController.getRecentFiles());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
