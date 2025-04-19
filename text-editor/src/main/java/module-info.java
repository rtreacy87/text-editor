module com.texteditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.prefs;
    
    exports com.texteditor;
    exports com.texteditor.model;
    exports com.texteditor.view;
    exports com.texteditor.controller;
}
