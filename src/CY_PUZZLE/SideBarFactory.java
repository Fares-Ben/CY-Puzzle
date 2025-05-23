package CY_PUZZLE;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import ttt.PuzzleSolver;
import ttt.PieceSave;
import ttt.PuzzleAnalyzer;
import ttt.PuzzleImageViewer;
import javafx.stage.FileChooser;
import javafx.scene.control.ProgressBar;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.stage.Modality;

import CY_PUZZLE.Home;


public class SideBarFactory {

    private static final double SIDEBAR_WIDTH = 300; // Largeur fixe de la barre latérale
    private static File selectedPuzzleDirectory = null;
    private static List<File> selectedPngFiles = List.of();
    public static ImageView fusionImageView;

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

        // Bouton pour choisir un dossier 
        Button addFolderButton = ButtonFactory.createButton("Ajouter un dossier", Color.web("#3498db"));
        addFolderButton.setMaxWidth(Double.MAX_VALUE);
        addFolderButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choisir un dossier de puzzle");
            File selectedDir = directoryChooser.showDialog(null);
            if (selectedDir != null && selectedDir.isDirectory()) {
                selectedPuzzleDirectory = selectedDir;
                directoryLabel.setText("Dossier sélectionné : " + selectedDir.getName());
                timerLabel.setText("Timer ⏱ : 0.00 secondes");

                // Afficher les pièces dans la grille
                Home.gridPane.getChildren().clear();
                try (Stream<Path> files = Files.list(selectedDir.toPath())) {
                    selectedPngFiles = files
                        .filter(p -> p.toString().toLowerCase().endsWith(".png"))
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                    
                    // Afficher chaque image dans la grille
                    int col = 0;
                    int row = 0;
                    int maxCol = 5; // Nombre de colonnes dans la grille

                    for (File imgFile : selectedPngFiles) {
                        Image fxImage = new Image(imgFile.toURI().toString());
                        ImageView imageView = new ImageView(fxImage);
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);
                        imageView.setPreserveRatio(true);

                        Home.gridPane.add(imageView, col, row);
                        
                        col++;
                        if (col >= maxCol) {
                            col = 0;
                            row++;
                        }
                    }

                    // Mettre à jour le label des pièces
                    pieceLabel.setText("Nombre de pièces : " + selectedPngFiles.size());
                    
                    // Afficher la liste des fichiers dans la zone de texte
                    StringBuilder fileList = new StringBuilder();
                    fileList.append("Pièces trouvées dans le dossier :\n\n");
                    selectedPngFiles.forEach(file -> 
                        fileList.append(file.getName()).append("\n")
                    );
                    Home.piecesListArea.setText(fileList.toString());
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Home.piecesListArea.setText("Erreur lors de la lecture du dossier.");
                }
            }
        });
        // Bouton pour lancer la résolution
        Button startButton = ButtonFactory.createButton("Lancer la résolution", Color.web("#926871"));
startButton.setMaxWidth(Double.MAX_VALUE);

