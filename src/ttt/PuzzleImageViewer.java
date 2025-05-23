import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * PuzzleImageViewer affiche directement et redimensionne l'image finale d'un puzzle résolu.
 */
public class PuzzleImageViewer extends JFrame {
    private final Path piecesFolder;
    private final String[][] puzzleMatrix;
    private final int tailleTop;
    private final int tailleLeft;

    // Taille maximale d'affichage
    private static final int MAX_DISPLAY_WIDTH = 800;
    private static final int MAX_DISPLAY_HEIGHT = 600;

    // Cache des images et des informations des pièces
    private final Map<String, BufferedImage> pieceImages = new HashMap<>();
    private final Map<String, PieceSave> pieceData    = new HashMap<>();

    public PuzzleImageViewer(Path piecesFolder,
                             String[][] puzzleMatrix,
                             int tailleTop, int tailleLeft) {
        this.piecesFolder = piecesFolder;
        this.puzzleMatrix = puzzleMatrix;
        this.tailleTop    = tailleTop;
        this.tailleLeft   = tailleLeft;
        initializeFrame();
    }

    public PuzzleImageViewer(Path piecesFolder,
                             PuzzleSolver solver,
                             PuzzleSolver.PuzzleResult result) {
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
            BufferedImage finalImage = assemblePuzzle();
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

    private BufferedImage assemblePuzzle() {
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

    public static void displayPuzzle(Path piecesFolder,
                                     PuzzleSolver solver,
                                     PuzzleSolver.PuzzleResult result) {
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
}
