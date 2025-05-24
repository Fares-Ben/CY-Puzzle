/**
 * PuzzleImageViewer is a Swing component that displays the fully assembled puzzle.
 * <p>
 * It supports zooming, resizing, and centering the final image to fit the window.
 * This class also includes methods to retrieve the puzzle image with or without a progress listener.
 * </p>
 */

package Factory;

import Model.PieceSave; // Correct package for PieceSave
import Resolution_Puzzle.PuzzleAnalyzer;
import Resolution_Puzzle.PuzzleSolver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Factory.ProgressListener;

/**
 * JavaFX component for displaying and manipulating the assembled puzzle image.
 */
public class PuzzleImageViewer extends JFrame {
/** Path to the folder containing the puzzle piece images. */
private final Path piecesFolder;
/** Matrix containing the layout of piece IDs in their solved positions. */
private final String[][] puzzleMatrix;    
/** Total width of the assembled puzzle image. */
private final int tailleLeft;
/** Total height of the assembled puzzle image. */
private final int tailleTop;


    // Taille maximale d'affichage
    private static final int MAX_DISPLAY_WIDTH = 800;
    private static final int MAX_DISPLAY_HEIGHT = 600;

/** Map caching the loaded images of each puzzle piece, keyed by filename. */
private final Map<String, BufferedImage> pieceImages = new HashMap<>();

/** Map storing metadata for each puzzle piece, including corner positions. */
private final Map<String, PieceSave> pieceData = new HashMap<>();
    
    

/**
 * Constructs a new PuzzleImageViewer with the specified puzzle data.
 *
 * @param piecesFolder the folder containing all puzzle pieces as images
 * @param puzzleMatrix the solved matrix containing the piece IDs in their positions
 * @param tailleTop the total height of the final puzzle
 * @param tailleLeft the total width of the final puzzle
 */
    public PuzzleImageViewer(Path piecesFolder, 
    String[][] puzzleMatrix, 
    int tailleTop, 
    int tailleLeft) { 

        this.piecesFolder = piecesFolder;
        this.puzzleMatrix = puzzleMatrix;
        this.tailleTop    = tailleTop;
        this.tailleLeft   = tailleLeft;
        initializeFrame();
    }

/**
 * Alternative constructor using a PuzzleSolver and its result.
 *
 * @param piecesFolder the folder containing the pieces
 * @param solver the puzzle solver instance used
 * @param result the result of the puzzle solving
 */
public PuzzleImageViewer(Path piecesFolder, PuzzleSolver solver, PuzzleSolver.PuzzleResult result) {
        this(piecesFolder,
             result.getMatrix(),
             solver.getTailleTop(),
             solver.getTailleLeft());
    }

    private void initializeFrame() {
        setTitle("Puzzle Résolu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            loadPiecesData();
            BufferedImage finalImage = assemblePuzzle(null);
            // Redimensionnement proportionnel
            BufferedImage displayImage = getScaledInstance(finalImage,
                MAX_DISPLAY_WIDTH, MAX_DISPLAY_HEIGHT);

            JLabel imageLabel = new JLabel(new ImageIcon(displayImage));
            getContentPane().add(imageLabel, BorderLayout.CENTER);

            pack();
            setResizable(false);
            setLocationRelativeTo(null);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des images: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPiecesData() throws IOException {
        try (var stream = Files.list(piecesFolder)) {
            for (Path path : (Iterable<Path>) stream::iterator) {
                if (!Files.isRegularFile(path)) continue;
                BufferedImage img = ImageIO.read(path.toFile());
                if (img != null) {
                    String filename = path.getFileName().toString();
                    pieceImages.put(filename, img);
                    PuzzleAnalyzer analyzer = new PuzzleAnalyzer(filename, img);
                    PieceSave piece = analyzer.getPiece();
                    if (piece != null) pieceData.put(filename, piece);
                }
            }
        }
    }

    private BufferedImage assemblePuzzle(ProgressListener listener) {
        int rows = puzzleMatrix.length;
        int cols = puzzleMatrix[0].length;
        BufferedImage finalImage = new BufferedImage(tailleTop, tailleLeft, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = finalImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, tailleTop, tailleLeft);

        int[][] posX = new int[rows][cols];
        int[][] posY = new int[rows][cols];

        // Coin (0,0)
        posX[0][0] = 0;
        posY[0][0] = 0;

        // 1) Première ligne
        for (int c = 1; c < cols; c++) {
            String prevId = puzzleMatrix[0][c - 1];
            int[] prev    = pieceData.get(prevId).coins();
            posX[0][c]    = posX[0][c - 1] + (prev[2] - prev[0]) + 1;
            posY[0][c]    = posY[0][0];
        }

        // 2) Première colonne
        for (int r = 1; r < rows; r++) {
            String aboveId = puzzleMatrix[r - 1][0];
            int[] above    = pieceData.get(aboveId).coins();
            posY[r][0]     = posY[r - 1][0] + (above[7] - above[1]) + 1;
            posX[r][0]     = posX[0][0];
        }

        // 3) Reste de la grille
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                String leftId  = puzzleMatrix[r][c - 1];
                String aboveId = puzzleMatrix[r - 1][c];
                int[] left     = pieceData.get(leftId).coins();
                int[] above    = pieceData.get(aboveId).coins();
                posX[r][c]     = posX[r][c - 1] + (left[2] - left[0]) + 1;
                posY[r][c]     = posY[r - 1][c] + (above[7] - above[1]) + 1;
            }
        }
        //pour la barre de progression
    int totalPieces = rows * cols;
    int piecesDrawn = 0;
        // Dessin des pièces
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String id = puzzleMatrix[r][c];
                BufferedImage img = pieceImages.get(id);
                PieceSave info    = pieceData.get(id);
                if (img != null && info != null) {
                    g2d.drawImage(img,
                        posX[r][c] - info.coins()[0],
                        posY[r][c] - info.coins()[1],
                        null);
                } else {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(posX[r][c], posY[r][c], 100, 100);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("Missing: " + id,
                        posX[r][c] + 10,
                        posY[r][c] + 50);
                }
                piecesDrawn++;
            if (listener != null) {
            double progressStep = 0.5 / (totalPieces * 3);
            double progress = 0.5 + progressStep * piecesDrawn;
            listener.onProgress(progress);

            // Pause minime pour que la barre ait le temps de s’afficher
            try {
                Thread.sleep(5);  // 5 ms, à ajuster (plus grand = plus lent)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
            }
        }
        g2d.dispose();
        return finalImage;
    }

