/**
 * PuzzleSolver class is responsible for solving a jigsaw puzzle.
 * It analyzes edge compatibilities between pieces and assembles them accordingly.
 * It can detect the width and height based on the outer pieces.
 */
package Resolution_Puzzle;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.imageio.ImageIO;

import Model.EdgeResult;
import Model.PieceSave;
import Factory.ProgressListener;



/**
 * PuzzleSolver résout un puzzle jigsaw sans rotation des pièces.
 * Il détecte automatiquement la largeur (m) et la hauteur (n)
 * en parcourant la première ligne et la première colonne.
 * Le seul paramètre requis est le chemin vers le dossier contenant
 * toutes les images des pièces.
 */
public class PuzzleSolver {

    private final Path folder;
    private final EdgeCompatibilityChecker checker;
    private int tailleTop;
    private int tailleLeft;
    private List<Integer> decalageTop;
    private List<Integer> decalageLeft;

/**
 * Constructs a PuzzleSolver for the specified puzzle folder.
 *
 * @param folder path to the folder containing the puzzle pieces
 */
public PuzzleSolver(Path folder) {
        this.folder = folder;
        this.checker = new EdgeCompatibilityChecker();
        this.tailleLeft = 0;
        this.tailleTop = 0;
        this.decalageTop = new ArrayList<>();
        this.decalageLeft = new ArrayList<>();
    }

/**
 * @return list of vertical shifts (top edge) for each column
 */
public List<Integer> getDecalageTop()   { return decalageTop; }
/**
 * @return list of horizontal shifts (left edge) for each row
 */
public List<Integer> getDecalageLeft() { return decalageLeft; }

    // Getters pour la taille globale, si tu veux t’en servir
/**
 * Gets the top size of the puzzle layout.
 * @return number of cells at the top
 */
public int getTailleTop() { return tailleTop; }
/**
 * @return the total width of the final puzzle image
 */
public int getTailleLeft() { return tailleLeft; }

/**
 * Loads all puzzle pieces from disk and analyzes them.
 *
 * @return list of PieceSave containing analyzed pieces
 * @throws IOException if an error occurs during image reading
 */
public List<PieceSave> loadAllPieces() throws IOException {
        List<PieceSave> list = new ArrayList<>();
        try (var stream = Files.list(folder)) {
            for (Path path : (Iterable<Path>) stream::iterator) {
                if (!Files.isRegularFile(path)) continue;
                BufferedImage img = ImageIO.read(path.toFile());
                if (img == null) continue;
                String name = path.getFileName().toString();
                // Utiliser la bonne classe PuzzleAnalyzer
                PuzzleAnalyzer analyzer = new PuzzleAnalyzer(name, img);
                PieceSave piece = analyzer.getPiece();
                // Filtrer les éventuels retours nuls
                if (piece == null) continue;
                list.add(piece);
            }
        }
        return list;
    }

    /** Trouve la pièce coin (TOP & LEFT plats). */
    private PieceSave find_HL_corner(List<PieceSave> pieces) {
        for (PieceSave p : pieces) {
            if (p.edges()[Edge.TOP].type() == 2 && p.edges()[Edge.LEFT].type() == 2) {
                return p;
            }
        }
        throw new IllegalStateException("Aucun coin trouvé");
    }

    /** Trouve la pièce coin en bas à droite (BOTTOM & RIGHT plats). */
    private PieceSave find_BR_corner(List<PieceSave> pieces) {
        for (PieceSave p : pieces) {
            if (p.edges()[Edge.BOTTOM].type() == 2
            && p.edges()[Edge.RIGHT] .type() == 2) {
                return p;
            }
        }
        throw new IllegalStateException("Aucun coin bas-droite trouvé");
    }


