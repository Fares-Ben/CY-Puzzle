package CY_PUZZLE; // Declares the package where this class belongs, ensuring proper organization of the project.

import Resolution_Puzzle.PuzzleSolver; // Imports the PuzzleSolver class, which is responsible for solving the puzzle.
import java.nio.file.Files; // Provides utility methods to work with files and directories.
import java.nio.file.Path; // Represents file and directory paths in a platform-independent way.
import java.util.Scanner; // Allows reading user input from the terminal;

/**
 * TerminalInterface provides a command-line interface for solving puzzles.
 * It allows the user to input the path to the folder containing puzzle pieces
 * and uses the PuzzleSolver class to solve the puzzle.
 */
public class TerminalInterface {

    public static void main(String[] args) {
        // Create a Scanner object to read user input from the terminal.
        Scanner scanner = new Scanner(System.in);

        // Display a welcome message for the terminal interface.
        System.out.println("=== Interface Terminal Puzzle Solver ===");

        // Prompt the user to enter the path to the folder containing puzzle pieces.
        System.out.print("Entrez le chemin du dossier contenant les pièces : ");
        String chemin = scanner.nextLine(); // Read the user's input as a string.

        // Convert the input string into a Path object.
        Path dossier = Path.of(chemin);

        // Check if the provided path exists and is a directory.
        if (!Files.exists(dossier) || !Files.isDirectory(dossier)) {
            // If the path is invalid, display an error message and exit the program.
            System.out.println("❌ Chemin invalide. Vérifiez que le dossier existe.");
            return; // Exit the program.
        }

        try {
            // Create an instance of PuzzleSolver with the provided folder path.
            PuzzleSolver solver = new PuzzleSolver(dossier);

            // Call the solvePuzzle method to solve the puzzle.
            // This method analyzes the pieces and assembles them into a solution.
            solver.solvePuzzle();

            // If the puzzle is solved successfully, display a success message.
            System.out.println("✅ Puzzle résolu avec succès !");
        } catch (Exception e) {
            // If an error occurs during the puzzle-solving process, display an error message.
            System.out.println("❌ Une erreur est survenue : " + e.getMessage());

            // Print the stack trace of the exception for debugging purposes.
            e.printStackTrace();
        }
    }
}
