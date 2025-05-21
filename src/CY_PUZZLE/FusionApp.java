 package CY_PUZZLE;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.image.PixelReader;
    import javafx.scene.image.PixelWriter;
    import javafx.scene.image.WritableImage;
    import javafx.scene.paint.Color;

    import javafx.application.Application;
    import javafx.scene.Scene;
    import javafx.scene.layout.StackPane;
    import javafx.stage.Stage;
    import java.nio.file.Paths;

    public class FusionApp extends Application {

        public static void main(String[] args) {
            launch(args);
        }


    @Override
    public void start(Stage primaryStage) {
        String[][] grille = {
            {"img_0_0.png", "img_1_0.png", "img_2_0.png", "img_3_0.png", "img_4_0.png", "img_5_0.png", "img_6_0.png", "img_7_0.png", "img_8_0.png", "img_9_0.png"},
            {"img_0_1.png", "img_1_1.png", "img_2_1.png", "img_3_1.png", "img_4_1.png", "img_5_1.png", "img_6_1.png", "img_7_1.png", "img_8_1.png", "img_9_1.png"},
            {"img_0_2.png", "img_1_2.png", "img_2_2.png", "img_3_2.png", "img_4_2.png", "img_5_2.png", "img_6_2.png", "img_7_2.png", "img_8_2.png", "img_9_2.png"},
            {"img_0_3.png", "img_1_3.png", "img_2_3.png", "img_3_3.png", "img_4_3.png", "img_5_3.png", "img_6_3.png", "img_7_3.png", "img_8_3.png", "img_9_3.png"},
            {"img_0_4.png", "img_1_4.png", "img_2_4.png", "img_3_4.png", "img_4_4.png", "img_5_4.png", "img_6_4.png", "img_7_4.png", "img_8_4.png", "img_9_4.png"},
        };

        Image puzzle = assemblerPuzzle(grille);
        ImageView imageView = new ImageView(puzzle);
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, puzzle.getWidth(), puzzle.getHeight());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Image finale");
        primaryStage.show();
    }



public static Image assemblerPuzzle(String[][] grille) {
    int rows = grille.length;
    int cols = grille[0].length;

    double totalRawWidth = 0;
    double totalRawHeight = 0;

    String basePath = Paths.get("resources", "images").toString();
    int decalage = 0;
    // Calcul largeur max pour chaque colonne
    for (int col = 0; col < cols; col++) {
        double colWidth = 0;
        for (int row = 0; row < rows; row++) {
            String path = Paths.get(basePath, grille[row][col]).toUri().toString();
            Image img = new Image(path);
            colWidth = Math.max(colWidth, img.getWidth());
        }
        totalRawWidth += colWidth;
    }

    // Calcul hauteur max pour chaque ligne
    for (int row = 0; row < rows; row++) {
        double rowHeight = 0;
        for (int col = 0; col < cols; col++) {
            String path = Paths.get(basePath, grille[row][col]).toUri().toString();
            Image img = new Image(path);
            rowHeight = Math.max(rowHeight, img.getHeight());
        }
        totalRawHeight += rowHeight;
    }

    // Calcul dynamique du scale pour que tout rentre dans la fenetre
    // double scaleX = 1500 / totalRawWidth;
    // double scaleY = 1500 / totalRawHeight;
    double scale = 0.5;


    // Fusionne toutes les lignes en une seule image
    Image[] lignesFusionnees = new Image[rows];
    for (int i = 0; i < rows; i++) {
        lignesFusionnees[i] = fusionnerLigne(grille[i], scale, decalage);
    }


    return fusionnerColonne(lignesFusionnees, decalage);
}



public static Image fusionnerLigne(String[] ligne, double scale, int decalage) {
    int nbImages = ligne.length;
    Image[] images = new Image[nbImages];

    String basePath = Paths.get("resources", "images").toString();
    int totalWidth = 0;
    int maxHeight = 0;

    // Charger les images et calculer les dimensions finales
    for (int i = 0; i < nbImages; i++) {
        String filePath = Paths.get(basePath, ligne[i]).toUri().toString();
        images[i] = new Image(filePath);
        totalWidth += (int) (images[i].getWidth() * scale) - decalage;
        maxHeight = Math.max(maxHeight, (int) (images[i].getHeight() * scale));
    }
    totalWidth += decalage; // Ajouter le dernier chevauchement retiré en trop

    WritableImage result = new WritableImage(totalWidth, maxHeight);
    PixelWriter writer = result.getPixelWriter();

    int currentX = 0;
    for (Image image : images) {
        PixelReader reader = image.getPixelReader();
        int imgWidth = (int) image.getWidth();
        int imgHeight = (int) image.getHeight();

        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                Color color = reader.getColor(x, y);

                // Ne pas dessiner les pixels entièrement transparents
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


public static Image fusionnerColonne(Image[] images, int decalage){
    int maxWidth = 0;
    int totalHeight = 0;

    // Calculer la hauteur totale en tenant compte du chevauchement constant
    for (int i = 0; i < images.length; i++) {
        int height = (int) images[i].getHeight();
        totalHeight += height;
        if (i > 0) { // On soustrait le chevauchement pour toutes sauf la première
            totalHeight -= decalage;
        }
        maxWidth = Math.max(maxWidth, (int) (images[i].getWidth()));
    }

    WritableImage result = new WritableImage(maxWidth, totalHeight);
    PixelWriter writer = result.getPixelWriter();

    int currentY = 0;
    for (int i = 0; i < images.length; i++) {
        Image img = images[i];
        PixelReader reader = img.getPixelReader();

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color color = reader.getColor(x, y);

                // Ne pas dessiner les pixels entièrement transparents
                if (color.getOpacity() == 0.0) continue;

                int scaledX = x ;
                int scaledY = y + currentY;

                if (scaledX < maxWidth && scaledY < totalHeight) {
                    writer.setColor(scaledX, scaledY, color);
                }
            }
        }

        currentY += (int) (img.getHeight());
            if (i < images.length - 1) {
                currentY -= decalage;
                System.out.println("Image " + i + " currentY: " + currentY);
        }

    }

    return result;
}
}
    