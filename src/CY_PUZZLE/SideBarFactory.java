package CY_PUZZLE;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class SideBarFactory {

    // Chemin du dossier sélectionné, accessible pour la résolution
    private static File selectedPuzzleDirectory = null;

    public static VBox createSideBarPanel(Label pieceLabel, Label timerLabel) {
        VBox sideBarPanel = new VBox(10);
        sideBarPanel.setAlignment(Pos.TOP_CENTER);
        sideBarPanel.setPadding(new Insets(30));
        sideBarPanel.setPrefWidth(300);

        // Fond dégradé
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#2980b9")),
            new Stop(1, Color.web("#2c3e50"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        sideBarPanel.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = LabelFactory.createLabel("CY-PUZZLE", 30);
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Label directoryLabel = LabelFactory.createLabel("Aucun dossier sélectionné", 15);
        directoryLabel.setWrapText(true);
        directoryLabel.setMaxWidth(Double.MAX_VALUE);

        // Bouton pour choisir un dossier
        Button uploadButton = ButtonFactory.createButton("Télécharger un dossier", Color.web("#2ecc71"));
        uploadButton.setMaxWidth(Double.MAX_VALUE);
        uploadButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choisir un dossier contenant les pièces du puzzle");
            Stage stage = (Stage) sideBarPanel.getScene().getWindow();
            File directory = directoryChooser.showDialog(stage);

            if (directory != null) {
                selectedPuzzleDirectory = directory;
                File[] nonPngFiles = directory.listFiles(file -> !file.isDirectory() && !file.getName().toLowerCase().endsWith(".png"));

            if (nonPngFiles != null && nonPngFiles.length > 0) {
                  directoryLabel.setText("⚠️ " + nonPngFiles.length + " fichier(s) non attendus détectés !");
                } else {
                     directoryLabel.setText("Dossier : " + directory.getName());
                }
            }
        });

        // Bouton pour lancer la résolution
        Button startButton = ButtonFactory.createButton("Lancer la résolution", Color.web("#926871"));
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.setOnAction(e -> {
            if (selectedPuzzleDirectory != null) {
                System.out.println("Dossier à résoudre : " + selectedPuzzleDirectory.getAbsolutePath());
                // Mettre ici le code pour lancer la résolution du puzzle !!!!
            } else {
                directoryLabel.setText("⚠️ Veuillez d'abord choisir un dossier !");
            }
        });

        VBox statsPanel = StatsPanelFactory.createStatsPanel(pieceLabel, timerLabel);

        sideBarPanel.getChildren().addAll(titleLabel, uploadButton, directoryLabel, startButton, statsPanel);
        return sideBarPanel;
    }

    public static File getSelectedPuzzleDirectory() {
        return selectedPuzzleDirectory;
    }
}
