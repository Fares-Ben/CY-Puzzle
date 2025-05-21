package Res_Puzzle;

public record EdgeResult(
    int type,        // 0 = tenon, 1 = mortaise, 2 = bord plat
    int[] lengths,   // {longPlatDébut, longEncoche, longPlatFin}
    int[] colors  // moyenne de couleur sur chaque segment
) {}