startButton.setOnAction(event -> {
    if (selectedPuzzleDirectory == null) {
        Home.piecesListArea.setText("⚠️ Veuillez d'abord choisir un dossier !");
        return;
    }

    // Création de la popup de chargement avec ProgressBar liée à la tâche
    Stage loadingPopup = new Stage();
    loadingPopup.initModality(Modality.APPLICATION_MODAL);
    loadingPopup.setTitle("Chargement...");

    VBox loadingBox = new VBox(10);
    loadingBox.setAlignment(Pos.CENTER);
    loadingBox.setPadding(new Insets(20));

    ProgressBar loadingBar = new ProgressBar(0);
    Label loadingLabel = new Label("Résolution du puzzle en cours...");

    loadingBox.getChildren().addAll(loadingLabel, loadingBar);
    Scene loadingScene = new Scene(loadingBox, 300, 100);
    loadingPopup.setScene(loadingScene);
    loadingPopup.show();

    // Création d'une Task pour exécuter la résolution en arrière-plan
    Task<Void> task = new Task<>() {
        @Override
        protected Void call() throws Exception {
            Path folderPath = selectedPuzzleDirectory.toPath();
            PuzzleSolver solver = new PuzzleSolver(folderPath);

            solver.setProgressListener(progress -> updateProgress(progress, 1.0));

            long startTime = System.currentTimeMillis();

            PuzzleSolver.PuzzleResult result = solver.solvePuzzle();

            // Vérification des pièces restantes
            List<String> remaining = result.getRemainingIds();
            if (!remaining.isEmpty()) {
                // Affichage popup dans le thread UI
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Puzzle non résolu");
                    alert.setHeaderText("Attention !");
                    alert.setContentText("Le puzzle n'est pas complètement résolu.\n"
                        + "Pièces non placées : " + String.join(", ", remaining));
                    alert.showAndWait();
                });
                // On stoppe ici pour ne pas continuer l'assemblage
                return null;
            }

            BufferedImage assembledImage = PuzzleImageViewer.getAssembledImageWithProgress(folderPath, solver, result, progress -> {
                updateProgress(progress, 1.0);
            });

            long endTime = System.currentTimeMillis();
            double durationSeconds = (endTime - startTime) / 1000.0;

            Image fxImage = SwingFXUtils.toFXImage(assembledImage, null);

            Platform.runLater(() -> {
                Home.gridPane.getChildren().clear();
                Home.fusionImageView.setImage(fxImage);
                Home.fusionImageView.setPreserveRatio(true);
                Home.fusionImageView.setSmooth(true);
                Home.fusionImageView.setFitWidth(600);
                Home.fusionImageView.setFitHeight(400);
                Home.derniereImageAssemblee = assembledImage;

                String[][] matrix = result.getMatrix();
                StringBuilder sb = new StringBuilder("Résolution terminée !\n\n");
                for (int r = 0; r < matrix.length; r++) {
                    for (int c = 0; c < matrix[0].length; c++) {
                        String pieceId = matrix[r][c];
                        if (pieceId == null) pieceId = "----";
                        sb.append(String.format("%-15s", pieceId));
                    }
                    sb.append("\n");
                }
                Home.piecesListArea.setText(sb.toString());

                timerLabel.setText(String.format("Timer ⏱ : %.2f secondes", durationSeconds));
            });
            return null;
        }
    };

    // Lie la barre de progression à la progression du task
    loadingBar.progressProperty().bind(task.progressProperty());

    // Quand la tâche est terminée, ferme la popup
    task.setOnSucceeded(e -> loadingPopup.close());
    task.setOnFailed(e -> {
        loadingPopup.close();
        Home.piecesListArea.setText("❌ Erreur lors de la résolution.");
        timerLabel.setText("⏱ Échec de la résolution.");
    });

    // Lance la tâche dans un thread de fond
    Thread th = new Thread(task);
    th.setDaemon(true);
    th.start();
});



Button downloadButton = ButtonFactory.createButton("Télécharger l'image", Color.web("#2ecc71")); // Couleur verte, harmonieuse
downloadButton.setMaxWidth(Double.MAX_VALUE);
downloadButton.setOnAction(e -> {
    if (Home.derniereImageAssemblee == null) {
        Home.piecesListArea.setText("❌ Aucune image à sauvegarder.");
        return;
    }

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Enregistrer l'image fusionnée");
    fileChooser.setInitialFileName("puzzle_resolu.png");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PNG", "*.png"));

    File file = fileChooser.showSaveDialog(null);
    if (file != null) {
        try {
            ImageIO.write(Home.derniereImageAssemblee, "png", file);
            Home.piecesListArea.setText("✅ Image sauvegardée : " + file.getName());
        } catch (IOException ex) {
            Home.piecesListArea.setText("❌ Erreur lors de la sauvegarde : " + ex.getMessage());
            ex.printStackTrace();
        }
    } else {
        Home.piecesListArea.setText("❌ Sauvegarde annulée.");
    }
});

        VBox statsPanel = StatsPanelFactory.createStatsPanel(pieceLabel, timerLabel);

        sideBarPanel.getChildren().addAll(titleLabel, addFolderButton, directoryLabel, startButton, downloadButton, statsPanel);

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
