import java.util.Arrays;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point; // Simple class to hold (x, y)
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;


/**
 * A custom JPanel that draws 2D points, a hull, AND axes.
 */
public class DrawerPanel extends JPanel {

    // The 2D data structures (in data coordinates, e.g., 0-1000)
    private List<Point> allPoints;
    private List<Point> hullPoints;

    // Define data range and padding
    private static final int PADDING = 40; // Pixels for axis labels
    private static final int MAX_COORD = 1000; // Max value for X and Y
    public String [] XYList;
    public String [] XYhullpoint;
    public int[] UpperHullBrute;
    public int UpperCounter;
    public int[] LowerHullBrute;
    public int LowerCounter;
    public int[] ExtremPointBrute;
    public int ExtremCounter;
    public DrawerPanel(String path) {
        
        //THIS LINE WILL CONVERT THE TXT TO AN ARRAY WITH X,Y ONLY LIKE THIS 123,323.4
        XYList= Reader.getData(path);
        allPoints = new ArrayList<>();
        UpperHullBrute=new int[XYList.length];
        UpperCounter=0;
        LowerHullBrute=new int[XYList.length];
        LowerCounter=0;
        ExtremPointBrute=new int[XYList.length];
        ExtremCounter=0;
        Arrays.fill(UpperHullBrute,-1);
        Arrays.fill(LowerHullBrute,-1);
        Arrays.fill(ExtremPointBrute,-1);
        //This One Only For TEST We Should Do it With Some Algorithm
      // XYhullpoint= Reader.getData("D:\\Github\\ProgramingLanguae\\Java\\CSC311Proj\\src\\HullPointTest.txt");
        hullPoints = new ArrayList<>();
        AddPoints();
        int []h=BruteForceConvexHull();
        //NotSure About UPper HULLand Lower Hull 
        UpperHullBrute=Arrays.copyOf(UpperHullBrute, UpperCounter);
        LowerHullBrute=Arrays.copyOf(LowerHullBrute, LowerCounter);
        ExtremPointBrute=Arrays.copyOf(h,h.length);
        ExtremCounter=h.length;
        AddHullPoint(h);
      
    }//END Of METHOD

    public Double MaxY(){
        //This Algorithm Try To Find The Maxest Y in the points
        //Output:Double Maxest Y vlaue

        String[] parts = XYList[0].split(",");
        double MaxestY = Double.parseDouble(parts[1]);
        for(int i=1;i<XYList.length;i++){
            String[] part = XYList[i].split(",");
            double y = Double.parseDouble(part[1]);
            if(MaxestY<y)
                MaxestY=y;
        }
        return MaxestY;
    }
    public void AddPoints(){        
        //THIS LOOP WILL TAKE EACH ONE X,Y AND PUT IT IN TO THE GRAPGH
        for(int i=0;i<XYList.length;i++){
        String[] parts = XYList[i].split(",");

        // Parse as a double first
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
            allPoints.add(new Point((int)x,(int)y));
        }
    }
    public void AddHullPoint(int []XYHullPoint){
        //Input: Integar Array of HullPointIndexs 
        //THIS LOOP WILL TAKE EACH ONE X,Y OF THE HULL POINTS AND PUT IT IN TO THE GRAPGH
        for(int i=0;i<XYHullPoint.length;i++){
            
        String[] parts = XYList[XYHullPoint[i]].split(",");

        // Parse as a double first
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
            hullPoints.add(new Point((int)x,(int)y));
        }
    }
    public void displyValue(int[] A){
        for(int i=0;i<A.length;i++)
            System.out.println("Point: "+XYList[A[i]]);     
        System.out.println("They are:"+A.length+" Points");
    }
    public void AddHullPoint(String []XYHullPoint){
        //Input: String Array of HullPointsXY
        //THIS LOOP WILL TAKE EACH ONE X,Y OF THE HULL POINTS AND PUT IT IN TO THE GRAPGH
        for(int i=0;i<XYHullPoint.length;i++){
        String[] parts = XYHullPoint[i].split(",");

        // Parse as a double first
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
            hullPoints.add(new Point((int)x,(int)y));
        }
    }
    public int IndexOfSmallestXY(){
        //This Algorithm Try To Find The Point That have Smallest XY to make it as Start State
        //OutPut:Index of the Smallest XY Point
        int index = 0;
        for(int i=1;i<XYList.length;i++){
            String [] parts = XYList[i].split(",");
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            String [] smallestpoint=XYList[index].split(",");
            double smallestx = Double.parseDouble(smallestpoint[0]);
            double smallesty = Double.parseDouble(smallestpoint[1]);
            if(y<smallesty||(y==smallesty && x<smallestx)){
                index=i;
            }
        }
        return index;
    }
    public int [] BruteForceConvexHull(){
        //THis Algorithm to compute the ConvexHull Using BruteForce Method
        int [] hullP=new int[XYList.length];
        int hullPCount=0;
        int nextIndex = IndexOfSmallestXY();
        System.out.println(nextIndex);
        for(int i=0;i<hullP.length;i++){
            hullP[i]=-1;
        }

        hullP[hullPCount++]=nextIndex;
        char keySwitch='A';
        double MaxY=MaxY();
        boolean stop=false;
        //i Think its Big O(nm) 
            while(!stop){
                String parts[]=XYList[nextIndex].split(",");
                double y = Double.parseDouble(parts[1]);
                if(keySwitch=='A')
                    UpperHullBrute[UpperCounter++]=nextIndex;
                else if(keySwitch=='B')
                    LowerHullBrute[LowerCounter++]=nextIndex;
                if(y==MaxY)
                    keySwitch='B';
                nextIndex=findNextHullPoint(nextIndex,keySwitch);
                hullP[hullPCount]=nextIndex;
                if(hullP[hullPCount]==hullP[0])
                    stop=true;
                
                hullPCount++;   
               
        }
        int hullPt[]=Arrays.copyOf(hullP, hullPCount);

        /*       ^
                / \
                 |
        {Arrays.copyOf do that}
        int hullPt[]=new int[hullPCount];
        for(int i=0;i<hullPt.length;i++)
            hullPt[i]=hullP[i];
             */
        return hullPt;
    }

