/**
 * LabelFactory creates styled labels used throughout the GUI.
 * Labels can be customized with text, size, and alignment.
 */
package Factory;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Utility factory class to create pre-styled JavaFX Labels.
 */
public class LabelFactory {
    /**
     * Creates a JavaFX label with the given text and font size.
     * 
     * @param text     the text to display
     * @param fontSize the font size
     * @return a styled Label
     */
    public static Label createLabel(String text, int fontSize) {

        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
        label.setTextFill(Color.WHITE);
        return label;
    }
}
