package CY_PUZZLE;

import java.nio.file.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;

import java.io.File;

import static CY_PUZZLE.Accueil.gridPane;

public class SideBarFactory {

    private static final double SIDEBAR_WIDTH = 300; // Largeur fixe de la barre latérale
    private static File selectedPuzzleDirectory = null;
    private static List<File> selectedPngFiles = List.of();

    public static VBox createSideBarPanel(Label pieceLabel, Label timerLabel) {
        VBox sideBarPanel = new VBox(10);
        sideBarPanel.setAlignment(Pos.TOP_CENTER);
        sideBarPanel.setPadding(new Insets(30));
        sideBarPanel.setPrefWidth(SIDEBAR_WIDTH);
        sideBarPanel.setMinWidth(SIDEBAR_WIDTH);  // Forcer la largeur minimale
        sideBarPanel.setMaxWidth(SIDEBAR_WIDTH);  // Forcer la largeur maximale

        // Fond dégradé
        Stop[] stops = new Stop[]{
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
                    directoryLabel.setText("Attention : " + nonPngCount + " fichier(s) non attendus. "
                            + pngCount + " fichier(s) .png détectés.");
                } else {
                    directoryLabel.setText("Dossier : " + directory.getName());
                }

                // Affichage immédiat des images
                List<PuzzlePiece> puzzlePieces = new ArrayList<>();
                for (File file : selectedPngFiles) {
                    try {
                        puzzlePieces.add(new PuzzlePiece(file));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if (!puzzlePieces.isEmpty()) {
                    gridPane.getChildren().clear();
                    
                    // Configuration du GridPane principal
                    gridPane.setPadding(new Insets(10));
                    gridPane.setHgap(5);
                    gridPane.setVgap(5);
                    
                    // Calcul du nombre de colonnes optimal
                    int numCols = (int) Math.ceil(Math.sqrt(puzzlePieces.size()));// nombre de colonne en fonction de la racine carre 
                    int numRows = (int) Math.ceil(puzzlePieces.size() / (double) numCols);
                    
                    int index = 0;
                    for (int row = 0; row < numRows && index < puzzlePieces.size(); row++) {
                        for (int col = 0; col < numCols && index < puzzlePieces.size(); col++) {
                            try {
                                PuzzlePiece piece = puzzlePieces.get(index++);
                                Image image = convertToFxImage(piece.getImage());
                                ImageView imageView = new ImageView(image);
                                
                                imageView.setFitWidth(80);
                                imageView.setFitHeight(80);
                                imageView.setPreserveRatio(true);
                                
                                StackPane imageContainer = new StackPane(imageView);
                                imageContainer.setPadding(new Insets(2));
                                
                                gridPane.add(imageContainer, col, row);
                            } catch (Exception t) {
                                t.printStackTrace();
                            }
                        }
                    }
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

                // Liste de pièce ==================
                List<PuzzlePiece> puzzlePieces = new ArrayList<>();
                for (File file : selectedPngFiles) {
                    try {
                        puzzlePieces.add(new PuzzlePiece(file));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                
                //Apelle la fonction qui résoud a partir de la liste de pièce : 
                if (puzzlePieces.isEmpty()) {
                    System.err.println("❌ Aucune pièce PNG valide trouvée !");
                    return;
                }
                
                PuzzleSolver solver = new PuzzleSolver(puzzlePieces);

                PuzzlePiece[][] tab = solver.solve();
                


                gridPane.getChildren().clear();

                int rows = solver.getRows();
                int cols = solver.getCols();

                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        PuzzlePiece piece = tab[r][c];

                        if (piece == null) continue;

                        try {
                            Image image = convertToFxImage(piece.getImage());
                            ImageView imageView = new ImageView(image);

                            imageView.setFitWidth(50);     // à adapter
                            imageView.setFitHeight(50);    // à adapter
                            imageView.setPreserveRatio(true);

                            gridPane.add(imageView, c, r);
                        } catch (Exception t) {
                            // Faudra voir on veut recup quoi comme erreur mais bon 
                        }
                    }
                }


            } else {
                directoryLabel.setText("⚠️ Veuillez d'abord choisir un dossier !");
            }
        });

        VBox statsPanel = StatsPanelFactory.createStatsPanel(pieceLabel, timerLabel);

        sideBarPanel.getChildren().addAll(titleLabel, uploadButton, directoryLabel, startButton, statsPanel);

        return sideBarPanel;
    }

    public static Image convertToFxImage(BufferedImage bf) {
        return SwingFXUtils.toFXImage(bf, null);
    }

    public static File getSelectedPuzzleDirectory() {
        return selectedPuzzleDirectory;
    }

    public static List<File> getSelectedPngFiles() {
        return selectedPngFiles;
    }
}