    /**
     * Retourne une version redimensionnée de l'image d'origine,
     * tout en conservant le ratio, pour tenir dans maxWidth×maxHeight.
     */
    private BufferedImage getScaledInstance(BufferedImage img, int maxWidth, int maxHeight) {
        int w = img.getWidth();
        int h = img.getHeight();
        double scale = Math.min((double) maxWidth / w, (double) maxHeight / h);
        int newW = (int) (w * scale);
        int newH = (int) (h * scale);
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resized.createGraphics();
        g2.drawImage(tmp, 0, 0, null);
        g2.dispose();
        return resized;
    }
/**
 * Launches a GUI window displaying the solved puzzle.
 *
 * @param piecesFolder path to the folder containing the puzzle pieces
 * @param solver the solver instance used for assembling the puzzle
 * @param result the result containing the solved matrix
 */
public static void displayPuzzle(Path piecesFolder, PuzzleSolver solver, PuzzleSolver.PuzzleResult result) { 

        SwingUtilities.invokeLater(() -> {
            try {
                PuzzleImageViewer v = new PuzzleImageViewer(piecesFolder, solver, result);
                v.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'affichage: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

/**
 * Entry point for standalone usage to view a puzzle directly from the console.
 *
 * @param args expects one argument: the path to the folder containing pieces
 */
public static void main(String[] args) { 
        if (args.length < 1) {
            System.err.println("Usage: java PuzzleImageViewer <dossier_pieces>");
            System.exit(1);
        }
        try {
            Path folder = Path.of(args[0]);
            if (!Files.isDirectory(folder)) {
                System.err.println("Répertoire invalide: " + folder);
                System.exit(2);
            }
            PuzzleSolver solver = new PuzzleSolver(folder);
            PuzzleSolver.PuzzleResult result = solver.solvePuzzle();
            displayPuzzle(folder, solver, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/**
 * Builds and returns the final puzzle image, without displaying it.
 *
 * @param piecesFolder the folder containing the pieces
 * @param solver the solver used to assemble the puzzle
 * @param result the result object containing the matrix
 * @return a BufferedImage of the assembled puzzle
 * @throws IOException if an image can't be loaded
 */
public static BufferedImage getAssembledImage(Path piecesFolder, PuzzleSolver solver, PuzzleSolver.PuzzleResult result) throws IOException {
    return getAssembledImageWithProgress(piecesFolder, solver, result, null);
}

/**
 * Builds and returns the final puzzle image, displaying a progress bar during the process.
 *
 * @param piecesFolder the folder containing the pieces
 * @param solver the solver used to assemble the puzzle
 * @param result the result object containing the matrix
 * @param listener optional listener to track progress
 * @return a BufferedImage of the assembled puzzle
 * @throws IOException if an image can't be loaded
 */
public static BufferedImage getAssembledImageWithProgress(Path piecesFolder, PuzzleSolver solver, PuzzleSolver.PuzzleResult result, ProgressListener listener) throws IOException {
    PuzzleImageViewer viewer = new PuzzleImageViewer(piecesFolder, solver, result);
    return viewer.assemblePuzzle(listener);
}



}
