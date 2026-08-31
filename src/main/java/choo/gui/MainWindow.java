package choo.gui;

import choo.Choo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Controls the main CHOO chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private static final int AVATAR_SIZE = 64;
    private static final Image USER_IMAGE = createAvatar(Color.web("#4A90E2"));
    private static final Image CHOO_IMAGE = createAvatar(Color.web("#50A684"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Choo choo;

    /**
     * Configures automatic scrolling after FXML fields are injected.
     */
    @FXML
    public void initialize() {
        this.scrollPane.vvalueProperty().bind(this.dialogContainer.heightProperty());
    }

    /**
     * Supplies the command engine used to answer messages.
     *
     * @param choo CHOO command engine.
     */
    public void setChoo(Choo choo) {
        this.choo = choo;
        this.dialogContainer.getChildren().add(
                DialogBox.getChooDialog("Hello! I'm CHOO. What can I do for you?", CHOO_IMAGE));
    }

    /**
     * Sends the current text-field content and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = this.userInput.getText().trim();
        if (input.isEmpty() || this.choo == null) {
            return;
        }

        String response = this.choo.getResponse(input);
        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, USER_IMAGE),
                DialogBox.getChooDialog(response, CHOO_IMAGE));
        this.userInput.clear();
        if (input.equals("bye")) {
            Platform.exit();
        }
    }

    private static Image createAvatar(Color color) {
        WritableImage image = new WritableImage(AVATAR_SIZE, AVATAR_SIZE);
        PixelWriter pixels = image.getPixelWriter();
        double center = (AVATAR_SIZE - 1) / 2.0;
        double radiusSquared = center * center;
        for (int y = 0; y < AVATAR_SIZE; y++) {
            for (int x = 0; x < AVATAR_SIZE; x++) {
                double horizontalDistance = x - center;
                double verticalDistance = y - center;
                boolean isInsideCircle = horizontalDistance * horizontalDistance
                        + verticalDistance * verticalDistance <= radiusSquared;
                pixels.setColor(x, y, isInsideCircle ? color : Color.TRANSPARENT);
            }
        }
        return image;
    }
}
