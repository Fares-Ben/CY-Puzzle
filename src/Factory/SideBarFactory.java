/**
 * SideBarFactory builds the sidebar with buttons and controls used during the puzzle solving process.
 */
package Factory;

import Factory.PuzzleImageViewer;
import Factory.StatsPanelFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import Model.PieceSave; // Correct package for PieceSave
import Resolution_Puzzle.PuzzleSolver;


/**
 * Factory class to generate the sidebar of the puzzle GUI.
 */
public class SideBarFactory {

    private static final double SIDEBAR_WIDTH = 300; // Largeur fixe de la barre latérale
    private static File selectedPuzzleDirectory = null;
    private static List<File> selectedPngFiles = List.of();
/** ImageView to display the assembled puzzle inside the sidebar. */
public static ImageView fusionImageView;

/**
 * Creates the sidebar panel containing controls and labels.
 *
 * @param pieceLabel label for number of placed pieces
 * @param timerLabel label for elapsed time
 * @return VBox containing sidebar UI components
 */
public static VBox createSideBarPanel(Label pieceLabel, Label timerLabel) {
        VBox sideBarPanel = new VBox(10);
        sideBarPanel.setAlignment(Pos.TOP_CENTER);
        sideBarPanel.setPadding(new Insets(30));
        sideBarPanel.setPrefWidth(SIDEBAR_WIDTH);
        sideBarPanel.setMinWidth(SIDEBAR_WIDTH);  // Force the minimum width
        sideBarPanel.setMaxWidth(SIDEBAR_WIDTH);  // Force the maximum width

        // Degraded background
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

        // Button to choose a folder 
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

                // Show the parts in the grid
                Home.gridPane.getChildren().clear();
                try (Stream<Path> files = Files.list(selectedDir.toPath())) {
                    selectedPngFiles = files
                        .filter(p -> p.toString().toLowerCase().endsWith(".png"))
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                    
                    // Show each image in the grid
                    int col = 0;
                    int row = 0;
                    int maxCol = 5; // Number of columns in the grid

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

                    // Update the label of the parts
                    pieceLabel.setText("Nombre de pièces : " + selectedPngFiles.size());
                    
                    // Show the file list in the text box
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
        // Button to launch the resolution
        Button startButton = ButtonFactory.createButton("Lancer la résolution", Color.web("#926871"));
startButton.setMaxWidth(Double.MAX_VALUE);

startButton.setOnAction(event -> {
    if (selectedPuzzleDirectory == null) {
        Home.piecesListArea.setText("⚠️ Veuillez d'abord choisir un dossier !");
        return;
    }

    // Creation of the loading popup with progressbar linked to the task
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

    // Creation of a task to perform resolution in the background
    Task<Void> task = new Task<>() {
        @Override
        protected Void call() throws Exception {
            Path folderPath = selectedPuzzleDirectory.toPath();
            PuzzleSolver solver = new PuzzleSolver(folderPath);

            solver.setProgressListener(progress -> updateProgress(progress, 1.0));

            long startTime = System.currentTimeMillis();

            PuzzleSolver.PuzzleResult result = solver.solvePuzzle();

            // Verification of the remaining parts
            List<String> remaining = result.getRemainingIds();
            if (!remaining.isEmpty()) {
                // 
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Puzzle non résolu");
                    alert.setHeaderText("Attention !");
                    alert.setContentText("Le puzzle n'est pas complètement résolu.\n"
                        + "Pièces non placées : " + String.join(", ", remaining));
                    alert.showAndWait();
                });
                // We can return here if we want to show the remaining pieces
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

    // Binds the progression bar to the progression of the Task
    loadingBar.progressProperty().bind(task.progressProperty());

    // When the task is over, closes the popup
    task.setOnSucceeded(e -> loadingPopup.close());
    task.setOnFailed(e -> {
        loadingPopup.close();
        Home.piecesListArea.setText("❌ Erreur lors de la résolution.");
        timerLabel.setText("⏱ Échec de la résolution.");
    });

    // Launches the task in a background thread
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

/**
 * Converts a BufferedImage to a JavaFX Image.
 *
 * @param bf the BufferedImage to convert
 * @return the JavaFX Image
 */
public static Image convertToFxImage(BufferedImage bf) {
        return SwingFXUtils.toFXImage(bf, null);
    }

/**
 * Opens a directory chooser dialog to select the puzzle folder.
 *
 * @return the selected folder
 */
public static File getSelectedPuzzleDirectory() {
        return selectedPuzzleDirectory;
    }

/**
 * Opens a file chooser dialog to select PNG files.
 *
 * @return list of selected PNG files
 */
public static List<File> getSelectedPngFiles() {
        return selectedPngFiles;
    }
}
