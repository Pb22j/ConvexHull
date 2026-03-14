import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.JPanel;

/**
 * Visualization Engine.
 * Updated for better readability and explicit sorting loops.
 */
public class DrawerPanel extends JPanel {

    // --- Data ---
    private List<Point> allPoints;
    public String[] XYList;

    // --- Algorithm Results ---
    public List<Integer> upperHullIndices = new ArrayList<>();
    public List<Integer> lowerHullIndices = new ArrayList<>();
    public List<Integer> extremePointIndices = new ArrayList<>();

    // --- Visual Lists (Points actually drawn on screen) ---
    private List<Point> upperHullPointsDraw = new ArrayList<>();
    private List<Point> lowerHullPointsDraw = new ArrayList<>();

    // --- Viewport Settings ---
    private static final int PADDING = 50;
    private int minX = 0, maxX = 1000, minY = 0, maxY = 1000;

    // --- Colors ---
    private final Color BG_COLOR = new Color(32, 33, 36);
    private final Color GRID_COLOR = new Color(60, 64, 67);
    private final Color AXIS_COLOR = new Color(154, 160, 166);
    private final Color TEXT_COLOR = new Color(200, 200, 200);
    private final Color POINT_COLOR = new Color(138, 180, 248);
    
    // Verification Colors
    private final Color UPPER_HULL_COLOR = new Color(66, 133, 244); // Blue
    private final Color LOWER_HULL_COLOR = new Color(52, 168, 83);  // Green
    private final Color EXTREME_POINT_COLOR = new Color(234, 67, 53); // Red

    public DrawerPanel() {
        this.setBackground(BG_COLOR);
        allPoints = new ArrayList<>();
        XYList = new String[0];
    }

    public void loadData(String path) {
        XYList = Reader.getData(path);
        allPoints = new ArrayList<>();
        clearResults();

        if (XYList != null) {
            parsePointsAndFindBounds();
        }
        repaint();
    }

    private void clearResults() {
        upperHullIndices.clear();
        lowerHullIndices.clear();
        extremePointIndices.clear();
        upperHullPointsDraw.clear();
        lowerHullPointsDraw.clear();
    }

    public void clearHull() {
        clearResults();
        repaint();
    }

    private void parsePointsAndFindBounds() {
        if (XYList == null || XYList.length == 0) return;
        
        // Reset bounds to default 0-1000
        minX = 0; maxX = 1000;
        minY = 0; maxY = 1000;

        for (String s : XYList) {
            try {
                String[] parts = s.split(",");
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                
                // Expand bounds if points are outside 0-1000
                if (x < minX) minX = (int)x;
                if (x > maxX) maxX = (int)x;
                if (y < minY) minY = (int)y;
                if (y > maxY) maxY = (int)y;

                allPoints.add(new Point((int)x, (int)y));
            } catch (Exception ignored) {}
        }
        
        // Add a small buffer around the edges
        if (minX < 0) minX -= 10;
        if (maxX > 1000) maxX += 10;
        if (minY < 0) minY -= 10;
        if (maxY > 1000) maxY += 10;
    }

    // ==========================================
    //            ALGORITHMS SECTION
    // ==========================================

    // --- 1. Brute Force ---
    public void runBruteForce() {
        if (allPoints.size() < 3) return;
        clearResults();

        int n = allPoints.size();
        int leftMost = 0;
        
        // Find the leftmost point
        for (int i = 1; i < n; i++) {
            Point p = allPoints.get(i);
            Point l = allPoints.get(leftMost);
            if (p.x < l.x || (p.x == l.x && p.y < l.y)) leftMost = i;
        }

        int p = leftMost;
        int q;
        do {
            extremePointIndices.add(p);
            q = (p + 1) % n;
            for (int i = 0; i < n; i++) {
                if (pointLocation(allPoints.get(p), allPoints.get(i), allPoints.get(q)) == 1) {
                    q = i;
                }
            }
            p = q;
        } while (p != leftMost);

        finishAndSort();
    }

