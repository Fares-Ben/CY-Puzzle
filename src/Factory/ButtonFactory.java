package Factory;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ButtonFactory {

    public static Button createButton(String text, Color color) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 16));
        button.setTextFill(Color.WHITE);
        button.setBackground(new Background(new BackgroundFill(color, new CornerRadii(10), null)));
        button.setMaxWidth(240);
        button.setMinHeight(50);
        button.setEffect(new DropShadow());
        button.setStyle("-fx-cursor: hand;");

        // Effet de survol
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(color.darker(), new CornerRadii(10), null)));
        });
        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(color, new CornerRadii(10), null)));
        });

        return button;
    }
}