    /** Retire la première pièce compatible ou renvoie null si aucune. */
    private PieceSave findAndRemove(List<PieceSave> pieces, Predicate<PieceSave> pred) {
        Iterator<PieceSave> it = pieces.iterator();
        while (it.hasNext()) {
            PieceSave p = it.next();
            if (pred.test(p)) {
                it.remove();
                return p;
            }
        }
        return null;
    }

/**
 * Attempts to solve the puzzle and return the result.
 *
 * @return PuzzleResult containing the solved matrix and unplaced pieces
 * @throws IOException if a piece cannot be loaded or analyzed
 */
public PuzzleResult solvePuzzle() throws IOException {
        List<PieceSave> pieces = loadAllPieces();

        
        tailleTop = 0;
        tailleLeft = 0;

        PieceSave corner = find_HL_corner(pieces); // corner en haut a gauche
        pieces.remove(corner);
        // On initialise le décalage
        tailleTop += corner.edges()[0].lengths()[0];
        decalageTop.add(corner.edges()[1].profondeur());

        tailleLeft += corner.edges()[3].lengths()[0];
        decalageLeft.add(corner.edges()[2].profondeur());

        // Découverte largeur m
        List<PieceSave> firstRow = new ArrayList<>();
        firstRow.add(corner);
        while (true) {
            // 1) On extrait l’EdgeResult « right » du dernier élément de firstRow
            EdgeResult rightEdge = firstRow
                .get(firstRow.size() - 1)
                .edges()[Edge.RIGHT];

            // 2) On cherche et on retire la première pièce compatible sur RIGHT–LEFT
            PieceSave next = findAndRemove(
                pieces,
                p -> checker.areCompatible(
                    rightEdge,
                    p.edges()[Edge.LEFT]
                )
                && p.edges()[Edge.TOP].type() == 2 // Bord top est plat
            );

            if (next == null || next.edges()[Edge.RIGHT].type() == 2) {
                if (next != null){ 
                    tailleTop += next.edges()[0].lengths()[0];
                    firstRow.add(next);
                }
                break;
            }

            tailleTop += next.edges()[0].lengths()[0];
            decalageTop.add(next.edges()[1].profondeur());

            firstRow.add(next);
        }
        int m = firstRow.size();

        // Découverte hauteur n
        List<PieceSave> firstCol = new ArrayList<>();
        firstCol.add(corner);
        while (true) {
            // 1) On extrait l’EdgeResult du dernier élément de firstCol
            EdgeResult bottomEdge = firstCol
                .get(firstCol.size() - 1)
                .edges()[Edge.BOTTOM];

            // 2) On cherche et on retire la première pièce compatible
            PieceSave next = findAndRemove(
                pieces,
                p -> checker.areCompatible(
                    bottomEdge,
                    p.edges()[Edge.TOP]
                )
                && p.edges()[Edge.LEFT].type() == 2
            );

            if (next == null || next.edges()[Edge.BOTTOM].type() == 2) {
                if (next != null){ 
                    firstCol.add(next);
                    tailleLeft += next.edges()[3].lengths()[0];
                }
                break;
            }

            tailleLeft += next.edges()[3].lengths()[0];
            decalageLeft.add(next.edges()[2].profondeur());

            firstCol.add(next);
        }


        //System.out.println(tailleTop);
        //System.out.println(decalageTop);
        //System.out.println(tailleLeft);
        //System.out.println(decalageLeft);
        int n = firstCol.size();


        // Initialisation
        PieceSave[][] grid = new PieceSave[n][m];
        for (int j=0; j<m; j++) grid[0][j] = firstRow.get(j);
        for (int i=0; i<n; i++) grid[i][0] = firstCol.get(i);
//barre de progression
        int totalSteps = (n - 1) * (m - 1); // nombre total d’emplacements à traiter
        int stepsDone = 0;
        // Remplissage intérieur en partant de en haut a gauche
        for (int i=1; i<n; i++) {
            for (int j=1; j<m; j++) {
                PieceSave top = grid[i-1][j];
                PieceSave left = grid[i][j-1];
                stepsDone++;
        if (progressListener != null) {
            double progress = (double) stepsDone / totalSteps * 0.5; // 50% max ici, pour la résolution
            progressListener.onProgress(progress);
        }
                // si un voisin est manquant, on saute
                if (top == null || left == null) {
                    //grid[i][j] = null;
                    continue;
                }
                
                PieceSave match = findAndRemove(pieces,
                    p -> checker.areCompatible(top.edges()[Edge.BOTTOM], p.edges()[Edge.TOP])
                        && checker.areCompatible(left.edges()[Edge.RIGHT], p.edges()[Edge.LEFT])
                );

                // 2) Si aucune trouvée, on se rabat sur la condition plus permissive
                
                if (match == null) {
                    match = findAndRemove(pieces,
                        p -> checker.areCompatible(top.edges()[Edge.BOTTOM], p.edges()[Edge.TOP])
                            || checker.areCompatible(left.edges()[Edge.RIGHT], p.edges()[Edge.LEFT])
                    );
                }
                
                if(match!=null){
                    grid[i][j] = match;
                }
                
            }
        }
        
        // Conversion en ids
        String[][] result = new String[n][m];
        for (int i=0; i<n; i++) {
            for (int j=0; j<m; j++) {
                result[i][j] = (grid[i][j] != null) ? grid[i][j].id() : "";
            }
        }

        // Liste des pièces restantes
        List<String> remainingIds = new ArrayList<>();
        for (PieceSave p : pieces) {
            remainingIds.add(p.id());
        }

        // Affichage automatique à chaque appel
        for (String[] row : result) {
            for (String id : row) {
                System.out.print(id + " ");
            }
            System.out.println();
        }
        System.out.println("Pièces non placées :");
        for (String id : remainingIds) {
            System.out.print(id + " ");
        }
        System.out.println();


        return new PuzzleResult(result, remainingIds);
    }

