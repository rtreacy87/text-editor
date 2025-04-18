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

import static org.junit.jupiter.api.Assertions.*;

public class DocumentIntegrationTest {

    private Document document;
    private EditorTextArea textArea;
    private StatusBar statusBar;
    private DocumentController documentController;

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
    }

    @Test
    public void testDocumentToViewSync() {
        // Change the document and verify the view updates
        document.setText("Hello, world!");

        // Wait for JavaFX thread to process changes
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Hello, world!", textArea.getText());
    }

    @Test
    public void testViewToDocumentSync() {
        // Change the view and verify the document updates
        textArea.setText("Hello from view!");

        // Wait for changes to propagate
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Hello from view!", document.getText());
    }

    @Test
    public void testCursorPositionSync() {
        document.setText("Hello, world!");

        // Wait for JavaFX thread to process changes
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Set cursor position in document
        document.getCursor().setPosition(7);

        // Wait for changes to propagate
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(7, textArea.getCaretPosition());
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

### Step 3: Implement Security Best Practices

Implement these security best practices in your text editor:

1. **Validate file paths**: Prevent path traversal attacks by validating file paths
2. **Handle sensitive data securely**: Don't store passwords or sensitive data in plain text
3. **Use secure file operations**: Use atomic file operations to prevent data loss
4. **Implement proper error handling**: Don't expose sensitive information in error messages
5. **Limit file access**: Only access files that the user has permission to access

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

## Debugging Common Issues

Here are some common issues you might encounter when developing your text editor and how to debug them:

### Issue 1: Text Not Updating

**Symptoms**: Changes to the document model are not reflected in the UI.

**Debugging Steps**:
1. Check that you've registered the document listener correctly
2. Verify that the `onContentChanged` method is being called
3. Add logging to track the flow of data from model to view
4. Check for any exceptions in the console

**Solution Example**:
```java
// Add logging to DocumentController.java
@Override
public void onContentChanged(Document document) {
    System.out.println("Document content changed: " + document.getText());
    updateTextAreaContent();
    updateStatusBar();
}
```

### Issue 2: Undo/Redo Not Working

**Symptoms**: Undo or redo operations don't restore the expected state.

**Debugging Steps**:
1. Check that commands are being added to the command stack
2. Verify that the `execute` and `undo` methods work correctly
3. Add logging to track command execution and undoing
4. Test each command type individually

**Solution Example**:
```java
// Add logging to CommandManager.java
public void executeCommand(Command command) {
    System.out.println("Executing command: " + command.getDescription());
    command.execute();
    undoStack.push(command);
    redoStack.clear();

    // Limit the undo stack size
    if (undoStack.size() > maxUndoLevels) {
        undoStack.remove(0);
    }
}

public void undo() {
    if (!undoStack.isEmpty()) {
        Command command = undoStack.pop();
        System.out.println("Undoing command: " + command.getDescription());
        command.undo();
        redoStack.push(command);
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

**Solution Example**:
```java
// Add more detailed error handling to FileController.java
public void openFile(File file) {
    try {
        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        document.setText(content);
        document.setFile(file);
        document.resetModified();
        addRecentFile(file.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error opening file: " + file.getAbsolutePath());
        System.err.println("Exception: " + e.getMessage());
        e.printStackTrace();
        showErrorDialog("Error Opening File", "Could not open file: " + file.getName(), e.getMessage());
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

**Solution Example**:
```java
// Move file loading to a background thread
public void openFile(File file) {
    Task<String> loadTask = new Task<>() {
        @Override
        protected String call() throws Exception {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        }
    };

    loadTask.setOnSucceeded(event -> {
        String content = loadTask.getValue();
        document.setText(content);
        document.setFile(file);
        document.resetModified();
        addRecentFile(file.getAbsolutePath());
    });

    loadTask.setOnFailed(event -> {
        Throwable exception = loadTask.getException();
        showErrorDialog("Error Opening File", "Could not open file: " + file.getName(), exception.getMessage());
    });

    new Thread(loadTask).start();
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
