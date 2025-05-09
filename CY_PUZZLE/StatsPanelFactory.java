package CY_PUZZLE;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class StatsPanelFactory {

    public static VBox createStatsPanel(Label pieceLabel, Label timerLabel) {
        VBox statsPanel = new VBox(10);
        statsPanel.setPadding(new Insets(15));
        statsPanel.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.1), new CornerRadii(5), Insets.EMPTY)));
        statsPanel.setBorder(new Border(new BorderStroke(Color.rgb(255, 255, 255, 0.2),
                BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1))));

        Label statsTitle = LabelFactory.createLabel("Informations", 20);
        statsPanel.getChildren().addAll(statsTitle, pieceLabel, timerLabel);

        return statsPanel;
    }
}
