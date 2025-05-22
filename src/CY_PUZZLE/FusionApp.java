package CY_PUZZLE;
import javafx.application.Platform;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;

import javafx.scene.Node; // pour pouvoir passer un Node au ScrollPane si besoin

import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import javafx.scene.control.ScrollPane; // ✅ celui-là

import java.io.File;
import java.nio.file.Paths;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;


import java.nio.file.Path;

public class FusionApp {

    private static String[][] matrixFromResolution;
    private static Image imageFusionFinale = null;

    public static void setMatrixFromButton(String[][] matrix) {
        matrixFromResolution = matrix;
    }

    
    
public static void showFusion(String[][] matrix, Path folderPath) {
    Image imageFusionFinale = assemblerPuzzle(matrix, folderPath, 1.0);
    if (imageFusionFinale == null) {
        System.out.println("❌ Échec de la fusion");
        return;
    }

    // Mise à jour de l'image dans l'ImageView statique de la vue principale
    Platform.runLater(() -> {
        Accueil.fusionImageView.setImage(imageFusionFinale);

        // Ajuste la taille pour que ça rentre bien (tu peux adapter)
        double windowWidth = 600;  // par exemple largeur max dans ta vue
        double windowHeight = 400; // hauteur max souhaitée

        double imgWidth = imageFusionFinale.getWidth();
        double imgHeight = imageFusionFinale.getHeight();

        double scaleX = windowWidth / imgWidth;
        double scaleY = windowHeight / imgHeight;
        double scale = Math.min(scaleX, scaleY);

        Accueil.fusionImageView.setFitWidth(imgWidth * scale);
        Accueil.fusionImageView.setFitHeight(imgHeight * scale);
        Accueil.fusionImageView.setPreserveRatio(true);
        Accueil.fusionImageView.setSmooth(true);
        Accueil.gridPane.setVisible(false);
Accueil.gridPane.setManaged(false);
Accueil.piecesListArea.setVisible(false);
Accueil.piecesListArea.setManaged(false);

    });
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

public static void sauvegarderImageFusion(File file) {
    Image imageFusion = Accueil.fusionImageView.getImage();
    if (imageFusion == null) {
        System.out.println("Aucune image fusionnée à sauvegarder !");
        return;
    }

    try {
        WritableImage writableImage = new WritableImage((int) imageFusion.getWidth(), (int) imageFusion.getHeight());
        writableImage.getPixelWriter().setPixels(0, 0, (int) imageFusion.getWidth(), (int) imageFusion.getHeight(),
            imageFusion.getPixelReader(), 0, 0);

        ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
        System.out.println("✅ Image sauvegardée dans : " + file.getAbsolutePath());
    } catch (Exception e) {
        e.printStackTrace();
    }
}


}
    