package ttt;

import Model.EdgeResult;

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
        int badCount = 0;
        for (int i = 0; i < 3; i++) {
            System.out.println(Math.abs(l1[i] - l2m[i]));
            if (Math.abs(l1[i] - l2m[i]) > 0) {
                badCount++;
                if (badCount > 1 || (Math.abs(l1[i] - l2m[i]) > 1)) {
                    // Deux couleurs hors tolérance = rejet
                    System.out.println("Erreur taille");
                    return false;
                }
            }
        }

        // 4) Couleurs : paliers « bruts » + 1 seul joker
        badCount = 0;
        for (int i = 0; i < 3; i++) {
            int len = l1[i];
            int tol;

            if (i == 1) {
                // Segment central 
                if (len <= 25)      tol = 7;
                else if (len <= 50) tol = 6;
                else if (len <= 100) tol = 5;
                else                 tol = 3;
            } else {
                // Segments 0 et 2
                if (len <= 25)       tol = 16;
                else if (len <= 50)  tol = 14;
                else if (len <= 100)  tol = 9;
                else if (len <= 200)  tol = 5;
                else                  tol = 4;
            }
            //System.out.println(len +" "+ tol);
            if (Math.abs(c1[i] - c2m[i]) > tol) {
                if (i==1) badCount +=2;
                else badCount++;
                if (badCount > 2) {
                    // Deux couleurs hors tolérance = rejet
                    System.out.println("Erreur couleur");
                    return false;
                }
            }

        }

        //forme du creux / bosse

        int[] nbrMaxPixel1 = e1.maxPixel();
        int[] nbrMaxPixel2 = e2.maxPixel();

        for (int i = 0; i < 3; i++) {
            if (nbrMaxPixel1[i] != nbrMaxPixel2[i]) {
                System.out.println("Erreur pixel");
                return false;
            }
        }

        // 5) Tout est OK si on n’a pas jeté deux erreurs
        return true;
    }
}
