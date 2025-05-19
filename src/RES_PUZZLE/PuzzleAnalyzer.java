import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;


public class PuzzleAnalyzer {

    public enum Side { TOP, RIGHT, BOTTOM, LEFT } // on va supp
    private final EdgeResult[] results = new EdgeResult[4]; // ok
    private final PieceSave piece;
    private int sizeEdge;

    public PuzzleAnalyzer(String id, BufferedImage img) {


        // Le masque 
        boolean[][] mask = buildMask(img);
        // Le contour
        List<Point> contour = traceContour(mask);
        //System.out.println(contour.size());
        //On splite le contour
        this.sizeEdge = contour.size();
        List<List<Point>> edges = splitEdges(contour);
        //On prend chaque résult que on veut et on le stock

         // Calcul des results
        for (int i = 0; i < 4; i++) {
            results[i] = analyserBord(edges.get(i), Side.values()[i], img);
        }


        this.piece = new PieceSave(id, results.clone());
        // Debug : print de chaque résultat
        /** 
        System.out.println(id + " : ");
        for (Side side : Side.values()) {
            EdgeResult r = results[side.ordinal()];
            
            System.out.printf(
                "Side %s → type=%d, lengths=%s, colors=%s%n",
                side,
                r.type(),
                Arrays.toString(r.lengths()),
                Arrays.toString(r.colors())
            );
        }
        */
        

    }

    public PieceSave getPiece() {
        return piece;
    }

    public int getSizeEdge() {
        return sizeEdge;
    }
    
