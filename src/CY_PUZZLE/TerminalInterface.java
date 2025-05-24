package CY_PUZZLE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import Resolution_Puzzle.PuzzleSolver; 
public class TerminalInterface {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Interface Terminal Puzzle Solver ===");
        System.out.print("Entrez le chemin du dossier contenant les pièces : ");
        String chemin = scanner.nextLine();

        Path dossier = Path.of(chemin);
        if (!Files.exists(dossier) || !Files.isDirectory(dossier)) {
            System.out.println("❌ Chemin invalide. Vérifiez que le dossier existe.");
            return;
        }

        try {
            // Appel direct à ta classe existante PuzzleSolver
            PuzzleSolver solver = new PuzzleSolver(dossier);
            solver.solvePuzzle(); // ou une autre méthode, selon ton implémentation
            System.out.println("✅ Puzzle résolu avec succès !");
        } catch (Exception e) {
            System.out.println("❌ Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
