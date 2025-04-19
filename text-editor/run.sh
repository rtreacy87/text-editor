#!/bin/bash

# Get the JavaFX modules path
JAVAFX_PATH="$(pwd)/javafx-sdk-21.0.2/lib"

# Run the application
java --module-path $JAVAFX_PATH \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics \
     --add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED \
     --add-exports javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED \
     -cp target/classes com.texteditor.TextEditorApp
