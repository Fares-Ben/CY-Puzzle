public class EdgeCompatibilityChecker {
    private int colorTolerance;
    private int lengthTolerance;


    public EdgeCompatibilityChecker() {
        this.colorTolerance = 4;
        this.lengthTolerance = 3;

         // en fonction de la taille de l'image
    }

    public EdgeCompatibilityChecker(int colorTolerance, int lengthTolerance) {
        this.colorTolerance = colorTolerance;
        this.lengthTolerance = lengthTolerance;
    }

    public int getColorTolerance() {
        return colorTolerance;
    }

    public void setColorTolerance(int colorTolerance) {
        this.colorTolerance = colorTolerance;
    }

    public int getLengthTolerance() {
        return lengthTolerance;
    }

    public void setLengthTolerance(int lengthTolerance) {
        this.lengthTolerance = lengthTolerance;
    }

    
    public boolean areCompatible(EdgeResult e1, EdgeResult e2) {
        // Ignore flat edges
        if (e1.type() == 2 || e2.type() == 2) {
            return false;
        }

        // Check tenon/mortise pair
        boolean tenonMortise = (e1.type() == 0 && e2.type() == 1) || (e1.type() == 1 && e2.type() == 0);
        if (!tenonMortise) {
            return false;
        }

        int[] l1 = e1.lengths();
        int[] l2 = e2.lengths();
        int[] c1 = e1.colors();
        int[] c2 = e2.colors();

        // Map segments: index 0 <-> 2, 1 <-> 1, 2 <-> 0
        int[] l2Mapped = { l2[2], l2[1], l2[0] };
        int[] c2Mapped = { c2[2], c2[1], c2[0] };

        int size = l1[0] + l1[1] + l1[2];
        if (size >= 250 && size <= 500){
            setColorTolerance(4);
            setLengthTolerance(3);
            
        }
        else if(size <= 250){
            setColorTolerance(4);
            setLengthTolerance(3);
        }
        else{
            setColorTolerance(10);
            setLengthTolerance(5);
        }

        int count = 0;
        // Compare each segment within tolerances
        for (int i = 0; i < 3; i++) {
            if (Math.abs(l1[i] - l2Mapped[i]) < lengthTolerance) {
                count+=1;
            }
            if (Math.abs(c1[i] - c2Mapped[i]) < colorTolerance) {
                count+=1;
            }
        }

        // Check total length equality
        int sum1 = l1[0] + l1[1] + l1[2];
        int sum2 = l2Mapped[0] + l2Mapped[1] + l2Mapped[2];
        
        if (Math.abs(sum1 - sum2) > 0) {
            return false;
        }
        
        if (count >= 5){
            return true;
        }
        else{
            return false;
        }
    }
}
