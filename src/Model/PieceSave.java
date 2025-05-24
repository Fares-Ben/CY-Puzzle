/**
 * PieceSave stores information about a puzzle piece placement,
 * including position and orientation in the final puzzle.
 */
package Model;

/**
 * Record representing a saved puzzle piece's ID, orientation, and coordinates in the grid.
 */
public record PieceSave(

    String id,            // ex. "img_1_1.png" ou tout autre identifiant unique
    EdgeResult[] edges,    // le tableau de 4 résultats (TOP, RIGHT, BOTTOM, LEFT)
    int[] coins
) {}