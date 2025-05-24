/**
 * EdgeResult represents the result of comparing two edges.
 * It contains the score and direction of a potential match.
 */
package Model;
/**
 * Represents the result of an edge extraction process on a puzzle piece.
 * Stores the edge contour data and orientation for matching.
 */
public record EdgeResult(

    int type,        // 0 = tenon, 1 = mortaise, 2 = bord plat
    int[] lengths,   // {longPlatDébut, longEncoche, longPlatFin}
    int[] colors,  // moyenne de couleur sur chaque segment
    int profondeur,
    int[] maxPixel
) {}
