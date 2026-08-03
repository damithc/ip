package duke;

import java.io.IOException;

import duke.ui.GuiUi;
import duke.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A JavaFX GUI for Damien using an FXML-defined view.
 */
public class Main extends Application {
    /**
     * Builds and displays the main application window.
     *
     * @param stage the primary JavaFX window
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            assert root != null : "The main window FXML must provide a root node.";
            Scene scene = new Scene(root);

            GuiUi guiUi = new GuiUi();
            Damien damien = new Damien(Damien.DEFAULT_DATA_FILE, guiUi);
            MainWindow controller = fxmlLoader.getController();
            assert controller != null : "The main window FXML must provide a controller.";
            controller.setDamien(damien, guiUi);

            stage.setTitle("Damien | Task Assistant");
            stage.setMinWidth(620);
            stage.setMinHeight(520);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the Damien GUI.", exception);
        }
    }
}
