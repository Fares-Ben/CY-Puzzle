package CY_PUZZLE;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LabelFactory {

    public static Label createLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
        label.setTextFill(Color.WHITE);
        return label;
    }
}
