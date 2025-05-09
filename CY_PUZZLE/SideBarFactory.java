package CY_PUZZLE;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class SideBarFactory {

    public static VBox createSideBarPanel(Label pieceLabel, Label timerLabel) {
        VBox sideBarPanel = new VBox(10);
        sideBarPanel.setAlignment(Pos.TOP_CENTER);
        sideBarPanel.setPadding(new Insets(30));
        sideBarPanel.setPrefWidth(300);

        // Dégradé en fond
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#2980b9")),
            new Stop(1, Color.web("#2c3e50"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        sideBarPanel.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = LabelFactory.createLabel("CY-PUZZLE", 30);
        Button uploadButton = ButtonFactory.createButton("Télécharger une image", Color.web("#2ecc71"));
        Button startButton = ButtonFactory.createButton("Lancer la résolution", Color.web("#926871"));
        VBox statsPanel = StatsPanelFactory.createStatsPanel(pieceLabel, timerLabel);

        sideBarPanel.getChildren().addAll(titleLabel, uploadButton, startButton, statsPanel);
        return sideBarPanel;
    }
}
