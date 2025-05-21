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
        
    }

    public PieceSave getPiece() {
        return piece;
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
    // 1) p0 : premier pixel actif dont la gauche est vide
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

    int[] dx = { -1, 0, 1,  0 };
    int[] dy = {  0, 1, 0, -1 };
    Point c = new Point(p0);
    Point b = new Point(p0.x - 1, p0.y);

    List<Point> contour = new ArrayList<>();
    // p0 est forcément un bord “gauche”, donc déjà exposé :
    contour.add(new Point(c));

    while (true) {
        // 3) d’où on vient ?
        int bi = 0;
        for (int i = 0; i < 4; i++) {
            if (c.x + dx[i] == b.x && c.y + dy[i] == b.y) {
                bi = i;
                break;
            }
        }

        // 4) chercher nextC (sans touchesVoid ici !)
        Point nextC = null, nextB = null;
        int start = (bi + 1) & 3;
        for (int k = 0; k < 4; k++) {
            int idx = (start + k) & 3;
            int nx = c.x + dx[idx], ny = c.y + dy[idx];
            if (nx >= 0 && nx < w && ny >= 0 && ny < h
                && mask[ny][nx]) {
                nextC = new Point(nx, ny);
                nextB = new Point(c);
                break;
            }
        }

        if (nextC == null || nextC.equals(p0)) break;

        // 6) Avancer et n’ajouter que si c touche vraiment le vide
        b = nextB;
        c = nextC;
        if (touchesVoid(mask, c.x, c.y)) {
            contour.add(new Point(c));
        }
    }

    return contour;
}

/** true si (x,y) a un bord N, S, E ou O exposé au “vide” */
private static boolean touchesVoid(boolean[][] mask, int x, int y) {
    int h = mask.length, w = mask[0].length;
    if (x == 0            || !mask[y][x - 1]) return true;
    if (x == w - 1        || !mask[y][x + 1]) return true;
    if (y == 0            || !mask[y - 1][x]) return true;
    if (y == h - 1        || !mask[y + 1][x]) return true;
    return false;
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
                // 1) on ajoute p dans l'arête qui se termine
                currentEdge.add(p);

                // 2) on passe à l'arête suivante
                edgeIndex = (edgeIndex + 1) % 4;
                switch (edgeIndex) {
                    case 0 -> currentEdge = top;
                    case 1 -> currentEdge = left;
                    case 2 -> currentEdge = bottom;
                    case 3 -> currentEdge = right;
                }

                // 3) on ajoute p dans la nouvelle arête
                currentEdge.add(p);
                continue;
            }
            // sinon on est toujours sur la même arête
            currentEdge.add(p);
        }
        top.sort(Comparator.comparingInt(p -> p.x));
        right.sort(Comparator.comparingInt(p -> p.y));
        bottom.sort(Comparator.comparingInt((Point p) -> p.x).reversed());
        left.sort(Comparator.comparingInt((Point p) -> p.y).reversed());

        return List.of(top, right, bottom, left);
    }


    
    //Algo 
    private EdgeResult analyserBord(List<Point> edge, Side side, BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        int[] lengths = new int[3];
        int[] rgbSums = new int[3];
        int[] rgbMeans = new int[3];

        boolean[][] isEdge = new boolean[h][w];
        if (edge.isEmpty()) {
            return new EdgeResult(2, lengths, new int[3]);
        }
        for (Point p : edge){

            if (p.y >= 0 && p.y < h && p.x >= 0 && p.x < w) {
                        isEdge[p.y][p.x] = true;
                    }
        }
        // Axe de mesure : 1 pour Y constant (TOP/BOTTOM), 0 pour X constant (LEFT/RIGHT)
        int axe = (side == Side.TOP || side == Side.BOTTOM) ? 1 : 0;

        // Détermination de la direction du parcours du bord
        int dx = 0, dy = 0;
        switch (side) {
            case TOP:    dx =  1; dy =  0; break;
            case RIGHT:  dx =  0; dy =  1; break;
            case BOTTOM: dx = -1; dy =  0; break;
            case LEFT:   dx =  0; dy = -1; break;
        }

        // Ensemble ordonné des points restants
        Set<Point> remaining = new LinkedHashSet<>(edge);

        // -------- PARTIE 1 : depuis le début --------
        Point start = edge.get(0);
        int x = start.x, y = start.y;
        while (true) {
            Point p = new Point(x, y);
            if (!remaining.contains(p)) break;
            lengths[0]++;
            int rgb = img.getRGB(x, y);
            rgbSums[0] += (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            remaining.remove(p);
            x += dx;
            y += dy;
        }

        // -------- PARTIE 3 : depuis la fin --------
        Point end = edge.get(edge.size() - 1);
        x = end.x;
        y = end.y;
        Point origine = edge.get(edge.size() - 1);
        while (true) {
            Point p = new Point(x, y);
            if (!remaining.contains(p)) break;
            lengths[2]++;
            int rgb = img.getRGB(x, y);
            rgbSums[2] += (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            remaining.remove(p);
            x -= dx;
            y -= dy;
        }

        // -------- PARTIE 2 : points restants --------
        List<Point> middle = new ArrayList<>(remaining);
        int type = 2;
        

        if (middle.size() > 1) {
            
            Point second  = middle.get(1);
            int delta = (axe == 1)
                ? (second.y - origine.y)
                : (origine.x - second.x);

            switch (side) {
                case TOP:    type = (delta > 0) ? 0 : 1; break;
                case BOTTOM: type = (delta > 0) ? 1 : 0; break;
                case RIGHT:  type = (delta > 0) ? 0 : 1; break;
                case LEFT:   type = (delta > 0) ? 1 : 0; break;
            }

            for (Point p : middle) {
                lengths[1]++;
                int rgb = img.getRGB(p.x, p.y);
                rgbSums[1] += (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;

                //System.out.printf("x:%d y:%d |", p.x, p.y);
            }
        }
        /** 
        // Affichage 0/1 pour debug de la partie centrale
        for (int yy = 0; yy < h; yy++) {
            for (int xx = 0; xx < w; xx++) {
                System.out.print(isEdge[yy][xx] ? '1' : '0');
            }
            System.out.println();
        }
        */
        // -------- Calcul des moyennes RGB --------
        for (int k = 0; k < 3; k++) {
            rgbMeans[k] = (lengths[k] == 0) ? 0 : (rgbSums[k] / lengths[k]);
        }

        return new EdgeResult(type, lengths, rgbMeans);
    }

    
    public static void main(String[] args) throws IOException {


        File    file1 = new File("/home/cytech/Desktop/cat/25x16/img_2_0.png");
        BufferedImage img1 = ImageIO.read(file1);

        File    file2 = new File("/home/cytech/Desktop/cat/25x16/img_5_0.png");
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
