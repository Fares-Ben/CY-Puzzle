/**
 * Home initializes and displays the main window of the application.
 * It serves as the entry GUI container with layout and navigation.
 */
package Factory;

import java.awt.image.BufferedImage;

import javafx.application.Application; // Base class for JavaFX applications
import javafx.geometry.Insets; // Used for padding and spacing
import javafx.scene.Scene; // Represents the main container for the GUI
import javafx.scene.control.Label; // Displays text in the GUI
import javafx.scene.control.ScrollPane; // Allows scrolling for large content
import javafx.scene.control.TextArea; // Displays a multi-line text area
import javafx.scene.image.ImageView; // Displays images in the GUI
import javafx.scene.layout.GridPane; // A grid-based layout for organizing components
import javafx.scene.layout.HBox; // A horizontal box layout
import javafx.scene.layout.Priority; // Defines resizing behavior for components
import javafx.scene.layout.VBox; // A vertical box layout
import javafx.stage.Stage; // Represents the main application window

/**
 * JavaFX main GUI container. Displays the interface to load, solve, and show puzzles.
 */
public class Home extends Application {

    /** Stores the last assembled image of the puzzle. */
    public static BufferedImage derniereImageAssemblee = null;

    // Constants for the main window dimensions
    private final int FRAME_WIDTH = 1200; // Width of the main window
    private final int FRAME_HEIGHT = 800; // Height of the main window
    private final int GRID_SIZE = 4; // Default size of the grid (4x4)

    // Labels to display the number of pieces and the timer
    private Label pieceLabel, timerLabel;

    /** GridPane that holds the GUI layout of puzzle visualization. */
    public static GridPane gridPane;

    /** Displays a list of the pieces currently loaded or placed. */
    public static TextArea piecesListArea;

    /** Displays the puzzle after assembly in the GUI. */
    public static ImageView fusionImageView;

    /**
     * The `start` method is the entry point for the JavaFX application.
     * It initializes and sets up the main GUI components.
     *
     * @param primaryStage The main application window
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize labels for displaying the number of pieces and the timer
        pieceLabel = LabelFactory.createLabel("Pièces :", 18);
        timerLabel = LabelFactory.createLabel("Timer :", 18);

        // Create the sidebar panel with buttons and controls
        VBox sideBarPanel = SideBarFactory.createSideBarPanel(pieceLabel, timerLabel);

        // Initialize the grid pane for displaying the puzzle pieces
        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: white;"); // Set the background color to white
        gridPane.setPadding(new Insets(0)); // No padding around the grid
        gridPane.setHgap(0); // No horizontal gaps between cells
        gridPane.setVgap(0); // No vertical gaps between cells

        // Populate the grid with empty labels (placeholders)
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridPane.add(new Label(" "), j, i);
            }
        }

        // Create a text area to display the list of puzzle pieces
        piecesListArea = new TextArea();
        piecesListArea.setEditable(false); // Make the text area read-only
        piecesListArea.setWrapText(true); // Enable text wrapping
        piecesListArea.setPrefHeight(150); // Set the preferred height
        piecesListArea.setStyle("-fx-font-family: monospace;"); // Use a monospace font for better alignment

        // Create an ImageView to display the assembled puzzle
        fusionImageView = new ImageView();
        fusionImageView.setFitWidth(600); // Set the maximum width
        fusionImageView.setPreserveRatio(true); // Maintain the aspect ratio
        fusionImageView.setSmooth(true); // Enable smooth scaling
        fusionImageView.setStyle("-fx-border-color: gray; -fx-border-width: 1;"); // Add a border for better visibility

        // Create a vertical box to hold the ImageView, grid, and text area
        VBox rightPane = new VBox(10, fusionImageView, gridPane, piecesListArea);
        rightPane.setPadding(new Insets(10)); // Add padding around the content
        rightPane.setFillWidth(true); // Allow the content to fill the width

        // Add the right pane to a scrollable container
        ScrollPane scrollPane = new ScrollPane(rightPane);
        scrollPane.setFitToWidth(true); // Fit the content to the width of the scroll pane
        scrollPane.setFitToHeight(true); // Fit the content to the height of the scroll pane
        scrollPane.setStyle("-fx-background: #f0f0f0; -fx-padding: 0;"); // Set the background color and padding

        // Create a horizontal box to hold the sidebar and the scrollable content
        HBox root = new HBox();
        root.getChildren().addAll(sideBarPanel, scrollPane);
        HBox.setHgrow(scrollPane, Priority.ALWAYS); // Allow the scroll pane to grow horizontally

        // Create the main scene with the specified dimensions
        Scene scene = new Scene(root, FRAME_WIDTH, FRAME_HEIGHT);
        primaryStage.setTitle("CY-PUZZLE"); // Set the title of the window
        primaryStage.setScene(scene); // Set the scene for the primary stage
        primaryStage.show(); // Display the window
    }

    /**
     * Main method to launch the JavaFX application manually.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
