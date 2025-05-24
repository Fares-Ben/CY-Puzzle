/**
 * StatsPanelFactory generates a panel showing puzzle statistics,
 * including resolution time and number of pieces placed.
 */
package Factory;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Factory class to generate the statistics panel in the GUI.
 */
public class StatsPanelFactory {

    /**
     * Creates a VBox containing the puzzle stats (e.g., time and pieces).
     *
     * @param pieceLabel label for number of pieces placed
     * @param timerLabel label for elapsed time
     * @return VBox with statistical display
     */
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
