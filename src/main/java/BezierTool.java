<<<<<<< HEAD
package PACKAGE_NAME;public class BezierTool {
}
=======
import com.sun.scenario.effect.Blend;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;


public class BezierTool extends Application {

    // modified from CS 349 sample code

    @Override
    public void start(Stage stage) throws Exception {
        // set font
        // Font font = new Font("Helvetica", 13);
        double menuHeight = 30;

        // create and initialize the Model to hold our counter
        Model model = new Model(stage, menuHeight);

        // Create menu items
        MenuBar menubar = new MenuBar();
        initMenuBar(menubar, model, menuHeight, stage);

        // create each view, and tell them about the model
        // the views will register themselves with the model
        CanvasView canvasView = new CanvasView(model);
        ToolbarView toolbarView = new ToolbarView(model);

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(menubar);      // top menu
        borderPane.setLeft(toolbarView);      // left-view toolbar
        borderPane.setCenter(canvasView);      // center-view toolbar

        // when close
        stage.setOnCloseRequest(event -> {
            if (!model.isSaved() && !model.isProjectEmpty()) {
                boolean answer = DialogBox.Display("Warning",
                        "Are you sure you want discard your changes and exit?");
                if (!answer) {
                    event.consume();    // window close event stops here
                }
            }
        });

        // setup the stage size and show the window
        stage.setResizable(true);
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setMaxWidth(1280);
        stage.setMaxHeight(800);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }

    private void initMenuBar(MenuBar menubar, Model model, double menuHeight, Stage stage) {
        menubar.setPrefHeight(menuHeight);
        menubar.setMaxHeight(menuHeight);
        menubar.setMinHeight(menuHeight);
        // initialized the menu bar (modified from sample code)
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem fileNew = new MenuItem("New");
        MenuItem fileSave = new MenuItem("Save");
        MenuItem fileLoad = new MenuItem("Load");
        MenuItem fileQuit = new MenuItem("Quit");
        fileMenu.getItems().addAll(fileNew, fileLoad, fileSave, fileQuit);
        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem helpAbout = new MenuItem("About");
        helpMenu.getItems().addAll(helpAbout);

        // Map accelerator keys to menu items
        fileNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        fileLoad.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        fileSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        fileQuit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

        // Put menus together
        menubar.getMenus().addAll(fileMenu, helpMenu);

        // Setup handlers
        fileNew.setOnAction(actionEvent -> {
            newFile(model, stage);
        });
        fileLoad.setOnAction(actionEvent -> {
            loadFile(model, stage);
        });
        fileSave.setOnAction(actionEvent -> {
            saveFile(model, stage);
        });

        fileQuit.setOnAction(actionEvent -> {
            if (!model.isSaved() && !model.isProjectEmpty()) {
                boolean answer = DialogBox.Display("Warning",
                        "Are you sure you want discard your changes and exit?");
                if (!answer) {
                    actionEvent.consume();    // window close event stops here
                    return;
                }
            }
            System.exit(0);
        });

        helpAbout.setOnAction(actionEvent -> {
            String about = readFile("readme.md");

            if (about != null) AlertBox.Display("About", about, 600, 450);
        });

        // click menu and stop the action in model
        menubar.addEventFilter(MouseEvent.MOUSE_CLICKED, model.stopMouseHandler);
    }

    private void newFile(Model model, Stage stage) {
        // if the project is not empty, prompt the user whether to discard the changes
        if (!model.isProjectEmpty() && !model.isSaved()) {
            String warning = "Warning";
            String warningMsg = "If you don't save, the changes will be discarded. \n" +
                    "Do you want to save the project before closing?";
            boolean answer = DialogBox.Display(warning, warningMsg);
            if (answer == true) {
                saveFile(model, stage);
            }
        }
        model.newProject();
    }

    private void loadFile(Model model, Stage stage) {
        // file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        fileChooser.setTitle("Load File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                newFile(model, stage);
                FileReader fileReader = new FileReader(file.getPath());
                BufferedReader reader = new BufferedReader(fileReader);
                model.loadProject(reader);
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(Model model, Stage stage) {
        // file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        fileChooser.setTitle("Save File");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file.getPath());
                BufferedWriter writer = new BufferedWriter(fileWriter);
                model.saveProject(writer);
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // copied from cs349 sample code : read_file
    private String readFile(String path) {
        try {
            // wrap FileReader in a BufferedReader for IO
            FileReader reader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String result = new String();
            // loop until EOF and print each line
            String line;
            while((line = bufferedReader.readLine()) != null) {
                result += line;
                result += "\n";
            }

            // close when complete
            bufferedReader.close();
            return result;
        } catch (Exception ex) {
            System.out.println("Error reading file: " + ex.toString());
        }
        return null;
    }

}
>>>>>>> d5eed7ed92c6d42e48bda6ae061e950156340942
