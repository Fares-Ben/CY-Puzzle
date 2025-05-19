package CY_PUZZLE;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzlePiece2{

    public record ContourPixel(int x, int y, byte brightness) {}

    private List<ContourPixel> topEdge = new ArrayList<>();
    private List<ContourPixel> bottomEdge = new ArrayList<>();
    private List<ContourPixel> leftEdge = new ArrayList<>();
    private List<ContourPixel> rightEdge = new ArrayList<>();

    public void extractEdgeContours(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int rgb = img.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                if (alpha == 0) continue;

                boolean isEdge = false;
                int[][] offsets = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
                for (int[] offset : offsets) {
                    int nx = x + offset[0];
                    int ny = y + offset[1];
                    int neighborAlpha = (img.getRGB(nx, ny) >> 24) & 0xff;
                    if (neighborAlpha == 0) {
                        isEdge = true;
                        break;
                    }
                }

                if (isEdge) {
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    int brightness = (r + g + b) / 3;
                    ContourPixel pixel = new ContourPixel(x, y, (byte) brightness);

                    if (x == 1) leftEdge.add(pixel);
                    else if (x == width - 2) rightEdge.add(pixel);
                    else if (y == 1) topEdge.add(pixel);
                    else if (y == height - 2) bottomEdge.add(pixel);
                }
            }
        }
    }

    public String generateBinaryCode(List<ContourPixel> edgePixels) {
        StringBuilder code = new StringBuilder();
        for (ContourPixel pixel : edgePixels) {
            code.append(toBinaryString(pixel.x(), 8));
            code.append(toBinaryString(pixel.y(), 8));
            code.append(toBinaryString(pixel.brightness() & 0xFF, 8));
        }
        return code.toString();
    }

    private String toBinaryString(int value, int bits) {
        String binary = Integer.toBinaryString(value);
        while (binary.length() < bits) {
            binary = "0" + binary;
        }
        return binary;
    }

    public boolean isCompatibleWith(PuzzlePiece other, String thisSide, String otherSide, int brightnessTolerance, int maxMismatch) {
        List<ContourPixel> edgeA = getEdgeBySide(thisSide);
        List<ContourPixel> edgeB = other.getEdgeBySide(otherSide);
        if (edgeA == null || edgeB == null || edgeA.isEmpty() || edgeB.isEmpty()) return false;

        List<ContourPixel> reversedB = new ArrayList<>(edgeB);
        Collections.reverse(reversedB);

        int length = Math.min(edgeA.size(), reversedB.size());
        int mismatchCount = 0;

        for (int i = 0; i < length; i++) {
            ContourPixel a = edgeA.get(i);
            ContourPixel b = reversedB.get(i);
            int diff = Math.abs((a.brightness() & 0xFF) - (b.brightness() & 0xFF));
            if (diff > brightnessTolerance) mismatchCount++;
            if (mismatchCount > maxMismatch) return false;
        }

        return true;
    }

    private List<ContourPixel> getEdgeBySide(String side) {
        return switch (side.toLowerCase()) {
            case "top" -> topEdge;
            case "bottom" -> bottomEdge;
            case "left" -> leftEdge;
            case "right" -> rightEdge;
            default -> null;
        };
    }

    public List<ContourPixel> getTopEdge() { return topEdge; }
    public List<ContourPixel> getBottomEdge() { return bottomEdge; }
    public List<ContourPixel> getLeftEdge() { return leftEdge; }
    public List<ContourPixel> getRightEdge() { return rightEdge; }
} 
