/**
 * Represents the result of analyzing an edge of a puzzle piece.
 * This includes the type of edge, its lengths, colors, depth, and pixel data.
 */
package Model;

public record EdgeResult(
    int type,        // The type of the edge: 0 = tenon (protrusion), 1 = mortaise (indentation), 2 = flat edge
    int[] lengths,   // Array storing lengths of different segments of the edge (e.g., flat start, notch, flat end)
    int[] colors,    // Average color values for each segment of the edge, used for compatibility checks
    int profondeur,  // Depth of the edge (e.g., how deep the mortaise or tenon is)
    int[] maxPixel   // Array storing max pixel values for specific edge features (e.g., corners or notches)
) {}
