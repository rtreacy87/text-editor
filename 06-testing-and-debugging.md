# Testing and Debugging

This guide will walk you through testing and debugging your text editor application. Proper testing is crucial to ensure your application works correctly and provides a good user experience.

## Table of Contents
- [Understanding Testing for Text Editors](#understanding-testing-for-text-editors)
- [Setting Up a Testing Framework](#setting-up-a-testing-framework)
- [Writing Unit Tests](#writing-unit-tests)
- [Testing the User Interface](#testing-the-user-interface)
- [Integration Testing](#integration-testing)
- [Performance Testing](#performance-testing)
- [Code Coverage Analysis](#code-coverage-analysis)
- [Static Code Analysis](#static-code-analysis)
- [Security Testing](#security-testing)
- [Accessibility Testing](#accessibility-testing)
- [Defensive Programming](#defensive-programming)
- [Debugging Common Issues](#debugging-common-issues)
- [Continuous Integration](#continuous-integration)

## Understanding Testing for Text Editors

Testing a text editor involves verifying several aspects:

1. **Functionality**: Do all features work as expected?
2. **Usability**: Is the interface intuitive and responsive?
3. **Performance**: Does the editor handle large files efficiently?
4. **Reliability**: Does the editor prevent data loss?
5. **Cross-platform compatibility**: Does it work on all target platforms?

Let's explore how to test each of these aspects.

## Setting Up a Testing Framework

For our Java-based text editor, we'll use JUnit for unit testing and TestFX for UI testing. If you haven't set up your development environment yet, please refer to the [Project Setup and Architecture](01-project-setup-and-architecture.md) guide first.

### Step 1: Add Testing Dependencies

Add these dependencies to your `pom.xml` file:

```xml
<dependencies>
    <!-- Existing dependencies -->

    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.8.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.8.2</version>
        <scope>test</scope>
    </dependency>

    <!-- Mockito for mocking -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.3.1</version>
        <scope>test</scope>
    </dependency>

    <!-- TestFX for UI testing -->
    <dependency>
        <groupId>org.testfx</groupId>
        <artifactId>testfx-core</artifactId>
        <version>4.0.16-alpha</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testfx</groupId>
        <artifactId>testfx-junit5</artifactId>
        <version>4.0.16-alpha</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Step 2: Create a Test Directory Structure

In VSCode, you can easily create the test directory structure:

1. Right-click on your project in the Explorer panel
2. Select "New Folder" and create `src/test/java`
3. Inside this folder, create the package structure matching your main code:

```
src/
├── test/
│   ├── java/
│   │   ├── com/
│   │   │   ├── texteditor/
│   │   │   │   ├── model/
│   │   │   │   ├── view/
│   │   │   │   ├── controller/
```

## Writing Unit Tests

Unit tests verify that individual components work correctly in isolation. Let's write some unit tests for our text editor components.

### Step 1: Testing the Document Model

Create a new file `DocumentTest.java` in the `src/test/java/com/texteditor/model` directory:

```java
package com.texteditor.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentTest {

    private Document document;

    @BeforeEach
    public void setUp() {
        document = new Document();
    }

    @Test
    public void testNewDocumentIsEmpty() {
        assertEquals("", document.getText());
    }

    @Test
    public void testSetText() {
        String text = "Hello, world!";
        document.setText(text);
        assertEquals(text, document.getText());
    }

    @Test
    public void testInsertText() {
        document.setText("Hello world!");
        document.insertText(6, "beautiful ");
        assertEquals("Hello beautiful world!", document.getText());
    }

    @Test
    public void testDeleteText() {
        document.setText("Hello beautiful world!");
        document.deleteText(6, 16); // Delete "beautiful "
        assertEquals("Hello world!", document.getText());
    }

    @Test
    public void testModificationState() {
        assertFalse(document.isModified());
        document.setText("Hello");
        assertTrue(document.isModified());
        document.resetModified();
        assertFalse(document.isModified());
    }
}
```

### Step 2: Testing the Text Buffer

Create a new file `TextBufferTest.java` in the `src/test/java/com/texteditor/model` directory:

```java
package com.texteditor.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TextBufferTest {

    private TextBuffer buffer;

    @BeforeEach
    public void setUp() {
        buffer = new TextBuffer();
    }

    @Test
    public void testNewBufferIsEmpty() {
        assertEquals("", buffer.getText());
        assertEquals(0, buffer.length());
    }

    @Test
    public void testInsert() {
        buffer.insert(0, "Hello");
        assertEquals("Hello", buffer.getText());
        buffer.insert(5, " world");
        assertEquals("Hello world", buffer.getText());
        buffer.insert(5, ",");
        assertEquals("Hello, world", buffer.getText());
    }

    @Test
    public void testDelete() {
        buffer.setText("Hello, world");
        buffer.delete(5, 7); // Delete ", "
        assertEquals("Helloworld", buffer.getText());
    }

    @Test
    public void testReplace() {
        buffer.setText("Hello, world");
        buffer.replace(7, 12, "Java");
        assertEquals("Hello, Java", buffer.getText());
    }

    @Test
    public void testGetTextRange() {
        buffer.setText("Hello, world");
        assertEquals("Hello", buffer.getText(0, 5));
        assertEquals("world", buffer.getText(7, 12));
    }
}
```

### Step 3: Testing the Command System

Create a new file `CommandManagerTest.java` in the `src/test/java/com/texteditor/model/command` directory:

```java
package com.texteditor.model.command;

import com.texteditor.model.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandManagerTest {

    private CommandManager commandManager;
    private Document document;

    @BeforeEach
    public void setUp() {
        document = new Document();
        commandManager = new CommandManager(10);
    }

    @Test
    public void testExecuteCommand() {
        InsertTextCommand command = new InsertTextCommand(document, 0, "Hello");
        commandManager.executeCommand(command);
        assertEquals("Hello", document.getText());
    }

    @Test
    public void testUndo() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello"));
        assertTrue(commandManager.canUndo());
        commandManager.undo();
        assertEquals("", document.getText());
        assertFalse(commandManager.canUndo());
    }

    @Test
    public void testRedo() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello"));
        commandManager.undo();
        assertTrue(commandManager.canRedo());
        commandManager.redo();
        assertEquals("Hello", document.getText());
        assertFalse(commandManager.canRedo());
    }

    @Test
    public void testMultipleUndoRedo() {
        commandManager.executeCommand(new InsertTextCommand(document, 0, "Hello"));
        commandManager.executeCommand(new InsertTextCommand(document, 5, " world"));

        // Undo both commands
        commandManager.undo();
        assertEquals("Hello", document.getText());
        commandManager.undo();
        assertEquals("", document.getText());

        // Redo both commands
        commandManager.redo();
        assertEquals("Hello", document.getText());
        commandManager.redo();
        assertEquals("Hello world", document.getText());
    }
}
```

## Testing the User Interface

UI testing verifies that the user interface works correctly and responds appropriately to user input.

### Step 1: Create a Base UI Test Class

Create a new file `BaseUITest.java` in the `src/test/java/com/texteditor/view` directory:

```java
package com.texteditor.view;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.TimeoutException;

public abstract class BaseUITest extends ApplicationTest {

    protected FxRobot robot;

    @BeforeEach
    public void setUpClass() throws Exception {
        robot = new FxRobot();
    }

    @AfterEach
    public void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    protected <T extends Node> T find(String query) {
        return lookup(query).query();
    }
}
```

### Step 2: Testing the Main Window

Create a new file `MainWindowTest.java` in the `src/test/java/com/texteditor/view` directory:

```java
package com.texteditor.view;

import com.texteditor.TextEditorApp;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.TextInputControlMatchers;

import static org.junit.jupiter.api.Assertions.*;

public class MainWindowTest extends BaseUITest {

    @Override
    public void start(Stage stage) throws Exception {
        new TextEditorApp().start(stage);
    }

    @Test
    public void testInitialState() {
        // Verify that the text area is empty
        TextArea textArea = find("#textArea");
        FxAssert.verifyThat(textArea, TextInputControlMatchers.hasText(""));

        // Verify that the window title is correct
        assertEquals("Untitled - Text Editor", robot.window().getTitle());
    }

    @Test
    public void testTypingText() {
        // Type some text
        robot.clickOn("#textArea");
        robot.write("Hello, world!");

        // Verify that the text was entered
        TextArea textArea = find("#textArea");
        FxAssert.verifyThat(textArea, TextInputControlMatchers.hasText("Hello, world!"));

        // Verify that the window title shows the modified indicator
        assertTrue(robot.window().getTitle().contains("*"));
    }

    @Test
    public void testCutCopyPaste() {
        // Type some text
        robot.clickOn("#textArea");
        robot.write("Hello, world!");

        // Select all text
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);

        // Copy the text
        robot.press(KeyCode.CONTROL).press(KeyCode.C).release(KeyCode.C).release(KeyCode.CONTROL);

        // Delete the text
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

        // Verify that the text area is empty
        TextArea textArea = find("#textArea");
        FxAssert.verifyThat(textArea, TextInputControlMatchers.hasText(""));

        // Paste the text
        robot.press(KeyCode.CONTROL).press(KeyCode.V).release(KeyCode.V).release(KeyCode.CONTROL);

        // Verify that the text was pasted
        FxAssert.verifyThat(textArea, TextInputControlMatchers.hasText("Hello, world!"));
    }
}
```

### Step 3: Testing Menu Actions

Create a new file `MenuActionsTest.java` in the `src/test/java/com/texteditor/view` directory:

```java
package com.texteditor.view;

import com.texteditor.TextEditorApp;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.TextInputControlMatchers;

public class MenuActionsTest extends BaseUITest {

    @Override
    public void start(Stage stage) throws Exception {
        new TextEditorApp().start(stage);
    }

    @Test
    public void testNewFile() {
        // Type some text
        robot.clickOn("#textArea");
        robot.write("Hello, world!");

        // Click on File > New
        robot.clickOn("File").clickOn("New");

        // Verify that the text area is empty
        TextArea textArea = find("#textArea");
        FxAssert.verifyThat(textArea, TextInputControlMatchers.hasText(""));
    }

    @Test
    public void testUndoRedo() {
        // Type some text
        robot.clickOn("#textArea");
        robot.write("Hello");

        // Type more text
        robot.write(", world!");

        // Undo
        robot.clickOn("Edit").clickOn("Undo");

        // Verify that the last edit was undone
        TextArea textArea = find("#textArea");
        FxAssert.verifyThat(textArea, TextInputControlMatchers.hasText("Hello"));

        // Redo
        robot.clickOn("Edit").clickOn("Redo");

        // Verify that the edit was redone
        FxAssert.verifyThat(textArea, TextInputControlMatchers.hasText("Hello, world!"));
    }

    @Test
    public void testFind() {
        // Type some text
        robot.clickOn("#textArea");
        robot.write("Hello, world! Hello, Java!");

        // Open Find dialog
        robot.clickOn("Edit").clickOn("Find...");

        // Enter search text
        robot.write("Hello");

        // Click Find
        robot.clickOn("Find");

        // Verify that the first "Hello" is selected
        TextArea textArea = find("#textArea");
        assertEquals("Hello", textArea.getSelectedText());

        // Click Find again to find the next occurrence
        robot.clickOn("Find");

        // Verify that the second "Hello" is selected
        assertEquals("Hello", textArea.getSelectedText());
        assertEquals(14, textArea.getSelection().getStart());
    }
}
```

## Integration Testing

Integration testing verifies that different components of your text editor work correctly together. This is crucial for ensuring that the Model, View, and Controller components interact properly.

### Step 1: Create an Integration Test Class

Create a new file `DocumentIntegrationTest.java` in the `src/test/java/com/texteditor` directory:

```java
package com.texteditor;

import com.texteditor.model.Document;
import com.texteditor.controller.DocumentController;
import com.texteditor.view.EditorTextArea;
import com.texteditor.view.StatusBar;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class DocumentIntegrationTest {

    private static final Logger logger = Logger.getLogger(DocumentIntegrationTest.class.getName());
    private Document document;
    private EditorTextArea textArea;
    private StatusBar statusBar;
    private DocumentController documentController;

    @BeforeAll
    public static void initJFX() {
        try {
            // Initialize JavaFX for testing
            new JFXPanel();
            logger.info("JavaFX initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize JavaFX: " + e.getMessage());
            // Don't fail the test setup, but tests will be skipped
        }
    }

    @BeforeEach
    public void setUp() {
        // Defensive check to ensure JavaFX is available
        try {
            document = new Document();
            textArea = new EditorTextArea();
            statusBar = new StatusBar();
            documentController = new DocumentController(document, textArea, statusBar);
        } catch (Exception e) {
            logger.severe("Test setup failed: " + e.getMessage());
            fail("Failed to set up test environment: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS) // Defensive timeout
    public void testDocumentToViewSync() {
        // Verify components are initialized
        assertNotNull(document, "Document should not be null");
        assertNotNull(textArea, "TextArea should not be null");

        // Change the document and verify the view updates
        document.setText("Hello, world!");

        // Wait for JavaFX thread to process changes with timeout protection
        waitForJavaFXThread(500); // 500ms should be enough

        // Verify synchronization
        assertEquals("Hello, world!", textArea.getText(),
                   "TextArea content should match Document content");
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS) // Defensive timeout
    public void testViewToDocumentSync() {
        // Verify components are initialized
        assertNotNull(document, "Document should not be null");
        assertNotNull(textArea, "TextArea should not be null");

        // Change the view and verify the document updates
        textArea.setText("Hello from view!");

        // Wait for changes to propagate with timeout protection
        waitForJavaFXThread(500);

        // Verify synchronization
        assertEquals("Hello from view!", document.getText(),
                   "Document content should match TextArea content");
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS) // Defensive timeout
    public void testCursorPositionSync() {
        // Skip test if JavaFX is not available
        assumeTrue(textArea != null, "TextArea is not available");

        // Set up test data
        document.setText("Hello, world!");

        // Wait for JavaFX thread to process changes
        waitForJavaFXThread(500);

        // Verify text was set correctly before testing cursor
        assertEquals("Hello, world!", textArea.getText(),
                   "TextArea content should match Document content");

        // Set cursor position in document (with bounds checking)
        int position = 7;
        if (position >= 0 && position <= document.getText().length()) {
            document.getCursor().setPosition(position);
        } else {
            fail("Invalid cursor position: " + position);
        }

        // Wait for changes to propagate
        waitForJavaFXThread(500);

        // Verify cursor position
        assertEquals(position, textArea.getCaretPosition(),
                   "TextArea cursor position should match Document cursor position");
    }

    /**
     * Helper method to safely wait for JavaFX thread with exception handling
     */
    private void waitForJavaFXThread(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            logger.warning("Thread sleep interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
}
```

### Step 2: Testing File Operations Integration

Create a new file `FileOperationsIntegrationTest.java` in the `src/test/java/com/texteditor` directory:

```java
package com.texteditor;

import com.texteditor.controller.DocumentController;
import com.texteditor.controller.FileController;
import com.texteditor.model.Document;
import com.texteditor.view.EditorTextArea;
import com.texteditor.view.StatusBar;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileOperationsIntegrationTest {

    @TempDir
    Path tempDir;

    private Document document;
    private EditorTextArea textArea;
    private StatusBar statusBar;
    private DocumentController documentController;
    private FileController fileController;
    private Stage mockStage;

    @BeforeAll
    public static void initJFX() {
        // Initialize JavaFX for testing
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() {
        document = new Document();
        textArea = new EditorTextArea();
        statusBar = new StatusBar();
        documentController = new DocumentController(document, textArea, statusBar);
        mockStage = new Stage(); // This is a mock stage for testing
        fileController = new FileController(documentController, mockStage);
    }

    @Test
    public void testSaveAndLoadFile() throws IOException {
        // Create a test file
        File testFile = tempDir.resolve("test.txt").toFile();

        // Set document content
        document.setText("Test content for file operations");
        document.setFile(testFile);

        // Save the file
        fileController.saveFile();

        // Verify file was created and has correct content
        assertTrue(testFile.exists());
        String fileContent = Files.readString(testFile.toPath());
        assertEquals("Test content for file operations", fileContent);

        // Create a new document
        document = new Document();
        documentController = new DocumentController(document, textArea, statusBar);
        fileController = new FileController(documentController, mockStage);

        // Load the file
        fileController.openFile(testFile);

        // Verify content was loaded correctly
        assertEquals("Test content for file operations", document.getText());
    }
}
```

## Performance Testing

Performance testing ensures that your text editor can handle large files and operations efficiently.

### Step 1: Create a Performance Test Class

Create a new file `PerformanceTest.java` in the `src/test/java/com/texteditor` directory:

```java
package com.texteditor;

import com.texteditor.model.Document;
import com.texteditor.model.TextBuffer;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class PerformanceTest {

    private static final int SMALL_FILE_SIZE = 10_000; // 10KB
    private static final int MEDIUM_FILE_SIZE = 1_000_000; // 1MB
    private static final int LARGE_FILE_SIZE = 10_000_000; // 10MB

    @Test
    public void testTextBufferPerformance() {
        // Test with small file
        TextBuffer buffer = new TextBuffer();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < SMALL_FILE_SIZE; i++) {
            buffer.insert(buffer.length(), "a");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time to insert " + SMALL_FILE_SIZE + " characters: " + (endTime - startTime) + "ms");

        // Test with medium file
        buffer = new TextBuffer();
        startTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MEDIUM_FILE_SIZE; i++) {
            sb.append("a");
        }
        buffer.setText(sb.toString());
        endTime = System.currentTimeMillis();
        System.out.println("Time to set " + MEDIUM_FILE_SIZE + " characters: " + (endTime - startTime) + "ms");

        // Test random access
        startTime = System.currentTimeMillis();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int position = random.nextInt(buffer.length());
            buffer.insert(position, "b");
            buffer.delete(position, position + 1);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time for 1000 random edits in " + MEDIUM_FILE_SIZE + " characters: " + (endTime - startTime) + "ms");
    }

    @Test
    public void testFileLoadingPerformance() throws IOException {
        // Create a large test file
        File testFile = createTestFile(MEDIUM_FILE_SIZE);

        // Test loading time
        long startTime = System.currentTimeMillis();
        String content = Files.readString(testFile.toPath());
        long endTime = System.currentTimeMillis();
        System.out.println("Time to load " + MEDIUM_FILE_SIZE + " characters: " + (endTime - startTime) + "ms");

        // Clean up
        testFile.delete();
    }

    @Test
    public void testUndoRedoPerformance() {
        // Create a document with command manager
        Document document = new Document();

        // Perform a large number of operations
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            document.insertText(document.getText().length(), "a");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time to perform 1000 insert operations: " + (endTime - startTime) + "ms");

        // Test undo performance
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            document.undo();
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time to undo 1000 operations: " + (endTime - startTime) + "ms");

        // Test redo performance
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            document.redo();
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time to redo 1000 operations: " + (endTime - startTime) + "ms");
    }

    private File createTestFile(int size) throws IOException {
        File file = File.createTempFile("test", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < size; i++) {
                writer.write("a");
            }
        }
        return file;
    }
}
```

## Code Coverage Analysis

Code coverage analysis helps you identify which parts of your code are being tested and which parts need more testing.

### Step 1: Add JaCoCo Plugin

Add the JaCoCo plugin to your `pom.xml` file:

```xml
<build>
    <plugins>
        <!-- Other plugins -->

        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.7</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Step 2: Run Code Coverage Analysis

Run the following command to generate a code coverage report:

```bash
mvn clean test
```

The report will be generated in the `target/site/jacoco` directory. Open the `index.html` file in a web browser to view the report.

### Step 3: Analyze the Report

The report shows the percentage of code covered by tests for each class and method. Look for areas with low coverage and add more tests to improve coverage.

Here's what to look for in the report:

- **Line Coverage**: Percentage of lines executed during tests
- **Branch Coverage**: Percentage of branches (if/else, switch) executed during tests
- **Method Coverage**: Percentage of methods called during tests
- **Class Coverage**: Percentage of classes instantiated during tests

### Step 4: Improve Test Coverage

Focus on improving coverage for critical components like:

- Document model
- Text buffer
- Command system (undo/redo)
- File operations

Aim for at least 80% line coverage for these critical components.

### Step 5: Write Tests for Edge Cases and Error Handling

Defensive programming requires thorough testing of edge cases and error handling. Add tests for:

```java
package com.texteditor.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

public class TextBufferEdgeCaseTest {

    @Test
    public void testNullTextHandling() {
        TextBuffer buffer = new TextBuffer();

        // Test that setting null text is handled gracefully
        assertThrows(NullPointerException.class, () -> {
            buffer.setText(null);
        }, "Setting null text should throw NullPointerException");

        // Verify buffer state remains valid
        assertEquals("", buffer.getText(), "Buffer should remain empty after null text attempt");
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, -1, Integer.MIN_VALUE})
    public void testNegativePositionHandling(int position) {
        TextBuffer buffer = new TextBuffer("Hello");

        // Test that negative positions are handled gracefully
        assertThrows(IllegalArgumentException.class, () -> {
            buffer.insert(position, "text");
        }, "Inserting at negative position should throw IllegalArgumentException");

        // Verify buffer state remains valid
        assertEquals("Hello", buffer.getText(), "Buffer content should remain unchanged");
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 10, Integer.MAX_VALUE})
    public void testPositionBeyondLengthHandling(int position) {
        TextBuffer buffer = new TextBuffer("Hello");

        // Test that positions beyond length are handled gracefully
        assertThrows(IllegalArgumentException.class, () -> {
            buffer.insert(position, "text");
        }, "Inserting beyond buffer length should throw IllegalArgumentException");

        // Verify buffer state remains valid
        assertEquals("Hello", buffer.getText(), "Buffer content should remain unchanged");
    }

    @Test
    public void testDeleteWithInvalidRange() {
        TextBuffer buffer = new TextBuffer("Hello, world");

        // Test deleting with start > end
        assertThrows(IllegalArgumentException.class, () -> {
            buffer.delete(5, 3);
        }, "Deleting with start > end should throw IllegalArgumentException");

        // Test deleting with negative start
        assertThrows(IllegalArgumentException.class, () -> {
            buffer.delete(-1, 5);
        }, "Deleting with negative start should throw IllegalArgumentException");

        // Test deleting with end beyond length
        assertThrows(IllegalArgumentException.class, () -> {
            buffer.delete(5, 20);
        }, "Deleting with end beyond length should throw IllegalArgumentException");

        // Verify buffer state remains valid
        assertEquals("Hello, world", buffer.getText(), "Buffer content should remain unchanged");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testSearchWithEmptyString(String searchText) {
        TextBuffer buffer = new TextBuffer("Hello, world");

        // Test that searching for null or empty string is handled gracefully
        if (searchText == null) {
            assertThrows(NullPointerException.class, () -> {
                buffer.indexOf(searchText, 0);
            }, "Searching for null should throw NullPointerException");
        } else {
            assertEquals(-1, buffer.indexOf(searchText, 0),
                       "Searching for empty string should return -1");
        }
    }
}
```

This test class demonstrates how to test the defensive programming aspects of your code, ensuring that your application handles invalid inputs and edge cases gracefully.

## Static Code Analysis

Static code analysis helps identify potential bugs, code smells, and maintainability issues without running the code.

### Step 1: Add SpotBugs Plugin

Add the SpotBugs plugin to your `pom.xml` file:

```xml
<build>
    <plugins>
        <!-- Other plugins -->

        <plugin>
            <groupId>com.github.spotbugs</groupId>
            <artifactId>spotbugs-maven-plugin</artifactId>
            <version>4.5.3.0</version>
            <dependencies>
                <dependency>
                    <groupId>com.github.spotbugs</groupId>
                    <artifactId>spotbugs</artifactId>
                    <version>4.5.3</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

### Step 2: Add PMD Plugin

Add the PMD plugin to your `pom.xml` file:

```xml
<build>
    <plugins>
        <!-- Other plugins -->

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-pmd-plugin</artifactId>
            <version>3.16.0</version>
            <configuration>
                <linkXRef>false</linkXRef>
                <sourceEncoding>UTF-8</sourceEncoding>
                <minimumTokens>100</minimumTokens>
                <targetJdk>17</targetJdk>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Step 3: Run Static Analysis

Run the following commands to perform static analysis:

```bash
# Run SpotBugs
mvn spotbugs:check

# Run PMD
mvn pmd:check
```

### Step 4: Analyze and Fix Issues

Review the reported issues and fix them. Common issues include:

- **Null pointer dereferences**: Accessing methods or fields of potentially null objects
- **Resource leaks**: Not closing files, streams, or other resources
- **Unused code**: Methods, fields, or variables that are never used
- **Complex methods**: Methods that are too long or have too many branches
- **Duplicate code**: Code that is repeated in multiple places

## Security Testing

Security testing helps identify potential security vulnerabilities in your application.

### Step 1: Add Dependency Check Plugin

Add the OWASP Dependency Check plugin to your `pom.xml` file:

```xml
<build>
    <plugins>
        <!-- Other plugins -->

        <plugin>
            <groupId>org.owasp</groupId>
            <artifactId>dependency-check-maven</artifactId>
            <version>6.5.3</version>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Step 2: Run Dependency Check

Run the following command to check for vulnerabilities in your dependencies:

```bash
mvn dependency-check:check
```

### Step 3: Implement Security Best Practices with Defensive Programming

Implement these security best practices in your text editor:

1. **Validate file paths**: Prevent path traversal attacks by validating file paths

```java
public class FilePathValidator {

    private static final String[] FORBIDDEN_PATTERNS = {
        "..", // Directory traversal
        "~",  // Home directory
        "/etc/", // System files
        "/var/", // System files
        "/dev/", // Device files
        "/proc/", // Process information
        "/sys/"  // System information
    };

    /**
     * Validates a file path for security concerns
     * @param file The file to validate
     * @throws SecurityException if the file path is potentially unsafe
     */
    public static void validateFilePath(File file) throws SecurityException {
        if (file == null) {
            throw new SecurityException("File cannot be null");
        }

        String path = file.getAbsolutePath();

        // Check for forbidden patterns
        for (String pattern : FORBIDDEN_PATTERNS) {
            if (path.contains(pattern)) {
                throw new SecurityException("Path contains forbidden pattern: " + pattern);
            }
        }

        // Check if file is within allowed directories
        if (!isInAllowedDirectory(file)) {
            throw new SecurityException("File is outside of allowed directories");
        }
    }

    /**
     * Checks if a file is within allowed directories
     * @param file The file to check
     * @return true if the file is in an allowed directory, false otherwise
     */
    private static boolean isInAllowedDirectory(File file) {
        try {
            // Get canonical paths to prevent path manipulation
            String canonicalPath = file.getCanonicalPath();
            String userHome = new File(System.getProperty("user.home")).getCanonicalPath();
            String userDir = new File(System.getProperty("user.dir")).getCanonicalPath();

            // Allow files in user home directory or current working directory
            return canonicalPath.startsWith(userHome) || canonicalPath.startsWith(userDir);
        } catch (IOException e) {
            // If we can't resolve the path, it's safer to deny access
            return false;
        }
    }
}
```

2. **Handle sensitive data securely**: Don't store passwords or sensitive data in plain text

```java
public class SecurePreferences {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH = 256;

    /**
     * Securely store a sensitive value
     * @param key The preference key
     * @param value The sensitive value to store
     */
    public void storeSecurely(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value cannot be null");
        }

        try {
            // Generate a secure key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_LENGTH);
            SecretKey secretKey = keyGen.generateKey();

            // Store the key securely (using platform-specific secure storage)
            storeKey(key, secretKey);

            // Encrypt the value
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            // Store the IV and encrypted value
            storeIV(key, iv);
            storeEncryptedValue(key, encryptedValue);

        } catch (Exception e) {
            // Log the error type but not the actual data
            logger.severe("Error storing sensitive data: " + e.getClass().getName());
            throw new SecurityException("Failed to store data securely");
        }
    }

    // Other methods for retrieving and managing secure data...
}
```

3. **Use secure file operations**: Use atomic file operations to prevent data loss

```java
public void saveFileSafely(File file, String content) {
    if (file == null || content == null) {
        throw new IllegalArgumentException("File and content cannot be null");
    }

    // Create a temporary file in the same directory
    File tempFile = null;
    try {
        tempFile = File.createTempFile("temp_", ".tmp", file.getParentFile());

        // Write content to the temporary file
        Files.writeString(tempFile.toPath(), content, StandardCharsets.UTF_8);

        // Ensure the write is complete by forcing a sync to disk
        try (FileChannel channel = FileChannel.open(tempFile.toPath(), StandardOpenOption.WRITE)) {
            channel.force(true);
        }

        // Create backup of existing file if it exists
        File backupFile = null;
        if (file.exists()) {
            backupFile = new File(file.getAbsolutePath() + ".bak");
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // Atomically replace the target file with the temp file
        Files.move(tempFile.toPath(), file.toPath(),
                 StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        // Delete the backup file if everything succeeded
        if (backupFile != null && backupFile.exists()) {
            backupFile.delete();
        }

    } catch (IOException e) {
        logger.severe("Error during safe file save: " + e.getMessage());

        // Try to recover using the backup if available
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }

        throw new IOException("Failed to save file safely: " + e.getMessage(), e);
    }
}
```

4. **Implement proper error handling**: Don't expose sensitive information in error messages

```java
public void handleSecurityException(Exception e, String operation) {
    // Log the full exception for debugging (server-side only)
    logger.log(Level.SEVERE, "Security exception during " + operation, e);

    // Create a sanitized message for the user
    String userMessage;
    if (e instanceof SecurityException) {
        userMessage = "Security violation detected. Operation not permitted.";
    } else if (e instanceof IOException) {
        userMessage = "File operation failed. Please check permissions and try again.";
    } else {
        userMessage = "An error occurred. Please try again later.";
    }

    // Show sanitized message to user
    showErrorDialog("Security Error", operation + " failed", userMessage);
}
```

5. **Limit file access**: Only access files that the user has permission to access

```java
public boolean canAccessFile(File file) {
    if (file == null) {
        return false;
    }

    try {
        // Validate the file path first
        FilePathValidator.validateFilePath(file);

        // Check if file exists
        if (!file.exists()) {
            return false;
        }

        // Check read permission
        if (!file.canRead()) {
            return false;
        }

        // For write operations, check write permission
        if (isWriteOperation && !file.canWrite()) {
            return false;
        }

        // Additional platform-specific checks can be added here

        return true;
    } catch (SecurityException e) {
        logger.warning("Security check failed for file: " + file.getAbsolutePath());
        return false;
    }
}

### Step 4: Test for Security Vulnerabilities

Create tests that specifically check for security issues:

```java
package com.texteditor;

import com.texteditor.controller.FileController;
import com.texteditor.model.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityTest {

    @TempDir
    Path tempDir;

    @Test
    public void testPathTraversal() {
        // Create a file controller
        FileController fileController = new FileController(null, null);

        // Try to access a file outside the allowed directory
        File file = new File(tempDir.toString() + "/../../../etc/passwd");

        // This should be prevented by the file controller
        assertThrows(SecurityException.class, () -> {
            fileController.validateFilePath(file);
        });
    }
}
```

## Accessibility Testing

Accessibility testing ensures that your text editor can be used by people with disabilities.

### Step 1: Add Accessibility Testing Tools

Add the Axe JavaFX library to your `pom.xml` file:

```xml
<dependencies>
    <!-- Other dependencies -->

    <dependency>
        <groupId>com.deque.html.axe-core</groupId>
        <artifactId>axe-javafx</artifactId>
        <version>4.4.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Step 2: Create Accessibility Tests

Create tests that check for accessibility issues:

```java
package com.texteditor;

import com.deque.axe.AXE;
import com.texteditor.view.MainWindow;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccessibilityTest extends ApplicationTest {

    private MainWindow mainWindow;

    @Override
    public void start(Stage stage) {
        mainWindow = new MainWindow(stage);
        mainWindow.show();
    }

    @Test
    public void testAccessibility() {
        // Get the scene
        Scene scene = mainWindow.getRootLayout().getScene();

        // Run accessibility tests
        List<AXE.Result> violations = AXE.scan(scene);

        // Assert no violations
        assertTrue(violations.isEmpty(), "Accessibility violations found: " + violations);
    }
}
```

### Step 3: Implement Accessibility Features

Implement these accessibility features in your text editor:

1. **Keyboard navigation**: Ensure all features can be accessed using the keyboard
2. **Screen reader support**: Add proper labels and descriptions for UI elements
3. **High contrast mode**: Provide a high contrast theme option
4. **Font size adjustment**: Allow users to increase or decrease font size
5. **Color blindness considerations**: Don't rely solely on color to convey information

## Defensive Programming

Defensive programming is a practice where you design your code to handle unexpected inputs, errors, and edge cases gracefully. This approach is crucial for building a robust text editor that can withstand user errors and unexpected situations.

### Key Principles of Defensive Programming

1. **Never trust input**: Validate all inputs, whether from users, files, or other parts of your application
2. **Fail early and explicitly**: Detect errors as soon as possible and provide clear error messages
3. **Use assertions and preconditions**: Verify assumptions about your code's state
4. **Handle all exceptions**: Don't let exceptions propagate unhandled
5. **Design by contract**: Clearly define what each method expects and guarantees

### Implementing Defensive Programming in Your Text Editor

#### Input Validation

Always validate inputs before processing them:

```java
public void insertText(int position, String text) {
    // Validate position
    if (position < 0 || position > textBuffer.length()) {
        throw new IllegalArgumentException("Invalid position: " + position);
    }

    // Validate text
    if (text == null) {
        throw new NullPointerException("Text cannot be null");
    }

    // Proceed with insertion
    InsertTextCommand command = new InsertTextCommand(this, position, text);
    commandManager.executeCommand(command);
}
```

#### Null Checking

Always check for null references:

```java
public void setFile(File file) {
    // Null check
    if (file != null && !file.exists()) {
        throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
    }

    this.file = file;
    notifyFileChanged();
}
```

#### Boundary Checking

Always check array and string boundaries:

```java
public String getText(int start, int end) {
    // Boundary checks
    if (start < 0) {
        start = 0;
    }

    if (end > buffer.length()) {
        end = buffer.length();
    }

    if (start > end) {
        throw new IllegalArgumentException("Start position cannot be greater than end position");
    }

    return buffer.substring(start, end);
}
```

#### Immutable Objects

Use immutable objects where possible:

```java
public class FindResult {
    private final String searchText;
    private final boolean matchCase;

    public FindResult(String searchText, boolean matchCase) {
        this.searchText = searchText != null ? searchText : "";
        this.matchCase = matchCase;
    }

    public String getSearchText() {
        return searchText;
    }

    public boolean isMatchCase() {
        return matchCase;
    }
}
```

#### Defensive Copying

Make defensive copies of mutable objects:

```java
public List<String> getRecentFiles() {
    // Return a defensive copy
    return new ArrayList<>(recentFiles);
}
```

#### Error Handling Strategy

Develop a consistent error handling strategy:

```java
public void handleError(Exception e, String operation) {
    // Log the error
    logger.log(Level.SEVERE, "Error during " + operation, e);

    // Determine error type and create appropriate message
    String errorMessage;
    if (e instanceof IOException) {
        errorMessage = "File operation failed: " + e.getMessage();
    } else if (e instanceof SecurityException) {
        errorMessage = "Security violation: " + e.getMessage();
    } else {
        errorMessage = "An unexpected error occurred: " + e.getMessage();
    }

    // Show error to user
    showErrorDialog("Error", operation + " failed", errorMessage);
}
```

## Debugging Common Issues

Here are some common issues you might encounter when developing your text editor and how to debug them:

### Issue 1: Text Not Updating

**Symptoms**: Changes to the document model are not reflected in the UI.

**Debugging Steps**:
1. Check that you've registered the document listener correctly
2. Verify that the `onContentChanged` method is being called
3. Add logging to track the flow of data from model to view
4. Check for any exceptions in the console

**Defensive Solution**:
```java
// Add defensive programming to DocumentController.java
@Override
public void onContentChanged(Document document) {
    // Defensive null check
    if (document == null) {
        logger.warning("onContentChanged called with null document");
        return;
    }

    logger.info("Document content changed: " + (document.getText().length() > 50 ?
                document.getText().substring(0, 50) + "..." : document.getText()));

    try {
        updateTextAreaContent();
        updateStatusBar();
    } catch (Exception e) {
        logger.severe("Error updating UI after content change: " + e.getMessage());
        e.printStackTrace();
    }
}
```

### Issue 2: Undo/Redo Not Working

**Symptoms**: Undo or redo operations don't restore the expected state.

**Debugging Steps**:
1. Check that commands are being added to the command stack
2. Verify that the `execute` and `undo` methods work correctly
3. Add logging to track command execution and undoing
4. Test each command type individually

**Defensive Solution**:
```java
// Add defensive programming to CommandManager.java
public void executeCommand(Command command) {
    // Defensive null check
    if (command == null) {
        logger.warning("Attempted to execute null command");
        return;
    }

    logger.info("Executing command: " + command.getDescription());

    try {
        command.execute();
        undoStack.push(command);
        redoStack.clear();

        // Limit the undo stack size
        if (undoStack.size() > maxUndoLevels) {
            undoStack.remove(0);
        }
    } catch (Exception e) {
        logger.severe("Error executing command: " + e.getMessage());
        // Don't add failed commands to the stack
        throw new RuntimeException("Command execution failed", e);
    }
}

public void undo() {
    if (undoStack.isEmpty()) {
        logger.info("Undo requested but stack is empty");
        return;
    }

    try {
        Command command = undoStack.pop();
        logger.info("Undoing command: " + command.getDescription());
        command.undo();
        redoStack.push(command);
    } catch (Exception e) {
        logger.severe("Error undoing command: " + e.getMessage());
        // Restore consistency by clearing both stacks
        undoStack.clear();
        redoStack.clear();
        throw new RuntimeException("Undo operation failed", e);
    }
}
```

### Issue 3: File Operations Failing

**Symptoms**: Files don't open or save correctly.

**Debugging Steps**:
1. Check file permissions
2. Verify file paths
3. Add try-catch blocks to catch and log specific exceptions
4. Test with different file types and sizes

**Defensive Solution**:
```java
// Add defensive programming to FileController.java
public void openFile(File file) {
    // Defensive validation
    if (file == null) {
        logger.warning("Attempted to open null file");
        return;
    }

    if (!file.exists()) {
        logger.warning("Attempted to open non-existent file: " + file.getAbsolutePath());
        showErrorDialog("File Not Found", "The file does not exist",
                      "The file at " + file.getAbsolutePath() + " could not be found.");
        return;
    }

    if (!file.canRead()) {
        logger.warning("No read permission for file: " + file.getAbsolutePath());
        showErrorDialog("Permission Denied", "Cannot read file",
                      "You don't have permission to read the file at " + file.getAbsolutePath());
        return;
    }

    try {
        // Check file size before loading
        long fileSize = file.length();
        if (fileSize > MAX_FILE_SIZE) {
            logger.warning("File too large: " + fileSize + " bytes");
            showErrorDialog("File Too Large", "The file is too large to open",
                          "The maximum supported file size is " + MAX_FILE_SIZE / (1024*1024) + " MB.");
            return;
        }

        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        document.setText(content);
        document.setFile(file);
        document.resetModified();
        addRecentFile(file.getAbsolutePath());
        logger.info("Successfully opened file: " + file.getAbsolutePath());
    } catch (IOException e) {
        logger.severe("Error opening file: " + file.getAbsolutePath() + ", Error: " + e.getMessage());
        showErrorDialog("Error Opening File", "Could not open file: " + file.getName(), e.getMessage());
    } catch (OutOfMemoryError e) {
        logger.severe("Out of memory while opening file: " + file.getAbsolutePath());
        showErrorDialog("Out of Memory", "Not enough memory to open file",
                      "The file is too large to fit in memory. Try closing other applications.");
    }
}
```

### Issue 4: UI Freezing

**Symptoms**: The UI becomes unresponsive during certain operations.

**Debugging Steps**:
1. Identify which operations cause freezing
2. Move long-running operations to background threads
3. Use JavaFX's `Platform.runLater` for UI updates
4. Add progress indicators for long operations

**Defensive Solution**:
```java
// Move file loading to a background thread with defensive programming
public void openFile(File file) {
    // Validate file before starting the task
    if (file == null || !file.exists() || !file.canRead()) {
        handleInvalidFile(file);
        return;
    }

    // Show progress indicator
    ProgressIndicator progress = new ProgressIndicator();
    statusBar.getChildren().add(progress);

    Task<String> loadTask = new Task<>() {
        @Override
        protected String call() throws Exception {
            // Check file size
            if (file.length() > MAX_FILE_SIZE) {
                throw new IOException("File too large: " + file.length() + " bytes");
            }

            // Set task progress
            updateProgress(0, 100);

            // Read file with progress updates
            String content = readFileWithProgress(file);

            // Validate content (e.g., check for binary content)
            if (isBinaryContent(content)) {
                throw new IOException("File appears to be binary");
            }

            return content;
        }

        private String readFileWithProgress(File file) throws IOException {
            // Implementation with progress updates
            // ...
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        }
    };

    // Bind progress indicator to task progress
    progress.progressProperty().bind(loadTask.progressProperty());

    loadTask.setOnSucceeded(event -> {
        try {
            String content = loadTask.getValue();
            document.setText(content);
            document.setFile(file);
            document.resetModified();
            addRecentFile(file.getAbsolutePath());
            logger.info("Successfully loaded file: " + file.getAbsolutePath());
        } finally {
            // Always remove progress indicator
            statusBar.getChildren().remove(progress);
        }
    });

    loadTask.setOnFailed(event -> {
        try {
            Throwable exception = loadTask.getException();
            logger.severe("Error loading file: " + exception.getMessage());
            showErrorDialog("Error Opening File", "Could not open file: " + file.getName(),
                          exception.getMessage());
        } finally {
            // Always remove progress indicator
            statusBar.getChildren().remove(progress);
        }
    });

    // Start the task with a dedicated thread
    Thread thread = new Thread(loadTask);
    thread.setDaemon(true); // Don't prevent application shutdown
    thread.start();
}
```

## Continuous Integration

Setting up continuous integration (CI) helps ensure that your code remains functional as you make changes.

### Step 1: Create a GitHub Actions Workflow

If you're using GitHub, create a file `.github/workflows/build.yml`:

```yaml
name: Build and Test

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v2

    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'

    - name: Build with Maven
      run: mvn -B package --file pom.xml

    - name: Run tests
      run: mvn test
```

### Step 2: Add a Test Report

Update your `pom.xml` to generate test reports:

```xml
<build>
    <plugins>
        <!-- Other plugins -->

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M5</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                </includes>
            </configuration>
        </plugin>

        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.7</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Conclusion

Testing and debugging are essential parts of developing a robust text editor. By implementing comprehensive tests and effective debugging strategies, you can ensure that your text editor works correctly and provides a good user experience.

Remember these key points:
- Write unit tests for all core components
- Test the UI with automated tests
- Perform performance testing for large files
- Use logging and debugging tools to identify and fix issues
- Set up continuous integration to catch regressions

With these practices in place, you'll be able to develop a high-quality text editor that meets the needs of your users.

## Next Steps

Congratulations! You've completed all the guides for building your text editor. You now have a functional text editor with all the essential features. Here are some ideas for what to do next:

1. **Add advanced features**: Syntax highlighting, auto-completion, code folding
2. **Improve performance**: Optimize for large files
3. **Enhance the UI**: Add themes, customize fonts and colors
4. **Create a plugin system**: Allow users to extend the editor
5. **Share your project**: Publish your code on GitHub and get feedback

Happy coding!
