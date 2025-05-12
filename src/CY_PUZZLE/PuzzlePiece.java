package CY_PUZZLE;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class PuzzlePiece {

    private BufferedImage image;
    private boolean[][] masque; // matrice de la forme du puzzle : 0 si vide, 1 si couleur.
    private boolean[] topEdge, bottomEdge, leftEdge, rightEdge; // Stocker chaque coté de la forme.

    public PuzzlePiece(File file) throws Exception {
        
        this.image = ImageIO.read(file);
        this.masque = extractMask();
        extractEdges();
    }

    public BufferedImage getImage() {
        return image;
    }

    // permet de creer d'un masque binaire de la forme de la pièec
    private boolean[][] extractMask() {
        int w = image.getWidth();
        int h = image.getHeight();
        boolean[][] m = new boolean[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int alpha = (image.getRGB(x, y) >> 24) & 0xff;
                m[y][x] = alpha > 0;
            }
        }
        return m;
    }


    // On récup les 4 bords 
    private void extractEdges() {
        int w = image.getWidth();
        int h = image.getHeight();
        topEdge = new boolean[w];
        bottomEdge = new boolean[w];
        leftEdge = new boolean[h];
        rightEdge = new boolean[h];

        for (int x = 0; x < w; x++) {
            topEdge[x] = masque[0][x];
            bottomEdge[x] = masque[h - 1][x];
        }
        for (int y = 0; y < h; y++) {
            leftEdge[y] = masque[y][0];
            rightEdge[y] = masque[y][w - 1];
        }
    }

    // méthode pour comparer un bord avec celui d’une autre pièce 
    public int compareBord(PuzzlePiece other, String side, String otherSide) {
        boolean[] bord1 = getBord(side);
        boolean[] bord2 = other.getBord(otherSide);
        if (bord1 == null || bord2 == null || bord1.length != bord2.length) return Integer.MAX_VALUE;

        int diff = 0;
        for (int i = 0; i < bord1.length; i++) {
            if (bord1[i] != bord2[i]) diff++;
        }
        return diff;
    }

    // acces rapide aux bords
    public boolean[] getBord(String side) {
        return switch (side.toLowerCase()) {
            case "top" -> topEdge;
            case "bottom" -> bottomEdge;
            case "left" -> leftEdge;
            case "right" -> rightEdge;
            default -> null;
        };
    }
    //methode qui retourne le nombre de cotés lisse de la piece
    public int countFreeSides(Image image) {
    PixelReader reader = image.getPixelReader();
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    int threshold = 10; // tolérance (ex: au moins 10 pixels "vides")

    int freeSides = 0;

    // Vérifie le haut
    int transparentTop = 0;
    for (int x = 0; x < width; x++) {
        Color c = reader.getColor(x, 0);
        if (c.getOpacity() < 0.1) transparentTop++;
    }
    if (transparentTop > threshold) freeSides++;

    // Vérifie le bas
    int transparentBottom = 0;
    for (int x = 0; x < width; x++) {
        Color c = reader.getColor(x, height - 1);
        if (c.getOpacity() < 0.1) transparentBottom++;
    }
    if (transparentBottom > threshold) freeSides++;

    // Vérifie la gauche
    int transparentLeft = 0;
    for (int y = 0; y < height; y++) {
        Color c = reader.getColor(0, y);
        if (c.getOpacity() < 0.1) transparentLeft++;
    }
    if (transparentLeft > threshold) freeSides++;

    // Vérifie la droite
    int transparentRight = 0;
    for (int y = 0; y < height; y++) {
        Color c = reader.getColor(width - 1, y);
        if (c.getOpacity() < 0.1) transparentRight++;
    }
    if (transparentRight > threshold) freeSides++;

    return freeSides;
}

}