    // --- 2. Quick Hull ---
    public void runQuickHull() {
        if (allPoints.size() < 3) return;
        clearResults();

        PointWithIndex minP = new PointWithIndex(allPoints.get(0), 0);
        PointWithIndex maxP = new PointWithIndex(allPoints.get(0), 0);

        for (int i = 1; i < allPoints.size(); i++) {
            Point p = allPoints.get(i);
            if (p.x < minP.p.x) minP = new PointWithIndex(p, i);
            if (p.x > maxP.p.x) maxP = new PointWithIndex(p, i);
        }

        List<PointWithIndex> hull = new ArrayList<>();
        List<PointWithIndex> upperSet = new ArrayList<>();
        List<PointWithIndex> lowerSet = new ArrayList<>();

        for (int i = 0; i < allPoints.size(); i++) {
            Point p = allPoints.get(i);
            if (i == minP.originalIndex || i == maxP.originalIndex) continue;
            
            int loc = pointLocation(minP.p, maxP.p, p);
            if (loc == 1) upperSet.add(new PointWithIndex(p, i));
            else if (loc == -1) lowerSet.add(new PointWithIndex(p, i));
        }

        hull.add(minP);
        findHullRecursive(minP, maxP, upperSet, hull);
        hull.add(maxP);
        findHullRecursive(maxP, minP, lowerSet, hull);
        
        for(PointWithIndex pi : hull) extremePointIndices.add(pi.originalIndex);
        
        finishAndSort();
    }

    private void findHullRecursive(PointWithIndex A, PointWithIndex B, List<PointWithIndex> set, List<PointWithIndex> hull) {
        if (set.isEmpty()) return;
        int insertPos = hull.indexOf(B);
        if(insertPos == -1) insertPos = hull.size();

        if (set.size() == 1) {
            hull.add(insertPos, set.get(0));
            return;
        }

        double maxDist = Double.NEGATIVE_INFINITY;
        PointWithIndex furth = null;
        for (PointWithIndex pi : set) {
            double d = distance(A.p, B.p, pi.p);
            if (d > maxDist) { maxDist = d; furth = pi; }
        }
        
        if (furth == null) return;
        hull.add(insertPos, furth);

        List<PointWithIndex> leftAP = new ArrayList<>();
        List<PointWithIndex> leftPB = new ArrayList<>();
        for (PointWithIndex pi : set) {
            if (pi == furth) continue;
            if (pointLocation(A.p, furth.p, pi.p) == 1) leftAP.add(pi);
            if (pointLocation(furth.p, B.p, pi.p) == 1) leftPB.add(pi);
        }
        findHullRecursive(A, furth, leftAP, hull);
        findHullRecursive(furth, B, leftPB, hull);
    }

    // --- 3. Graham Scan ---
    public void runGrahamScan() {
        if (allPoints.size() < 3) return;
        clearResults();

        List<PointWithIndex> indexedPoints = new ArrayList<>();
        for(int i=0; i<allPoints.size(); i++) {
            indexedPoints.add(new PointWithIndex(allPoints.get(i), i));
        }

        PointWithIndex start = getMinY(indexedPoints);
        List<PointWithIndex> sorted = sortPolar(indexedPoints, start);

        Stack<PointWithIndex> stack = new Stack<>();
        stack.push(start);
        stack.push(sorted.get(0));

        for (int i = 1; i < sorted.size(); i++) {
            PointWithIndex top = stack.pop();
            while (!stack.isEmpty() && ccw(stack.peek().p, top.p, sorted.get(i).p) <= 0) {
                top = stack.pop();
            }
            stack.push(top);
            stack.push(sorted.get(i));
        }

        for(PointWithIndex pi : stack) extremePointIndices.add(pi.originalIndex);
        finishAndSort();
    }

    // ==========================================
    //      SORTING & FINISHING LOGIC
    // ==========================================

    private void finishAndSort() {
        classifyHullPoints();
        
        // 1. Manually sort the indices using loops (No arrow functions!)
        bubbleSortIndicesByX(upperHullIndices);
        bubbleSortIndicesByX(lowerHullIndices);
        
        // 2. Prepare draw lists (Upper Hull)
        upperHullPointsDraw.clear();
        for (int i = 0; i < upperHullIndices.size(); i++) {
            int index = upperHullIndices.get(i);
            upperHullPointsDraw.add(getPointFromIndex(index));
        }

        // 3. Prepare draw lists (Lower Hull)
        lowerHullPointsDraw.clear();
        for (int i = 0; i < lowerHullIndices.size(); i++) {
            int index = lowerHullIndices.get(i);
            lowerHullPointsDraw.add(getPointFromIndex(index));
        }
        
        repaint();
    }

