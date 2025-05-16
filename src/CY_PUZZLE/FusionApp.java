    package CY_PUZZLE;

    import javafx.application.Application;
    import javafx.scene.Scene;
    import javafx.scene.image.*;
    import javafx.scene.layout.StackPane;
    import javafx.stage.Stage;
    import javafx.scene.paint.Color;
    import java.io.File;
    import java.nio.file.Paths;
    
import javafx.scene.image.ImageView;

    public class FusionApp extends Application {

        public static void main(String[] args) {
            launch(args);
        }


       @Override
    public void start(Stage primaryStage) {
        String imgPath1 = Paths.get("resources", "images", "img_0_0.png").toUri().toString();
        String imgPath2 = Paths.get("resources", "images", "img_0_1.png").toUri().toString();

        Image img1 = new Image(imgPath1);
        Image img2 = new Image(imgPath2);

        Image imageFusionnee = fusionnerImages(img1, img2);

        ImageView imageView = new ImageView(imageFusionnee);

        StackPane root = new StackPane(); // vide
        root.getChildren().add(imageView); // on ajoute l'imageView explicitement

        Scene scene = new Scene(root, imageFusionnee.getWidth(), imageFusionnee.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Images fusionnées verticalement");
        primaryStage.show();
}


public static Image fusionnerImages(Image img1, Image img2) {
    int decalage = 0;
    double scale = 0.5; // réduction par 10

    int width = (int) (Math.max(img1.getWidth(), img2.getWidth()) * scale);
    int height = (int) ((img1.getHeight() + img2.getHeight() - decalage) * scale);

    WritableImage result = new WritableImage(width, height);
    PixelWriter writer = result.getPixelWriter();

    // Lire img1
    PixelReader reader1 = img1.getPixelReader();
    for (int y = 0; y < (int) img1.getHeight(); y++) {
        for (int x = 0; x < (int) img1.getWidth(); x++) {
            Color color = reader1.getColor(x, y);
            int scaledX = (int) (x * scale);
            int scaledY = (int) (y * scale);
            if (scaledX < width && scaledY < height) {
                writer.setColor(scaledX, scaledY, color);
            }
        }
    }

    // Lire img2, avec décalage
    PixelReader reader2 = img2.getPixelReader();
    int offsetY = (int) ((img1.getHeight() - decalage) * scale);
    for (int y = 0; y < (int) img2.getHeight(); y++) {
        for (int x = 0; x < (int) img2.getWidth(); x++) {
            Color color = reader2.getColor(x, y);
            if (color.getOpacity() > 0) {
                int scaledX = (int) (x * scale);
                int scaledY = (int) (y * scale) + offsetY;
                if (scaledX < width && scaledY < height) {
                    writer.setColor(scaledX, scaledY, color);
                }
            }
        }
    }

    return result;
}



}
