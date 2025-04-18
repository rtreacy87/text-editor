# Project Setup and Architecture

This guide will walk you through setting up your development environment and understanding the architecture of our text editor project. By the end of this guide, you'll have a solid foundation for building your text editor.

## Table of Contents
- [Setting Up Your Development Environment](#setting-up-your-development-environment)
- [Understanding the MVC Architecture](#understanding-the-mvc-architecture)
- [Project Structure](#project-structure)
- [Choosing a Technology Stack](#choosing-a-technology-stack)
- [Creating the Project](#creating-the-project)

## Setting Up Your Development Environment

Before we start coding, we need to set up our development environment. This involves installing the necessary software and tools. For this tutorial, we'll use Java with JavaFX for our text editor and Visual Studio Code (VSCode) as our IDE.

### Setting Up VSCode for Different Operating Systems

#### macOS Setup

1. **Install VSCode**:
   - Download VSCode from the [official website](https://code.visualstudio.com/download)
   - Drag the downloaded app to your Applications folder

2. **Install Java Development Kit (JDK)**:
   ```bash
   # Using Homebrew
   brew install openjdk@17

   # Set JAVA_HOME (add to your .zshrc or .bash_profile)
   echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
   source ~/.zshrc
   ```

3. **Install VSCode Extensions**:
   - Open VSCode
   - Go to Extensions (Cmd+Shift+X)
   - Install the following extensions:
     - Extension Pack for Java
     - Maven for Java
     - Test Runner for Java

4. **Verify Installation**:
   ```bash
   java --version
   mvn --version
   ```

#### Ubuntu Linux Setup

1. **Install VSCode**:
   ```bash
   # Download and install the .deb package
   wget -O vscode.deb 'https://code.visualstudio.com/sha/download?build=stable&os=linux-deb-x64'
   sudo apt install ./vscode.deb
   ```

2. **Install Java Development Kit (JDK)**:
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk

   # Set JAVA_HOME (add to your .bashrc)
   echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
   source ~/.bashrc
   ```

3. **Install Maven**:
   ```bash
   sudo apt install maven
   ```

4. **Install VSCode Extensions**:
   - Open VSCode
   - Go to Extensions (Ctrl+Shift+X)
   - Install the following extensions:
     - Extension Pack for Java
     - Maven for Java
     - Test Runner for Java

5. **Verify Installation**:
   ```bash
   java --version
   mvn --version
   ```

#### Windows with WSL (Ubuntu) Setup

1. **Install WSL**:
   - Open PowerShell as Administrator and run:
   ```powershell
   wsl --install
   ```
   - Restart your computer
   - Complete the Ubuntu setup when prompted

2. **Install VSCode on Windows**:
   - Download and install VSCode from the [official website](https://code.visualstudio.com/download)

3. **Install WSL Extension in VSCode**:
   - Open VSCode
   - Go to Extensions (Ctrl+Shift+X)
   - Search for and install "Remote - WSL"

4. **Connect VSCode to WSL**:
   - Click on the green button in the bottom-left corner of VSCode
   - Select "Remote-WSL: New Window"
   - VSCode will reopen connected to your WSL environment

5. **Install Java and Maven in WSL**:
   ```bash
   # Inside WSL terminal
   sudo apt update
   sudo apt install openjdk-17-jdk maven

   # Set JAVA_HOME
   echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
   source ~/.bashrc
   ```

6. **Install Java Extensions in WSL VSCode**:
   - In the VSCode window connected to WSL
   - Go to Extensions (Ctrl+Shift+X)
   - Install the following extensions:
     - Extension Pack for Java
     - Maven for Java
     - Test Runner for Java

7. **Verify Installation**:
   ```bash
   java --version
   mvn --version
   ```

### Alternative Development Environments

If you prefer a different IDE or programming language, here are some alternatives:

- **Other Java IDEs**: IntelliJ IDEA, Eclipse, or NetBeans
- **Python**: Install Python and a GUI library like Tkinter, PyQt, or wxPython
- **C++**: Install a C++ compiler and the Qt framework
- **JavaScript**: Install Node.js and Electron for desktop applications

## Understanding the MVC Architecture

Our text editor will follow the Model-View-Controller (MVC) pattern, which is a software design pattern commonly used for developing user interfaces. It divides the application into three interconnected components:

### Model

The Model represents the data and the business logic of the application. In our text editor, the Model will handle:

- The text content of the document
- File operations (open, save)
- Document state (modified, saved)

### View

The View is responsible for displaying the data to the user. In our text editor, the View will include:

- The main window
- Text area
- Menu bar
- Status bar
- Dialog boxes

### Controller

The Controller acts as an intermediary between the Model and the View. It:

- Processes user input from the View
- Updates the Model based on user actions
- Updates the View when the Model changes

### Benefits of MVC

- **Separation of concerns**: Each component has a specific responsibility
- **Code reusability**: Components can be reused in different parts of the application
- **Easier maintenance**: Changes to one component don't affect others
- **Parallel development**: Different team members can work on different components

## Project Structure

Let's organize our project with a clear directory structure:

```
text-editor/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── texteditor/
│   │   │   │   │   ├── TextEditorApp.java (Main application class)
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Document.java
│   │   │   │   │   │   ├── TextBuffer.java
│   │   │   │   │   ├── view/
│   │   │   │   │   │   ├── MainWindow.java
│   │   │   │   │   │   ├── TextArea.java
│   │   │   │   │   │   ├── MenuBar.java
│   │   │   │   │   │   ├── StatusBar.java
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── DocumentController.java
│   │   │   │   │   │   ├── FileController.java
│   │   │   │   │   │   ├── EditController.java
│   │   │   │   │   ├── util/
│   │   │   │   │   │   ├── FileUtils.java
│   │   │   │   │   │   ├── TextUtils.java
│   │   ├── resources/
│   │   │   ├── css/
│   │   │   │   ├── style.css
│   │   │   ├── images/
│   │   │   ├── fxml/
│   ├── test/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── texteditor/
│   │   │   │   │   ├── model/
│   │   │   │   │   ├── view/
│   │   │   │   │   ├── controller/
├── pom.xml (or build.gradle)
├── README.md
```

## Choosing a Technology Stack

For our text editor, we need to choose appropriate technologies. Here are some options with their pros and cons:

### Java + JavaFX

**Pros**:
- Cross-platform compatibility
- Rich UI components
- Strong typing and OOP features
- Good performance
- Extensive documentation

**Cons**:
- More verbose than some alternatives
- Requires JVM installation

### Python + Tkinter/PyQt/wxPython

**Pros**:
- Easy to learn and use
- Rapid development
- Cross-platform
- Large community

**Cons**:
- Slower performance for complex UIs
- Package distribution can be challenging

### C++ + Qt

**Pros**:
- Excellent performance
- Comprehensive framework
- Cross-platform
- Native look and feel

**Cons**:
- Steeper learning curve
- More complex memory management

### JavaScript + Electron

**Pros**:
- Web technologies (HTML, CSS, JavaScript)
- Large ecosystem of libraries
- Cross-platform
- Modern UI capabilities

**Cons**:
- Higher memory usage
- Larger application size

For our tutorial, we'll use **Java + JavaFX** for its balance of performance, cross-platform compatibility, and object-oriented design.

## Creating the Project

Now let's create our project structure:

### Using Maven

1. Create a new Maven project in your IDE or use the command line:

```bash
mvn archetype:generate -DgroupId=com.texteditor -DartifactId=text-editor -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

2. Add JavaFX dependencies to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.1</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>17.0.1</version>
    </dependency>
    <!-- Add more dependencies as needed -->
</dependencies>
```

3. Configure the JavaFX Maven plugin:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <mainClass>com.texteditor.TextEditorApp</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Creating the Main Application Class

Create a new file `TextEditorApp.java` in the `src/main/java/com/texteditor` directory:

```java
package com.texteditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TextEditorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // This is just a placeholder. We'll build the actual UI later.
        StackPane root = new StackPane();
        root.getChildren().add(new Label("Hello, Text Editor!"));

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

### Running the Application

To run the application:

1. In IntelliJ IDEA: Right-click on `TextEditorApp.java` and select "Run"
2. In Eclipse: Right-click on `TextEditorApp.java` and select "Run As > Java Application"
3. Using Maven: Run `mvn javafx:run`

You should see a window with the text "Hello, Text Editor!". This confirms that your development environment is set up correctly.

## Next Steps

Now that you have set up your development environment and understand the architecture of our text editor, you're ready to move on to implementing the user interface. Continue to the next guide: [User Interface Implementation](02-user-interface-implementation.md).

## Common Issues and Troubleshooting

### JavaFX Not Found

If you encounter errors related to JavaFX not being found:

1. Make sure you've added the JavaFX dependencies to your `pom.xml` or `build.gradle`
2. Check that your IDE is configured to use the correct JDK
3. Try adding the JavaFX modules to your VM options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

### Maven Build Fails

If your Maven build fails:

1. Check that you have Maven installed correctly
2. Verify that your `pom.xml` is correctly formatted
3. Try running `mvn clean install` to rebuild the project

### IDE Configuration Issues

If you're having problems with your IDE:

1. Make sure you've installed the latest version
2. Check for any required plugins (e.g., JavaFX plugin for Eclipse)
3. Restart the IDE after making configuration changes

## Resources for Further Learning

- [JavaFX Documentation](https://openjfx.io/javadoc/17/)
- [Maven Documentation](https://maven.apache.org/guides/index.html)
- [Model-View-Controller Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)
- [Java Programming Tutorial](https://docs.oracle.com/javase/tutorial/)