    private static class Edge {
        static final int TOP=0, RIGHT=1, BOTTOM=2, LEFT=3;
    }

    /**
     * Résultat de la résolution : matrice d'IDs et liste des pièces non placées.
     */
    public static class PuzzleResult {
        private final String[][] matrix;
        private final List<String> remainingIds;

/**
 * Creates a new result of the puzzle solving process.
 *
 * @param matrix the solved puzzle matrix (IDs of placed pieces)
 * @param remainingIds list of piece IDs that could not be placed
 */
public PuzzleResult(String[][] matrix, List<String> remainingIds) {

            this.matrix = matrix;
            this.remainingIds = remainingIds;
        }

/**
 * @return matrix representing the final placement of all pieces
 */
public String[][] getMatrix() {
            return matrix;
        }

/**
 * @return list of piece IDs that could not be placed
 */
public List<String> getRemainingIds() {
            return remainingIds;
        }
    }

/**
 * Main method for running the puzzle solver from command line.
 *
 * @param args command-line arguments, expects a folder path as the first argument
 */
public static void main(String[] args) {
    if (args.length < 1) {
        System.err.println("Usage: java PuzzleSolver <dossier>");
        System.exit(1);
    }
    try {
        Path folder = Path.of(args[0]);
        if (!Files.isDirectory(folder)) {
            System.err.println("Répertoire invalide: " + folder);
            System.exit(2);
        }
        PuzzleResult res = new PuzzleSolver(folder).solvePuzzle();
        String[][] matrix = res.getMatrix();
        List<String> leftovers = res.getRemainingIds();
        // Affichage de la matrice
        for (String[] row : matrix) {
            for (String id : row) System.out.print(id + " ");
            System.out.println();
        }
        // Affichage des pièces restantes
        System.out.println("Pièces non placées :");
        for (String id : leftovers) System.out.print(id + " ");
        System.out.println();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
/**
 * Interface to track puzzle solving progress.
 * This is used to update the UI or logs with the current progress.
 */
public interface ProgressListener {
    /**
     * Called with a value between 0.0 and 1.0 to report current progress.
     *
     * @param progress progress percentage (0.0 to 1.0)
     */
    void onProgress(double progress);
}


private ProgressListener progressListener;

/**
 * Sets the listener used to track puzzle solving progress.
 *
 * @param listener the ProgressListener to be notified
 */
public void setProgressListener(ProgressListener listener) {
    this.progressListener = listener;
}

}

//Si en partant de en haut a gauche il reste des possibilité, on part de en bas a droite et on teste de faire pareil.