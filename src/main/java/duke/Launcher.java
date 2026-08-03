package duke;

import javafx.application.Application;

/**
 * Launches the JavaFX application to work around a JavaFX classpath issue.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
