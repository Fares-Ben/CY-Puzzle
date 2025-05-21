package CY_PUZZLE;

import javafx.application.Application;
import javafx.scene.Scene;
// ✅ LE BON
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import javafx.scene.control.ScrollPane; // ✅ celui-là

import java.io.File;
import java.nio.file.Paths;

import javafx.scene.image.ImageView;


import java.nio.file.Path;

public class FusionApp {

    private static String[][] matrixFromResolution;

    public static void setMatrixFromButton(String[][] matrix) {
        matrixFromResolution = matrix;
    }

    
    
public static void showFusion(String[][] matrix, Path folderPath) {
    Image fusion = assemblerPuzzle(matrix, folderPath, 1.0); // Échelle 1.0 pour avoir l'image complète
    if (fusion == null) {
        System.out.println("❌ Échec de la fusion");
        return;
    }

    ImageView view = new ImageView(fusion);
    view.setPreserveRatio(true); // Important pour ne pas déformer l'image
    view.setSmooth(true);
    view.setCache(true);

    ScrollPane scrollPane = new ScrollPane(view);
    scrollPane.setPannable(true); // Permet de déplacer l'image si besoin
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);

    // Taille initiale de la fenêtre
    double windowWidth = 1000;
    double windowHeight = 800;

    // Adapter l’image à la taille de la fenêtre sans déborder
    double imgWidth = fusion.getWidth();
    double imgHeight = fusion.getHeight();

    double scaleX = windowWidth / imgWidth;
    double scaleY = windowHeight / imgHeight;
    double scale = Math.min(scaleX, scaleY); // Prend le plus petit pour tout faire rentrer

    view.setFitWidth(imgWidth * scale);
    view.setFitHeight(imgHeight * scale);

    Scene scene = new Scene(scrollPane, windowWidth, windowHeight);

    Stage stage = new Stage();
    stage.setTitle("Puzzle Fusionné");
    stage.setScene(scene);
    stage.show();
}


    public static Image assemblerPuzzle(String[][] grille, Path folderPath, double scale) {

        int rows = grille.length;
        int cols = grille[0].length;
        int decalage = 25;
        Image[] lignesFusionnees = new Image[rows];
        for (int i = 0; i < rows; i++) {
            lignesFusionnees[i] = fusionnerLigne(grille[i], folderPath, scale, decalage);

        }
        return fusionnerColonne(lignesFusionnees, decalage);
    }

    public static Image fusionnerLigne(String[] ligne, Path folderPath, double scale, int decalage) {
        int nbImages = ligne.length;
        Image[] images = new Image[nbImages];

        String basePath = folderPath.toString();
        int totalWidth = 0;
        int maxHeight = 0;

        for (int i = 0; i < nbImages; i++) {
            String filePath = Paths.get(basePath, ligne[i]).toUri().toString();
            images[i] = new Image(filePath);
            totalWidth += (int) (images[i].getWidth() * scale) - decalage;
            maxHeight = Math.max(maxHeight, (int) (images[i].getHeight() * scale));
        }

        totalWidth += decalage;

        WritableImage result = new WritableImage(totalWidth, maxHeight);
        PixelWriter writer = result.getPixelWriter();

        int currentX = 0;
        for (Image image : images) {
            PixelReader reader = image.getPixelReader();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color color = reader.getColor(x, y);
                    if (color.getOpacity() == 0.0) continue;

                    int scaledX = (int) (x * scale) + currentX;
                    int scaledY = (int) (y * scale);
                    if (scaledX < result.getWidth() && scaledY < result.getHeight()) {
                        writer.setColor(scaledX, scaledY, color);
                    }
                }
            }
            currentX += (int) (image.getWidth() * scale) - decalage;
        }

        return result;
    }

    public static Image fusionnerColonne(Image[] images, int decalage) {
        int maxWidth = 0;
        int totalHeight = 0;

        for (int i = 0; i < images.length; i++) {
            int height = (int) images[i].getHeight();
            totalHeight += height;
            if (i > 0) totalHeight -= decalage;
            maxWidth = Math.max(maxWidth, (int) images[i].getWidth());
        }

        WritableImage result = new WritableImage(maxWidth, totalHeight);
        PixelWriter writer = result.getPixelWriter();

        int currentY = 0;
        for (int i = 0; i < images.length; i++) {
            PixelReader reader = images[i].getPixelReader();
            for (int y = 0; y < images[i].getHeight(); y++) {
                for (int x = 0; x < images[i].getWidth(); x++) {
                    Color color = reader.getColor(x, y);
                    if (color.getOpacity() == 0.0) continue;

                    int scaledX = x;
                    int scaledY = y + currentY;
                    if (scaledX < maxWidth && scaledY < totalHeight) {
                        writer.setColor(scaledX, scaledY, color);
                    }
                }
            }
            currentY += (int) images[i].getHeight();
            if (i < images.length - 1) currentY -= decalage;
        }

        return result;
    }
}
