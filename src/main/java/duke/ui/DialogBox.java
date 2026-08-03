package duke.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents a conversation message with an avatar and message text.
 */
public class DialogBox extends HBox {
    /** Style class used for responses containing numbered task rows. */
    private static final String TASK_LIST_DIALOG_STYLE = "task-list-dialog";

    /** The label containing the message text. */
    @FXML
    private TextFlow dialog;

    /** The avatar shown beside the message text. */
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box by loading its FXML view.
     *
     * @param text the message text
     * @param image the avatar to display
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the dialog box view.", exception);
        }

        getStyleClass().add("dialog-box");
        Text messageText = new Text(text);
        messageText.getStyleClass().add("message-text");
        dialog.getChildren().setAll(messageText);
        displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box so that the avatar appears on the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for user input.
     *
     * @param text the user's input
     * @param image the user's avatar
     * @return a right-aligned user dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialog box for a Damien response.
     *
     * @param text Damien's response
     * @param image Damien's avatar
     * @return a left-aligned Damien dialog box
     */
    public static DialogBox getDamienDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("damien-dialog");
        if (containsTaskListRows(text)) {
            dialogBox.getStyleClass().add(TASK_LIST_DIALOG_STYLE);
        }
        return dialogBox;
    }

    /**
     * Checks whether a response contains a numbered task row.
     *
     * @param text the response text to inspect
     * @return true if the response contains a formatted task row
     */
    static boolean containsTaskListRows(String text) {
        return text.lines().anyMatch(line -> line.matches("\\d+\\.\\[[TDE]\\]\\[[ X]\\].*"));
    }
}
