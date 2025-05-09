package CY_PUZZLE;

import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    private static File selectedPuzzleDirectory = null;
    private static List<File> selectedPngFiles = List.of();

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

        // Bouton pour choisir un dossier avec parcour de sous dossiers
        Button uploadButton = ButtonFactory.createButton("Télécharger un dossier", Color.web("#2ecc71"));
        uploadButton.setMaxWidth(Double.MAX_VALUE);
        uploadButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choisir un dossier contenant les pièces du puzzle");
            Stage stage = (Stage) sideBarPanel.getScene().getWindow();
            File directory = directoryChooser.showDialog(stage);

            if (directory != null) {
                selectedPuzzleDirectory = directory;

                try (Stream<Path> paths = Files.walk(directory.toPath())) {
                    selectedPngFiles = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".png"))
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                } catch (IOException e) {
                    selectedPngFiles = List.of();
                    e.printStackTrace();
                }

                int pngCount = selectedPngFiles.size();
                pieceLabel.setText("Pièces : " + pngCount);

                // Vérifie s'il y a d'autres fichiers que des .png
                long nonPngCount = 0;
                try (Stream<Path> allFiles = Files.walk(directory.toPath())) {
                    nonPngCount = allFiles
                        .filter(Files::isRegularFile)
                        .filter(p -> !p.toString().toLowerCase().endsWith(".png"))
                        .count();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (nonPngCount > 0) {
                    directoryLabel.setText("Attention : " + nonPngCount + " fichier(s) non attendus. " +
                                           pngCount + " fichier(s) .png détectés.");
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
                System.out.println("Fichiers trouvés :");
                selectedPngFiles.forEach(f -> System.out.println(" - " + f.getAbsolutePath()));

                // ici utiliser selectedPngFiles pour l'algorithme de résolution

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

    public static List<File> getSelectedPngFiles() {
        return selectedPngFiles;
    }
}
