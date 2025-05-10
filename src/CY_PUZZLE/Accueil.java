package CY_PUZZLE;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Accueil extends Application {

    private final int FRAME_WIDTH = 1200;
    private final int FRAME_HEIGHT = 800;
    private final int GRID_SIZE = 4;

    private Label pieceLabel, timerLabel;
    public static GridPane gridPane;

    @Override
    public void start(Stage primaryStage) {
        pieceLabel = LabelFactory.createLabel("Pièces :", 18);
        timerLabel = LabelFactory.createLabel("Timer :", 18);

        VBox sideBarPanel = SideBarFactory.createSideBarPanel(pieceLabel, timerLabel);

        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: white;");
        gridPane.setPadding(new Insets(0)); // Supprime les marges pour maximiser l'espace
        gridPane.setHgap(0); // Supprime les espaces horizontaux entre les cellules
        gridPane.setVgap(0); // Supprime les espaces verticaux entre les cellules
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridPane.add(new Label(" "), j, i); // case vide temporaire
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true); // Permet au ScrollPane de s'adapter à la hauteur
        scrollPane.setStyle("-fx-background: #f0f0f0; -fx-padding: 0;"); // Supprime les paddings pour maximiser l'espace

        HBox root = new HBox();
        root.getChildren().addAll(sideBarPanel, scrollPane);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(root, FRAME_WIDTH, FRAME_HEIGHT);
        primaryStage.setTitle("CY-PUZZLE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
