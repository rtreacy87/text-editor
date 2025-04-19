package com.texteditor.view;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * EditorMenuBar provides the menu bar for the text editor.
 */
public class EditorMenuBar extends MenuBar {
    
    private MenuItem undoItem;
    private MenuItem redoItem;
    private Menu recentFilesMenu;
    
    /**
     * Creates a new menu bar.
     */
    public EditorMenuBar() {
        createFileMenu();
        createEditMenu();
        createViewMenu();
        createHelpMenu();
    }
    
    /**
     * Creates the File menu.
     */
    private void createFileMenu() {
        Menu fileMenu = new Menu("File");
        
        MenuItem newItem = new MenuItem("New");
        newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem openItem = new MenuItem("Open...");
        openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem saveAsItem = new MenuItem("Save As...");
        saveAsItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        
        recentFilesMenu = new Menu("Recent Files");
        
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();
        
        MenuItem exitItem = new MenuItem("Exit");
        
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, separator1, 
                                  recentFilesMenu, separator2, exitItem);
        
        this.getMenus().add(fileMenu);
    }
    
    /**
     * Creates the Edit menu.
     */
    private void createEditMenu() {
        Menu editMenu = new Menu("Edit");
        
        undoItem = new MenuItem("Undo");
        undoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        
        redoItem = new MenuItem("Redo");
        redoItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        
        MenuItem selectAllItem = new MenuItem("Select All");
        selectAllItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem findItem = new MenuItem("Find...");
        findItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN));
        
        MenuItem replaceItem = new MenuItem("Replace...");
        replaceItem.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN));
        
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();
        SeparatorMenuItem separator3 = new SeparatorMenuItem();
        
        editMenu.getItems().addAll(undoItem, redoItem, separator1, 
                                  cutItem, copyItem, pasteItem, deleteItem, separator2, 
                                  selectAllItem, separator3, findItem, replaceItem);
        
        this.getMenus().add(editMenu);
    }
    
    /**
     * Creates the View menu.
     */
    private void createViewMenu() {
        Menu viewMenu = new Menu("View");
        
        CheckMenuItem wordWrapItem = new CheckMenuItem("Word Wrap");
        CheckMenuItem statusBarItem = new CheckMenuItem("Status Bar");
        statusBarItem.setSelected(true);
        CheckMenuItem lineNumbersItem = new CheckMenuItem("Line Numbers");
        lineNumbersItem.setSelected(true);
        
        viewMenu.getItems().addAll(wordWrapItem, statusBarItem, lineNumbersItem);
        
        this.getMenus().add(viewMenu);
    }
    
    /**
     * Creates the Help menu.
     */
    private void createHelpMenu() {
        Menu helpMenu = new Menu("Help");
        
        MenuItem aboutItem = new MenuItem("About");
        
        helpMenu.getItems().addAll(aboutItem);
        
        this.getMenus().add(helpMenu);
    }
    
    /**
     * Updates the state of the undo and redo menu items.
     *
     * @param canUndo whether undo is available
     * @param canRedo whether redo is available
     */
    public void updateUndoRedoState(boolean canUndo, boolean canRedo) {
        undoItem.setDisable(!canUndo);
        redoItem.setDisable(!canRedo);
    }
    
    /**
     * Updates the recent files menu.
     *
     * @param recentFiles the list of recent files
     */
    public void updateRecentFilesMenu(java.util.List<String> recentFiles) {
        recentFilesMenu.getItems().clear();
        
        if (recentFiles == null || recentFiles.isEmpty()) {
            MenuItem noRecentFilesItem = new MenuItem("No Recent Files");
            noRecentFilesItem.setDisable(true);
            recentFilesMenu.getItems().add(noRecentFilesItem);
        } else {
            for (String filePath : recentFiles) {
                java.io.File file = new java.io.File(filePath);
                MenuItem item = new MenuItem(file.getName());
                recentFilesMenu.getItems().add(item);
            }
            
            recentFilesMenu.getItems().add(new SeparatorMenuItem());
            MenuItem clearRecentFilesItem = new MenuItem("Clear Recent Files");
            recentFilesMenu.getItems().add(clearRecentFilesItem);
        }
    }
    
    /**
     * Gets the undo menu item.
     *
     * @return the undo menu item
     */
    public MenuItem getUndoItem() {
        return undoItem;
    }
    
    /**
     * Gets the redo menu item.
     *
     * @return the redo menu item
     */
    public MenuItem getRedoItem() {
        return redoItem;
    }
}