    // Helper: Bubble Sort to organize points from Left to Right
    private void bubbleSortIndicesByX(List<Integer> indices) {
        int n = indices.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                
                Point p1 = getPointFromIndex(indices.get(j));
                Point p2 = getPointFromIndex(indices.get(j + 1));

                if (p1.x > p2.x) {
                    // Swap elements
                    int temp = indices.get(j);
                    indices.set(j, indices.get(j + 1));
                    indices.set(j + 1, temp);
                }
            }
        }
    }

    // --- Classification Logic ---
    private void classifyHullPoints() {
        if (extremePointIndices.isEmpty()) return;

        int minIdx = extremePointIndices.get(0);
        int maxIdx = extremePointIndices.get(0);
        Point minP = getPointFromIndex(minIdx);
        Point maxP = getPointFromIndex(maxIdx);

        // Find min and max X
        for (int idx : extremePointIndices) {
            Point p = getPointFromIndex(idx);
            if (p.x < minP.x) { minP = p; minIdx = idx; }
            if (p.x > maxP.x) { maxP = p; maxIdx = idx; }
        }

        // Classify top vs bottom
        for (int idx : extremePointIndices) {
            if (idx == minIdx || idx == maxIdx) {
                upperHullIndices.add(idx);
                lowerHullIndices.add(idx); 
                continue;
            }
            Point p = getPointFromIndex(idx);
            int loc = pointLocation(minP, maxP, p);
            if (loc > 0) upperHullIndices.add(idx); 
            else lowerHullIndices.add(idx);       
        }
        
        // Remove duplicates using Sets
        Set<Integer> set = new HashSet<>(upperHullIndices);
        upperHullIndices.clear(); upperHullIndices.addAll(set);
        set = new HashSet<>(lowerHullIndices);
        lowerHullIndices.clear(); lowerHullIndices.addAll(set);
    }

    // --- Math Helpers ---
    private Point getPointFromIndex(int i) {
        String[] parts = XYList[i].split(",");
        return new Point((int)Double.parseDouble(parts[0]), (int)Double.parseDouble(parts[1]));
    }

    private int pointLocation(Point A, Point B, Point P) {
        double cp = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        if (cp > 0) return 1; 
        if (cp == 0) return 0; 
        return -1;            
    }
    
    private double distance(Point A, Point B, Point C) {
        double ABx = B.x - A.x;
        double ABy = B.y - A.y;
        return Math.abs(ABx * (A.y - C.y) - ABy * (A.x - C.x));
    }

    private static class PointWithIndex {
        Point p; int originalIndex;
        PointWithIndex(Point p, int i) { this.p = p; this.originalIndex = i; }
    }

    private PointWithIndex getMinY(List<PointWithIndex> list) {
        PointWithIndex min = list.get(0);
        for(PointWithIndex pi : list) {
            if(pi.p.y < min.p.y || (pi.p.y == min.p.y && pi.p.x < min.p.x)) min = pi;
        }
        return min;
    }

    private List<PointWithIndex> sortPolar(List<PointWithIndex> list, PointWithIndex pivot) {
        // NOTE: Keeping a small lambda here for Collections.sort is standard, 
        // but if you want this removed too, let me know.
        List<PointWithIndex> copy = new ArrayList<>(list);
        copy.remove(pivot);
        copy.sort((a, b) -> {
            int orient = ccw(pivot.p, a.p, b.p);
            if (orient == 0) {
                double d1 = distSq(pivot.p, a.p);
                double d2 = distSq(pivot.p, b.p);
                return Double.compare(d1, d2);
            }
            return -orient; 
        });
        return copy;
    }

    private int ccw(Point a, Point b, Point c) {
        double val = (double)(b.x - a.x)*(c.y - a.y) - (double)(b.y - a.y)*(c.x - a.x);
        if (val < 0) return -1;
        if (val > 0) return 1;
        return 0;
    }

    private double distSq(Point a, Point b) {
        return Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2);
    }

    // ==========================================
    //              DRAWING SECTION
    // ==========================================
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        
        // Define the area where we actually draw points (inside padding)
        int drawableWidth = w - 2 * PADDING;
        int drawableHeight = h - 2 * PADDING;

        drawGridAndAxes(g2d, w, h, drawableWidth, drawableHeight);

        if (allPoints.isEmpty()) return;

        double rangeX = (maxX - minX) == 0 ? 1 : (maxX - minX);
        double rangeY = (maxY - minY) == 0 ? 1 : (maxY - minY);

        // 1. Draw All Points
        g2d.setColor(POINT_COLOR);
        for (Point p : allPoints) {
            Point s = scale(p, drawableWidth, drawableHeight, rangeX, rangeY, h);
            g2d.fillOval(s.x - 4, s.y - 4, 8, 8);
        }

        // 2. Draw UPPER Hull (Blue Lines)
        if (!upperHullPointsDraw.isEmpty()) {
            g2d.setColor(UPPER_HULL_COLOR);
            g2d.setStroke(new BasicStroke(3f));
            for (int i = 0; i < upperHullPointsDraw.size() - 1; i++) {
                Point p1 = upperHullPointsDraw.get(i);
                Point p2 = upperHullPointsDraw.get(i + 1);
                Point s1 = scale(p1, drawableWidth, drawableHeight, rangeX, rangeY, h);
                Point s2 = scale(p2, drawableWidth, drawableHeight, rangeX, rangeY, h);
                g2d.drawLine(s1.x, s1.y, s2.x, s2.y);
            }
        }

        // 3. Draw LOWER Hull (Green Lines)
        if (!lowerHullPointsDraw.isEmpty()) {
            g2d.setColor(LOWER_HULL_COLOR);
            g2d.setStroke(new BasicStroke(3f));
            for (int i = 0; i < lowerHullPointsDraw.size() - 1; i++) {
                Point p1 = lowerHullPointsDraw.get(i);
                Point p2 = lowerHullPointsDraw.get(i + 1);
                Point s1 = scale(p1, drawableWidth, drawableHeight, rangeX, rangeY, h);
                Point s2 = scale(p2, drawableWidth, drawableHeight, rangeX, rangeY, h);
                g2d.drawLine(s1.x, s1.y, s2.x, s2.y);
            }
        }

        // 4. Draw EXTREME Points (Red Dots)
        g2d.setColor(EXTREME_POINT_COLOR);
        for (int idx : extremePointIndices) {
             Point p = getPointFromIndex(idx);
             Point s = scale(p, drawableWidth, drawableHeight, rangeX, rangeY, h);
             g2d.fillOval(s.x - 5, s.y - 5, 10, 10); 
        }
    }

    private void drawGridAndAxes(Graphics2D g2d, int w, int h, int dw, int dh) {
        // Draw Center Dashed Grid
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{9.0f}, 0.0f));
        g2d.drawLine(PADDING, h/2, w-PADDING, h/2);
        g2d.drawLine(w/2, PADDING, w/2, h-PADDING);

        // Draw Main Axis Lines
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(AXIS_COLOR);
        g2d.drawLine(PADDING, PADDING, PADDING, h - PADDING); // Y-Axis
        g2d.drawLine(PADDING, h - PADDING, w - PADDING, h - PADDING); // X-Axis

        // Draw Numbers and Ticks (0 to 1000)
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Consolas", Font.PLAIN, 10));

        int step = 100;
        
        // X-Axis Labels
        for (int i = 0; i <= 1000; i += step) {
            int xPixel = PADDING + (int)((i / 1000.0) * dw);
            int yBase = h - PADDING;
            g2d.drawLine(xPixel, yBase, xPixel, yBase + 5);
            String label = String.valueOf(i);
            int textWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, xPixel - (textWidth / 2), yBase + 20);
        }

        // Y-Axis Labels
        for (int i = 0; i <= 1000; i += step) {
            int yPixel = (h - PADDING) - (int)((i / 1000.0) * dh);
            int xBase = PADDING;
            g2d.drawLine(xBase - 5, yPixel, xBase, yPixel);
            String label = String.valueOf(i);
            int textWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, xBase - 10 - textWidth, yPixel + 4);
        }
    }

    private Point scale(Point p, int dw, int dh, double rx, double ry, int h) {
        // Converts math coordinates to screen pixels
        int x = PADDING + (int)(((p.x - minX) / rx) * dw);
        int y = h - PADDING - (int)(((p.y - minY) / ry) * dh);
        return new Point(x, y);
    }
}
