package CY_PUZZLE; // Declares the package where this class belongs, ensuring proper organization of the project.

import Factory.Home; // Imports the Home class from the Factory package. This class is responsible for launching the GUI.

/**
 * Main launcher for the CY-Puzzle application.
 * This class serves as the entry point for the program.
 */
public class Main {

    /**
     * The main method is the starting point of the application.
     * It launches the JavaFX application by calling the `Home` class.
     *
     * @param args Command-line arguments (not used in this case).
     */
    public static void main(String[] args) {
        // Calls the main method of the Home class, which extends Application.
        // This effectively starts the JavaFX GUI for the puzzle application.
        Home.main(args);
    }
}
