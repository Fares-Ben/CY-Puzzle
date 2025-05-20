package CY_PUZZLE;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Accueil extends Application {

    private final int FRAME_WIDTH = 1200;
    private final int FRAME_HEIGHT = 800;
    private final int GRID_SIZE = 4;

    private Label pieceLabel, timerLabel;
    public static GridPane gridPane;

    // Nouveau champ pour afficher la liste des pièces
    public static TextArea piecesListArea;

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
        piecesListArea = new TextArea();
        piecesListArea.setEditable(false);
        piecesListArea.setWrapText(true);
        piecesListArea.setPrefHeight(150);  // taille raisonnable pour la liste
        piecesListArea.setStyle("-fx-font-family: monospace;");

        // VBox qui contiendra la grille + la liste des pièces
        VBox rightPane = new VBox(10, gridPane, piecesListArea);
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

    public static void main(String[] args) {
        launch(args);
    }
}