  public int findNextHullPoint(int index,char keySwitch){
    /*
    This Algorithm Take index of Point and KeySwitch its try to find the next hull point
    by tries with each point and see the Maxest Angle between them
    The keySwitch here is use for tell the Algorithm that y must not increased after the largest Y
    we have been see it 
    */ 
    //INPUT:index that try to find next hull point,char KeySwitch to notice the algorithm that y must decrese
    //OutPut:Next HUll Point Index
        int Smallestindex = index;
        String [] part = XYList[Smallestindex].split(",");
        double sx = Double.parseDouble(part[0]);
        double sy = Double.parseDouble(part[1]);
        double maxAngle=Double.NEGATIVE_INFINITY;
        int Maxindex=-1;
        if(keySwitch=='A'){
            for(int i=0;i<XYList.length;i++){
                String [] parts = XYList[i].split(",");
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double angle=Math.atan2(y-sy, x-sx);
                if(angle>maxAngle&&index!=i){
                    maxAngle=angle;
                    Maxindex=i;
                }
            }
        }
        else if(keySwitch=='B'){
            for(int i=0;i<XYList.length;i++){
                String [] parts = XYList[i].split(",");
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double angle=Math.atan2(y-sy, x-sx);
                if((angle>maxAngle&&index!=i)&&y<sy){
                    maxAngle=angle;
                    Maxindex=i;
                }
            }
        }
        return Maxindex;
    }
 

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        // Get the actual size of the drawing area (panel size minus padding)
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int drawingWidth = panelWidth - 2 * PADDING;
        int drawingHeight = panelHeight - 2 * PADDING;

        // --- 1. Draw Axes and Labels (NEW) ---
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new java.awt.BasicStroke(1)); // Thinner line for axes
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        // Draw Y-axis line
        g2d.drawLine(PADDING, PADDING, PADDING, panelHeight - PADDING);
        // Draw X-axis line
        g2d.drawLine(PADDING, panelHeight - PADDING, panelWidth - PADDING, panelHeight - PADDING);

        // Draw ticks and labels
        for (int i = 0; i <= MAX_COORD; i += 25) {
            String label = Integer.toString(i);

            // --- Y-axis ticks ---
            // Calculate the panel's Y coordinate (flipping the axis)
            int y = panelHeight - PADDING - (i * drawingHeight / MAX_COORD);
            // Draw tick mark
            g2d.drawLine(PADDING - 5, y, PADDING + 5, y);
            // Draw label (adjusting for font size)
            g2d.drawString(label, PADDING - 30, y + 5);

            // --- X-axis ticks ---
            // Calculate the panel's X coordinate
            int x = PADDING + (i * drawingWidth / MAX_COORD);
            // Draw tick mark
            g2d.drawLine(x, panelHeight - PADDING - 5, x, panelHeight - PADDING + 5);
            // Draw label (adjusting for font size)
            g2d.drawString(label, x - 5, panelHeight - PADDING + 20);
        }


        // --- 2. Draw all the points (MODIFIED to use coordinate transform) ---
        g2d.setColor(Color.BLACK);
        for (Point p : allPoints) {
            // Transform data coordinates (p.x, p.y) to panel coordinates (x, y)
            int x = PADDING + (p.x * drawingWidth / MAX_COORD);
            int y = panelHeight - PADDING - (p.y * drawingHeight / MAX_COORD);
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }

        // --- 3. Draw the hull (MODIFIED to use coordinate transform) ---
        g2d.setColor(Color.RED);
        g2d.setStroke(new java.awt.BasicStroke(2)); // Thicker line for hull

        for (int i = 0; i < hullPoints.size() - 1; i++) {
            Point p1 = hullPoints.get(i);
            Point p2 = hullPoints.get(i + 1);

            // Transform coordinates for p1
            int x1 = PADDING + (p1.x * drawingWidth / MAX_COORD);
            int y1 = panelHeight - PADDING - (p1.y * drawingHeight / MAX_COORD);

            // Transform coordinates for p2
            int x2 = PADDING + (p2.x * drawingWidth / MAX_COORD);
            int y2 = panelHeight - PADDING - (p2.y * drawingHeight / MAX_COORD);

            g2d.drawLine(x1, y1, x2, y2);
        }
    }
}