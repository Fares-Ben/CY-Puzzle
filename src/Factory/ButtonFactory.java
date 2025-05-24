/**
 * ButtonFactory provides styled buttons used in the GUI.
 * All buttons share consistent design defined in this factory.
 */
package Factory;

import javafx.scene.control.Button; // Import for JavaFX Button class
import javafx.scene.effect.DropShadow; // Import for adding shadow effects to buttons
import javafx.scene.layout.Background; // Import for setting button background
import javafx.scene.layout.BackgroundFill; // Import for filling the background with color
import javafx.scene.layout.CornerRadii; // Import for defining rounded corners
import javafx.scene.paint.Color; // Import for setting colors
import javafx.scene.text.Font; // Import for setting the font of the button text

/**
 * Factory class to create standardized buttons for the GUI.
 * This ensures all buttons in the application have a consistent style.
 */
public class ButtonFactory {

    /**
     * Creates a styled JavaFX button with the given text and background color.
     * 
     * @param text  the text displayed on the button
     * @param color the background color of the button
     * @return a styled Button instance
     */
    public static Button createButton(String text, Color color) {
        // Create a new Button instance with the specified text
        Button button = new Button(text);

        // Set the font of the button text to Arial with size 16
        button.setFont(Font.font("Arial", 16));

        // Set the text color to white
        button.setTextFill(Color.WHITE);

        // Set the background color of the button with rounded corners
        button.setBackground(new Background(new BackgroundFill(color, new CornerRadii(10), null)));

        // Set the maximum width of the button to 240 pixels
        button.setMaxWidth(240);

        // Set the minimum height of the button to 50 pixels
        button.setMinHeight(50);

        // Add a drop shadow effect to the button for a 3D look
        button.setEffect(new DropShadow());

        // Add a hand cursor style when hovering over the button
        button.setStyle("-fx-cursor: hand;");

        // Add hover effects to the button
        // When the mouse enters the button, darken the background color
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(color.darker(), new CornerRadii(10), null)));
        });

        // When the mouse exits the button, restore the original background color
        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(color, new CornerRadii(10), null)));
        });

        // Return the fully styled button
        return button;
    }
}
