/**
 * PuzzleImageViewer is a Swing-based component that displays the fully assembled puzzle.
 * It handles loading puzzle pieces, assembling them into a final image, and displaying the result.
 */
package Factory;

import Model.PieceSave; // Represents metadata for each puzzle piece
import Resolution_Puzzle.PuzzleAnalyzer; // Analyzes individual puzzle pieces
import Resolution_Puzzle.PuzzleSolver; // Solves the puzzle by arranging pieces

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
    // Path to the folder containing puzzle piece images
    private final Path piecesFolder;

    // Matrix representing the solved puzzle layout (IDs of pieces in their positions)
    private final String[][] puzzleMatrix;

    // Dimensions of the final assembled puzzle
    private final int tailleLeft; // Total width
    private final int tailleTop;  // Total height

    // Cache for storing loaded puzzle piece images
    private final Map<String, BufferedImage> pieceImages = new HashMap<>();

    // Metadata for each puzzle piece (e.g., edge details, corner positions)
    private final Map<String, PieceSave> pieceData = new HashMap<>();

    // Maximum display dimensions for the final image
    private static final int MAX_DISPLAY_WIDTH = 800;
    private static final int MAX_DISPLAY_HEIGHT = 600;

    /**
     * Constructor to initialize the viewer with puzzle data.
     * 
     * @param piecesFolder Path to the folder containing puzzle pieces
     * @param puzzleMatrix Solved matrix of piece IDs
     * @param tailleTop Total height of the puzzle
     * @param tailleLeft Total width of the puzzle
     */
    public PuzzleImageViewer(Path piecesFolder, String[][] puzzleMatrix, int tailleTop, int tailleLeft) {
        this.piecesFolder = piecesFolder;
        this.puzzleMatrix = puzzleMatrix;
        this.tailleTop = tailleTop;
        this.tailleLeft = tailleLeft;
        initializeFrame(); // Sets up the Swing frame and loads the puzzle
    }

    /**
     * Alternative constructor that uses a PuzzleSolver and its result.
     * This simplifies initialization when solving the puzzle programmatically.
     */
    public PuzzleImageViewer(Path piecesFolder, PuzzleSolver solver, PuzzleSolver.PuzzleResult result) {
        this(piecesFolder, result.getMatrix(), solver.getTailleTop(), solver.getTailleLeft());
    }

    /**
     * Sets up the Swing frame, loads puzzle data, and displays the assembled image.
     */
    private void initializeFrame() {
        setTitle("Puzzle Résolu"); // Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close behavior
        try {
            loadPiecesData(); // Loads all puzzle piece images and metadata
            BufferedImage finalImage = assemblePuzzle(null); // Assembles the puzzle
            BufferedImage displayImage = getScaledInstance(finalImage, MAX_DISPLAY_WIDTH, MAX_DISPLAY_HEIGHT); // Scales the image for display

            // Adds the scaled image to the frame
            JLabel imageLabel = new JLabel(new ImageIcon(displayImage));
            getContentPane().add(imageLabel, BorderLayout.CENTER);

            pack(); // Adjusts the frame size to fit the content
            setResizable(false); // Prevents resizing
            setLocationRelativeTo(null); // Centers the window on the screen
        } catch (IOException e) {
            // Displays an error message if loading fails
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des images: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads all puzzle piece images and analyzes their metadata.
     * Stores the images and metadata in `pieceImages` and `pieceData`.
     */
    private void loadPiecesData() throws IOException {
        try (var stream = Files.list(piecesFolder)) {
            for (Path path : (Iterable<Path>) stream::iterator) {
                if (!Files.isRegularFile(path)) continue; // Skip non-files
                BufferedImage img = ImageIO.read(path.toFile()); // Load the image
                if (img != null) {
                    String filename = path.getFileName().toString();
                    pieceImages.put(filename, img); // Cache the image
                    PuzzleAnalyzer analyzer = new PuzzleAnalyzer(filename, img); // Analyze the piece
                    PieceSave piece = analyzer.getPiece(); // Get metadata
                    if (piece != null) pieceData.put(filename, piece); // Cache metadata
                }
            }
        }
    }

    /**
     * Assembles the puzzle by arranging the pieces based on the solved matrix.
     * 
     * @param listener Optional progress listener for tracking assembly progress
     * @return The final assembled puzzle as a BufferedImage
     */
    private BufferedImage assemblePuzzle(ProgressListener listener) {
        int rows = puzzleMatrix.length; // Number of rows in the puzzle
        int cols = puzzleMatrix[0].length; // Number of columns in the puzzle
        BufferedImage finalImage = new BufferedImage(tailleTop, tailleLeft, BufferedImage.TYPE_INT_RGB); // Create a blank canvas
        Graphics2D g2d = finalImage.createGraphics(); // Graphics context for drawing
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setColor(Color.WHITE); // Background color
        g2d.fillRect(0, 0, tailleTop, tailleLeft); // Fill the canvas with white

        // Arrays to store the positions of each piece
        int[][] posX = new int[rows][cols];
        int[][] posY = new int[rows][cols];

        // Calculate positions for the first row and column
        // Coin (0,0)
        posX[0][0] = 0;
        posY[0][0] = 0;

        // 1) First line
        for (int c = 1; c < cols; c++) {
            String prevId = puzzleMatrix[0][c - 1];
            int[] prev    = pieceData.get(prevId).coins();
            posX[0][c]    = posX[0][c - 1] + (prev[2] - prev[0]) + 1;
            posY[0][c]    = posY[0][0];
        }

        // 2) First column
        for (int r = 1; r < rows; r++) {
            String aboveId = puzzleMatrix[r - 1][0];
            int[] above    = pieceData.get(aboveId).coins();
            posY[r][0]     = posY[r - 1][0] + (above[7] - above[1]) + 1;
            posX[r][0]     = posX[0][0];
        }

        // 3) rest of the grid
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
        //for the progress bar
    int totalPieces = rows * cols;
    int piecesDrawn = 0;
        // Draw the pieces
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String id = puzzleMatrix[r][c];
                BufferedImage img = pieceImages.get(id);
                PieceSave info    = pieceData.get(id);
                if (img != null && info != null) {
                    // Draw the piece at the calculated position
                    g2d.drawImage(img,
                        posX[r][c] - info.coins()[0],
                        posY[r][c] - info.coins()[1],
                        null);
                } else {
                    // Draw a placeholder if the piece is missing
                    g2d.setColor(Color.RED);
                    g2d.fillRect(posX[r][c], posY[r][c], 100, 100);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("Missing: " + id,
                        posX[r][c] + 10,
                        posY[r][c] + 50);
                }
                piecesDrawn++;
                if (listener != null) {
                    // Update progress if a listener is provided
                    double progressStep = 0.5 / (totalPieces * 3);
                    double progress = 0.5 + progressStep * piecesDrawn;
                    listener.onProgress(progress);

                    // for progress bar
                    try {
                        Thread.sleep(5);  // 5 ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        g2d.dispose(); // Release resources
        return finalImage;
    }

    /**
     * Scales the final image to fit within the specified dimensions while maintaining the aspect ratio.
     */
    private BufferedImage getScaledInstance(BufferedImage img, int maxWidth, int maxHeight) {
        int w = img.getWidth();
        int h = img.getHeight();
        double scale = Math.min((double) maxWidth / w, (double) maxHeight / h); // Calculate the scaling factor
        int newW = (int) (w * scale);
        int newH = (int) (h * scale);
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH); // Scale the image
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resized.createGraphics();
        g2.drawImage(tmp, 0, 0, null); // Draw the scaled image
        g2.dispose();
        return resized;
    }

    /**
     * Displays the solved puzzle in a new window.
     * This is the main entry point for viewing the puzzle after solving.
     */
    public static void displayPuzzle(Path piecesFolder, PuzzleSolver solver, PuzzleSolver.PuzzleResult result) {
        SwingUtilities.invokeLater(() -> {
            try {
                PuzzleImageViewer v = new PuzzleImageViewer(piecesFolder, solver, result);
                v.setVisible(true); // Show the window
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur lors de l'affichage: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
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

    /**
     * Main method for testing the viewer from the command line.
     * Expects the path to the folder containing puzzle pieces as an argument.
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
            PuzzleSolver solver = new PuzzleSolver(folder); // Solve the puzzle
            PuzzleSolver.PuzzleResult result = solver.solvePuzzle();
            displayPuzzle(folder, solver, result); // Display the result
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
