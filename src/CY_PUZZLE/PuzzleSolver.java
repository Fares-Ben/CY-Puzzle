package CY_PUZZLE;

import java.util.*;

public class PuzzleSolver {

    private List<PuzzlePiece> pieces;
    private PuzzlePiece[][] tab;
    private int rows;
    private int cols;

    public PuzzleSolver(List<PuzzlePiece> pieces) {
        this.pieces = pieces;
    }

    // méthode qui résoud le puzzle
    public PuzzlePiece[][] solve() {
        if (pieces.isEmpty()) {
            System.out.println("Aucune pièce à résoudre.");
            return null;
        }

        estimetaille();

        tab = new PuzzlePiece[rows][cols];

        tab[0][0] = pieces.get(0); // ON place une piece en haut a gauche pour commencer

        System.out.println("Début de la résolution...");
        
        randomPieces(); // on appelle

        System.out.println("Résolution terminée (version de démo).");

        return tab;
    }


    // méthode qui résoud aléatoirement faudra faire l'algo ici
    private void randomPieces() {
        List<PuzzlePiece> reste = new ArrayList<>(pieces);
        reste.remove(tab[0][0]); // déjà utilisée

        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 && c == 0) continue;
                if (index >= reste.size()) return;
                tab[r][c] = reste.get(index++);
            }
        }
    }

    // Quand on va faire l'algo qui fait le bord ce probème va etre régler la c'est pour que ca fasse pas bug le tableau.
    private void estimetaille() {
        int total = pieces.size();
        this.rows = (int) Math.sqrt(total);
        this.cols = (total + rows - 1) / rows; // arrondi vers le haut
        System.out.println("Taille estimée : " + rows + " x " + cols);
    }
    

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
