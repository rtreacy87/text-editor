package com.texteditor.controller;

import com.texteditor.model.Document;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * FileController handles file operations for the text editor.
 */
public class FileController {
    
    private Document document;
    private DocumentController documentController;
    private Stage stage;
    private List<String> recentFiles;
    private List<RecentFilesListener> recentFilesListeners = new ArrayList<>();
    
    private static final int MAX_RECENT_FILES = 5;
    private static final String RECENT_FILES_KEY = "recentFiles";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    
    /**
     * Creates a new file controller.
     *
     * @param documentController the document controller
     * @param stage the application stage
     */
    public FileController(DocumentController documentController, Stage stage) {
        if (documentController == null || stage == null) {
            throw new NullPointerException("DocumentController and Stage cannot be null");
        }
        
        this.documentController = documentController;
        this.document = documentController.getDocument();
        this.stage = stage;
        this.recentFiles = loadRecentFiles();
    }
    
    /**
     * Creates a new document.
     * If the current document has unsaved changes, the user will be prompted to save them.
     */
    public void newFile() {
        // Check for unsaved changes
        if (checkUnsavedChanges()) {
            documentController.newDocument();
        }
    }
    
    /**
     * Opens a file selected by the user.
     * If the current document has unsaved changes, the user will be prompted to save them.
     */
    public void openFile() {
        // Check for unsaved changes
        if (!checkUnsavedChanges()) {
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        configureFileChooser(fileChooser);
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }
    
    /**
     * Opens the specified file.
     *
     * @param file the file to open
     */
    public void openFile(File file) {
        if (file == null) {
            throw new NullPointerException("File cannot be null");
        }
        
        if (!file.exists()) {
            showErrorDialog("File Not Found", "The file does not exist", 
                          "The file at " + file.getAbsolutePath() + " could not be found.");
            return;
        }
        
        if (!file.canRead()) {
            showErrorDialog("Permission Denied", "Cannot read file", 
                          "You don't have permission to read the file at " + file.getAbsolutePath());
            return;
        }
        
        try {
            // Check file size before loading
            long fileSize = file.length();
            if (fileSize > MAX_FILE_SIZE) {
                showErrorDialog("File Too Large", "The file is too large to open", 
                              "The maximum supported file size is " + MAX_FILE_SIZE / (1024*1024) + " MB.");
                return;
            }
            
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            document.setText(content);
            document.setFile(file);
            document.resetModified();
            addRecentFile(file.getAbsolutePath());
        } catch (IOException e) {
            showErrorDialog("Error Opening File", "Could not open file: " + file.getName(), e.getMessage());
        }
    }
    
    /**
     * Saves the current document.
     * If the document is not associated with a file, the user will be prompted to select a file.
     *
     * @return true if the document was saved, false otherwise
     */
    public boolean saveFile() {
        if (document.getFile() == null) {
            return saveFileAs();
        } else {
            return saveFile(document.getFile());
        }
    }
    
    /**
     * Saves the current document to a file selected by the user.
     *
     * @return true if the document was saved, false otherwise
     */
    public boolean saveFileAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File As");
        configureFileChooser(fileChooser);
        
        if (document.getFile() != null) {
            fileChooser.setInitialDirectory(document.getFile().getParentFile());
            fileChooser.setInitialFileName(document.getFile().getName());
        }
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            return saveFile(file);
        }
        
