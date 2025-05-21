public class EdgeCompatibilityChecker {
    public boolean areCompatible(EdgeResult e1, EdgeResult e2) {
        // 1) Types : on exclut les plats et on impose tenon/mortise
        if (e1.type() == 2 || e2.type() == 2) return false;
        boolean tenonMortise = (e1.type() == 0 && e2.type() == 1)
                           || (e1.type() == 1 && e2.type() == 0);
        if (!tenonMortise) return false;

        // 2) Récupération et mapping
        int[] l1 = e1.lengths(), l2 = e2.lengths();
        int[] c1 = e1.colors(),  c2 = e2.colors();
        int[] l2m = { l2[2], l2[1], l2[0] };
        int[] c2m = { c2[2], c2[1], c2[0] };

        // 3) Longueurs strictement égales par segment
        for (int i = 0; i < 3; i++) {
            if (l1[i] != l2m[i]) {
                return false;
            }
        }

        // 4) Couleurs : paliers « bruts » + 1 seul joker
        int badCount = 0;
        for (int i = 0; i < 3; i++) {
            int len = l1[i];
            int tol;

            if (i == 1) {
                // Segment central 
                if (len <= 75)      tol = 7;
                else if (len <= 150) tol = 5;
                else if (len <= 300) tol = 3;
                else                 tol = 2;
            } else {
                // Segments 0 et 2
                if (len <= 75)       tol = 16;
                else if (len <= 150)  tol = 10;
                else if (len <= 300)  tol = 7;
                else if (len <= 600)  tol = 6;
                else                  tol = 4;
            }

            if (Math.abs(c1[i] - c2m[i]) > tol) {
                badCount++;
                if (badCount > 1) {
                    // Deux couleurs hors tolérance = rejet
                    return false;
                }
            }
        }

        // 5) Tout est OK si on n’a pas jeté deux erreurs
        return true;
    }
}
