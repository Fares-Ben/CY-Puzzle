import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.imageio.ImageIO;

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

    public PuzzleSolver(Path folder) {
        this.folder = folder;
        this.checker = new EdgeCompatibilityChecker();
        this.tailleLeft = 0;
        this.tailleTop = 0;
        this.decalageTop = new ArrayList<>();
        this.decalageLeft = new ArrayList<>();
    }

    // Getters pour les décalages
    public List<Integer> getDecalageTop()   { return decalageTop; }
    public List<Integer> getDecalageLeft()  { return decalageLeft; }

    // Getters pour la taille globale, si tu veux t’en servir
    public int getTailleTop()  { return tailleTop; }
    public int getTailleLeft() { return tailleLeft; }

    /**
     * Charge toutes les images du dossier et analyse chaque pièce.
     * Instancie PuzzleAnalyzer puis récupère le PieceSave.
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
     * Résout le puzzle et renvoie une matrice des ids.
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


        System.out.println(tailleTop);
        System.out.println(decalageTop);
        System.out.println(tailleLeft);
        System.out.println(decalageLeft);
        int n = firstCol.size();

        // Initialisation
        PieceSave[][] grid = new PieceSave[n][m];
        for (int j=0; j<m; j++) grid[0][j] = firstRow.get(j);
        for (int i=0; i<n; i++) grid[i][0] = firstCol.get(i);

        // Remplissage intérieur en partant de en haut a gauche
        for (int i=1; i<n; i++) {
            for (int j=1; j<m; j++) {
                PieceSave top = grid[i-1][j];
                PieceSave left = grid[i][j-1];
                
                // si un voisin est manquant, on saute
                if (top == null || left == null) {
                    //grid[i][j] = null;
                    continue;
                }
                

                // On teste chaque pièce restante
                /** 
                for (PieceSave p : pieces) {
                    EdgeResult topC  = p.edges()[Edge.TOP];
                    EdgeResult leftC = p.edges()[Edge.LEFT];

                    System.out.println("  * Candidate " + p.id());
                    System.out.println("      topC.type=" + topC.type()
                        + " lengths=" + Arrays.toString(topC.lengths())
                        + " colors="  + Arrays.toString(topC.colors()));
                    System.out.println("      leftC.type=" + leftC.type()
                        + " lengths=" + Arrays.toString(leftC.lengths())
                        + " colors="  + Arrays.toString(leftC.colors()));

                    boolean compTop  = checker.areCompatible(neededBottom, topC);
                    boolean compLeft = checker.areCompatible(neededRight,  leftC);
                    System.out.println("      → compTop="  + compTop
                        + "  compLeft=" + compLeft);
                }
                */
                /** 
                PieceSave match = null;
                if (top != null && left != null) {
                match = findAndRemove(pieces,
                    p -> checker.areCompatible(top.edges()[BOTTOM], p.edges()[TOP])
                    && checker.areCompatible(left.edges()[RIGHT], p.edges()[LEFT])
                );
                }
                else if (top != null) {
                match = findAndRemove(pieces,
                    p -> checker.areCompatible(top.edges()[BOTTOM], p.edges()[TOP])
                );
                }
                else if (left != null) {
                match = findAndRemove(pieces,
                    p -> checker.areCompatible(left.edges()[RIGHT], p.edges()[LEFT])
                );
                }
                */
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
        /** 
        // On part de en bas a gauche 
        if(pieces.size() != 0){

        //Récupe le coin en bas a droite
        PieceSave corner_BR = find_BR_corner(pieces); // corner en haut a gauche
        pieces.remove(corner_BR);

        grid[n-1][m-1] = corner_BR;

        // Remplissage intérieur en partant de en bas à droite
        for (int i = n - 2; i >= 0; i--) {
            for (int j = m - 2; j >= 0; j--) {
                // 0) Si déjà rempli en première passe, on ne fait rien
                if (grid[i][j] != null) continue;

                PieceSave bottom = grid[i + 1][j];
                PieceSave right  = grid[i][j + 1];
                // si un voisin est manquant, on ne peut pas placer ici
                if (bottom == null || right == null) {
                    continue;
                }
                // 1) on cherche une pièce parfaite
                PieceSave match = findAndRemove(pieces,
                    p -> checker.areCompatible(bottom.edges()[Edge.TOP],    p.edges()[Edge.BOTTOM])
                        && checker.areCompatible(right .edges()[Edge.LEFT],  p.edges()[Edge.RIGHT])
                );
                // 2) sinon, on accepte un accrochage partiel
                if (match == null) {
                    match = findAndRemove(pieces,
                        p -> checker.areCompatible(bottom.edges()[Edge.TOP],    p.edges()[Edge.BOTTOM])
                            || checker.areCompatible(right .edges()[Edge.LEFT],  p.edges()[Edge.RIGHT])
                    );
                }
                if (match != null) {
                    grid[i][j] = match;
                }
            }
        }
         
        }
        */
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

        public PuzzleResult(String[][] matrix, List<String> remainingIds) {
            this.matrix = matrix;
            this.remainingIds = remainingIds;
        }

        public String[][] getMatrix() {
            return matrix;
        }

        public List<String> getRemainingIds() {
            return remainingIds;
        }
    }

    /** Test console. */
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
}


//Si en partant de en haut a gauche il reste des possibilité, on part de en bas a droite et on teste de faire pareil.