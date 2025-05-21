public record PieceSave(
    String id,            // ex. "img_1_1.png" ou tout autre identifiant unique
    EdgeResult[] edges    // le tableau de 4 résultats (TOP, RIGHT, BOTTOM, LEFT)
) {}