    //Pour creer le masque
    private static boolean[][] buildMask(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        boolean[][] mask = new boolean[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int alpha = (img.getRGB(x, y) >>> 24) & 0xFF;
                mask[y][x] = (alpha > 0);
            }
        }
        return mask;
    }

    //Pour tracer les contours
    private static List<Point> traceContour(boolean[][] mask) {
        int h = mask.length, w = mask[0].length;
        Point p0 = null;
        outer:
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (mask[y][x] && (x == 0 || !mask[y][x - 1])) {
                    p0 = new Point(x, y);
                    break outer;
                }
            }
        }
        if (p0 == null) return Collections.emptyList();

        int[] dx = { -1, -1, 0, 1, 1,  1,  0, -1 };
        int[] dy = {  0,  1, 1, 1, 0, -1, -1, -1 };

        Point c = new Point(p0);
        Point b = new Point(p0.x - 1, p0.y);
        List<Point> contour = new ArrayList<>();
        contour.add(new Point(c));

        while (true) {
            int bi = 0;
            for (int i = 0; i < 8; i++) {
                if (c.x + dx[i] == b.x && c.y + dy[i] == b.y) {
                    bi = i; break;
                }
            }
            Point nextC = null, nextB = null;
            int start = (bi + 1) & 7;
            for (int k = 0; k < 8; k++) {
                int idx = (start + k) & 7;
                int nx = c.x + dx[idx], ny = c.y + dy[idx];
                if (nx >= 0 && nx < w && ny >= 0 && ny < h && mask[ny][nx]) {
                    nextC = new Point(nx, ny);
                    nextB = new Point(c);
                    break;
                }
            }
            if (nextC == null || nextC.equals(p0)) break;
            b = nextB;
            c = nextC;
            contour.add(new Point(c));
        }
        return contour;
    }

    //Split les bords des contours
    private static List<List<Point>> splitEdges(List<Point> contour) {

        List<Point> top    = new ArrayList<>();
        List<Point> right  = new ArrayList<>();
        List<Point> bottom = new ArrayList<>();
        List<Point> left   = new ArrayList<>();
        
        Set<Point> contourSet = new HashSet<>(contour);
        Set<Point> coins = new HashSet<>();

    // Détermine le nombre de pixels à checker selon la taille du contour
    int size = contour.size();
    int checkDist;
    if (size >= 4000) {
        checkDist = 2;
    } else if (size >= 1000) {
        checkDist = 4;
    } else {
        checkDist = 10;
    }

    for (Point p : contour) {
        int x = p.x, y = p.y;
        // Flags pour la présence continue jusqu’à checkDist
        boolean allRight  = true, allLeft   = true;
        boolean allBottom = true, allTop    = true;
        // Flags pour l’absence sur la même distance
        boolean noRight   = true, noLeft    = true;
        boolean noBottom  = true, noTop     = true;

        for (int i = 1; i <= checkDist; i++) {
            if (!contourSet.contains(new Point(x + i, y))) allRight  = false;
            if (!contourSet.contains(new Point(x - i, y))) allLeft   = false;
            if (!contourSet.contains(new Point(x, y + i))) allBottom = false;
            if (!contourSet.contains(new Point(x, y - i))) allTop    = false;

            if (contourSet.contains(new Point(x + i, y))) noRight   = false;
            if (contourSet.contains(new Point(x - i, y))) noLeft    = false;
            if (contourSet.contains(new Point(x, y + i))) noBottom  = false;
            if (contourSet.contains(new Point(x, y - i))) noTop     = false;
        }

        // Si on a checkDist pixels pleins dans deux directions adjacentes
        // et aucun pixel dans les deux autres directions
        if (allRight  && allBottom && noLeft  && noTop){coins.add(p);}    
        else if (allLeft   && allBottom && noRight && noTop){coins.add(p);}    
        else if (allLeft   && allTop    && noRight && noBottom) coins.add(p);
        else if (allRight  && allTop    && noLeft  && noBottom) coins.add(p);
    }


        List<Point> currentEdge = top;
        int edgeIndex = 0;

        for (Point p : contour) {
            if (coins.contains(p)) {
                edgeIndex = (edgeIndex + 1) % 4;
                switch (edgeIndex) {
                    case 0 -> currentEdge = top;
                    case 1 -> currentEdge = left;
                    case 2 -> currentEdge = bottom;
                    case 3 -> currentEdge = right;
                }
            }
            currentEdge.add(p);
        }
        top.sort(Comparator.comparingInt(p -> p.x));
        right.sort(Comparator.comparingInt(p -> p.y));
        bottom.sort(Comparator.comparingInt((Point p) -> p.x).reversed());
        left.sort(Comparator.comparingInt((Point p) -> p.y).reversed());

        return List.of(top, right, bottom, left);
    }


    private EdgeResult analyserBord(List<Point> edge, Side side, BufferedImage img) {

        int[] lengths = new int[3];
        int[] rgbSums = new int[3];
        int[] rgbMeans = new int[3];

        if (edge.isEmpty()) {
            return new EdgeResult(2, lengths, new int[3]);
        }

        int axe = (side == Side.TOP || side == Side.BOTTOM) ? 1 : 0;
        int size = edge.size();

        // -------- PARTIE 1 : depuis le début --------
        int ref;
        if (axe == 1) {
            ref = edge.get(0).y;
        } else {
            ref = edge.get(0).x;
        }
        int i = 0;

        while (i < size) {
            Point p = edge.get(i);
            int v = (axe == 1) ? p.y : p.x;
            if (v != ref) break;

            lengths[0]++;
            int rgb = img.getRGB(p.x, p.y);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            rgbSums[0] += (r + g + b) / 3;
            i++;
        }


        // -------- PARTIE 3 : depuis la fin --------
        int refEnd = (axe == 1) ? edge.get(size - 1).y : edge.get(size - 1).x;
        int j = 0;
        while (j < size - i) {
            Point p = edge.get(size - 1 - j);
            int v = (axe == 1) ? p.y : p.x;
            if (v != refEnd) break;

            lengths[2]++;
            int rgb = img.getRGB(p.x, p.y);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            rgbSums[2] += (r + g + b) / 3;
            j++;
        }

        // -------- PARTIE 2 : entre les deux --------
        int type = 2;
        for (int k = i; k < size - j; k++) {
            Point p = edge.get(k);
            lengths[1]++;
            int rgb = img.getRGB(p.x, p.y);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            rgbSums[1] += (r + g + b) / 3;

            if (type == 2 && k > i+2){
                int delta = (axe == 1) ? p.y - edge.get(0).y : p.x - edge.get(0).x;
                switch (side) {
                    case TOP:    type = (delta > 0) ? 0 : 1; break;
                    case BOTTOM: type = (delta > 0) ? 1 : 0; break;
                    case RIGHT:  type = (delta > 0) ? 1 : 0; break;
                    case LEFT:   type = (delta > 0) ? 0 : 1; break;
                }
            }
        }

        // -------- Moyennes RGB --------
        for (int k = 0; k < 3; k++) {
            if (lengths[k] == 0) {
                rgbMeans[k] = 0;
            } else {
                rgbMeans[k] = rgbSums[k] / lengths[k];
            }
        }

        return new EdgeResult(type, lengths, rgbMeans);
    }

    
    public static void main(String[] args) throws IOException {


        File    file1 = new File("/home/cytech/Desktop/tropical/tropical/50x35/img_22_10.png");
        BufferedImage img1 = ImageIO.read(file1);

        File    file2 = new File("/home/cytech/Desktop/tropical/tropical/50x35/img_23_10.png");
        BufferedImage img2 = ImageIO.read(file2);

        // on passe l'id (ici le nom de fichier) et l'image
        PuzzleAnalyzer analyzer1 = new PuzzleAnalyzer(file1.getName(), img1);
        PuzzleAnalyzer analyzer2 = new PuzzleAnalyzer(file2.getName(), img2);

        // on récupère l'objet léger qui contient les 4 EdgeResult
        PieceSave piece1 = analyzer1.getPiece();
        PieceSave piece2 = analyzer2.getPiece();
        

        // ou pour chaque côté :
        // Pour récupérer uniquement le bord TOP
        EdgeResult edge1 = piece1.edges()[1];
        EdgeResult edge2 = piece2.edges()[3];

        EdgeCompatibilityChecker checker = new EdgeCompatibilityChecker();
        // edge1 et edge2 sont deux instances de EdgeResult
        boolean ok = checker.areCompatible(edge1, edge2);

        System.out.println(ok);
    }   
    
}