        return false;
    }
    
    /**
     * Saves the current document to the specified file.
     *
     * @param file the file to save to
     * @return true if the document was saved, false otherwise
     */
    private boolean saveFile(File file) {
        if (file == null) {
            throw new NullPointerException("File cannot be null");
        }
        
        try {
            // Create a temporary file in the same directory
            File tempFile = File.createTempFile("temp_", ".tmp", file.getParentFile());
            
            // Write content to the temporary file
            Files.writeString(tempFile.toPath(), document.getText(), StandardCharsets.UTF_8);
            
            // Create backup of existing file if it exists
            File backupFile = null;
            if (file.exists()) {
                backupFile = new File(file.getAbsolutePath() + ".bak");
                Files.copy(file.toPath(), backupFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Atomically replace the target file with the temp file
            Files.move(tempFile.toPath(), file.toPath(), 
                     java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // Delete the backup file if everything succeeded
            if (backupFile != null && backupFile.exists()) {
                backupFile.delete();
            }
            
            document.setFile(file);
            document.resetModified();
            addRecentFile(file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            showErrorDialog("Error Saving File", "Could not save file: " + file.getName(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the list of recent files.
     *
     * @return the list of recent files
     */
    public List<String> getRecentFiles() {
        return new ArrayList<>(recentFiles);
    }
    
    /**
     * Opens a recent file.
     *
     * @param filePath the path of the file to open
     */
    public void openRecentFile(String filePath) {
        // Check for unsaved changes
        if (!checkUnsavedChanges()) {
            return;
        }
        
        File file = new File(filePath);
        if (file.exists()) {
            openFile(file);
        } else {
            showErrorDialog("Error Opening Recent File", "File not found: " + filePath, "The file may have been moved or deleted.");
            recentFiles.remove(filePath);
            saveRecentFiles();
            notifyRecentFilesChanged();
        }
    }
    
    /**
     * Clears the list of recent files.
     */
    public void clearRecentFiles() {
        recentFiles.clear();
        saveRecentFiles();
        notifyRecentFilesChanged();
    }
    
    /**
     * Checks if the current document has unsaved changes and prompts the user to save them.
     *
     * @return true if the operation can proceed, false if it should be cancelled
     */
    public boolean checkUnsavedChanges() {
        if (document.isModified()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("Save changes to " + document.getFileName() + "?");
            alert.setContentText("Your changes will be lost if you don't save them.");
            alert.initOwner(stage);
            
            ButtonType saveButton = new ButtonType("Save");
            ButtonType dontSaveButton = new ButtonType("Don't Save");
            ButtonType cancelButton = ButtonType.CANCEL;
            
            alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    return saveFile(); // Only continue if save was successful
                } else if (result.get() == dontSaveButton) {
                    return true; // Continue without saving
                } else {
                    return false; // Cancel the operation
                }
            }
            return false;
        }
        return true; // No unsaved changes, continue
    }
    
    /**
     * Configures the file chooser with appropriate file filters and initial directory.
     *
     * @param fileChooser the file chooser to configure
     */
    private void configureFileChooser(FileChooser fileChooser) {
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // Set initial directory to the last used directory or user home
        if (document.getFile() != null) {
            fileChooser.setInitialDirectory(document.getFile().getParentFile());
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
    }
    
    /**
     * Adds a file to the list of recent files.
     *
     * @param filePath the path of the file to add
     */
    private void addRecentFile(String filePath) {
        // Remove if already exists (to move it to the top)
        recentFiles.remove(filePath);
        
        // Add to the beginning of the list
        recentFiles.add(0, filePath);
        
        // Trim the list if it exceeds the maximum size
        while (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.remove(recentFiles.size() - 1);
        }
        
        saveRecentFiles();
        notifyRecentFilesChanged();
    }
    
    /**
     * Loads the list of recent files from preferences.
     *
     * @return the list of recent files
     */
    private List<String> loadRecentFiles() {
        List<String> files = new ArrayList<>();
        Preferences prefs = Preferences.userNodeForPackage(FileController.class);
        String recentFilesStr = prefs.get(RECENT_FILES_KEY, "");
        
        if (!recentFilesStr.isEmpty()) {
            String[] paths = recentFilesStr.split("\\|");
            for (String path : paths) {
                if (!path.isEmpty()) {
                    files.add(path);
                }
            }
        }
        
        return files;
    }
    
    /**
     * Saves the list of recent files to preferences.
     */
    private void saveRecentFiles() {
        Preferences prefs = Preferences.userNodeForPackage(FileController.class);
        StringBuilder sb = new StringBuilder();
        
        for (String file : recentFiles) {
            sb.append(file).append("|");
        }
        
        prefs.put(RECENT_FILES_KEY, sb.toString());
    }
    
    /**
     * Shows an error dialog.
     *
     * @param title the dialog title
     * @param header the dialog header
     * @param content the dialog content
     */
    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(stage);
        alert.showAndWait();
    }
    
    /**
     * Adds a recent files listener.
     *
     * @param listener the listener to add
     */
    public void addRecentFilesListener(RecentFilesListener listener) {
        if (listener != null) {
            recentFilesListeners.add(listener);
        }
    }
    
    /**
     * Removes a recent files listener.
     *
     * @param listener the listener to remove
     */
    public void removeRecentFilesListener(RecentFilesListener listener) {
        recentFilesListeners.remove(listener);
    }
    
    /**
     * Notifies all listeners that the list of recent files has changed.
     */
    private void notifyRecentFilesChanged() {
        for (RecentFilesListener listener : recentFilesListeners) {
            listener.onRecentFilesChanged();
        }
    }
    
    /**
     * Interface for listening to recent files changes.
     */
    public interface RecentFilesListener {
        /**
         * Called when the list of recent files changes.
         */
        void onRecentFilesChanged();
    }
}
