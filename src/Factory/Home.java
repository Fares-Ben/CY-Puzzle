/**
 * Home initializes and displays the main window of the application.
 * It serves as the entry GUI container with layout and navigation.
 */
package Factory;

import java.awt.image.BufferedImage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX main GUI container. Displays the interface to load, solve, and show puzzles.
 */
public class Home extends Application {
    /** Stores the last assembled image of the puzzle. */
    public static BufferedImage derniereImageAssemblee = null;

    private final int FRAME_WIDTH = 1200;
    private final int FRAME_HEIGHT = 800;
    private final int GRID_SIZE = 4;

    private Label pieceLabel, timerLabel;
    /** GridPane that holds the GUI layout of puzzle visualization. */
    public static GridPane gridPane;

    /** Displays a list of the pieces currently loaded or placed. */
    public static TextArea piecesListArea;

    /** Displays the puzzle after assembly in the GUI. */
    public static ImageView fusionImageView;

    @Override
    public void start(Stage primaryStage) {
        pieceLabel = LabelFactory.createLabel("Pièces :", 18);
        timerLabel = LabelFactory.createLabel("Timer :", 18);

        VBox sideBarPanel = SideBarFactory.createSideBarPanel(pieceLabel, timerLabel);

        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: white;");
        gridPane.setPadding(new Insets(0));
        gridPane.setHgap(0);
        gridPane.setVgap(0);

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridPane.add(new Label(" "), j, i);
            }
        }

        // Création du TextArea pour la liste des pièces
        piecesListArea = new TextArea(); // Il faut aussi l'initialiser ici, sinon NullPointerException
        piecesListArea.setEditable(false);
        piecesListArea.setWrapText(true);
        piecesListArea.setPrefHeight(150);
        piecesListArea.setStyle("-fx-font-family: monospace;");

        fusionImageView = new ImageView();
        fusionImageView.setFitWidth(600); // largeur max, adapte à ta vue
        fusionImageView.setPreserveRatio(true);
        fusionImageView.setSmooth(true);
        fusionImageView.setStyle("-fx-border-color: gray; -fx-border-width: 1;"); // Optionnel

        VBox rightPane = new VBox(10, fusionImageView, gridPane, piecesListArea);
        rightPane.setPadding(new Insets(10));
        rightPane.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(rightPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #f0f0f0; -fx-padding: 0;");

        HBox root = new HBox();
        root.getChildren().addAll(sideBarPanel, scrollPane);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(root, FRAME_WIDTH, FRAME_HEIGHT);
        primaryStage.setTitle("CY-PUZZLE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method to launch the JavaFX application manually.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
