import java.io.*;
import java.util.*;
import javax.swing.ImageIcon;
import java.awt.Image;

public class Reader {
    
    // --- Original Data Method ---
    public static String[] getData(String path) {
        List<String> points = new ArrayList<>();
        String[] XYList = null;
        try {
            File f = new File(path);
            if (!f.exists()) return new String[0];

            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            reader.close();

            if (line != null) {
                line = line.replaceAll("\\s+", "");
                String[] pairs = line.split("\\),\\(");
                for (String pair : pairs) {
                    pair = pair.replace("(", "").replace(")", "");
                    points.add(pair);
                }
            }
            XYList = points.toArray(new String[0]);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return new String[0];
        }
        return XYList;
    }

    // --- NEW: Image Loader for the Logo ---
    public static ImageIcon getLogo(String path) {
        // Try to load from file system
        File f = new File(path);
        if (f.exists()) {
            return new ImageIcon(path);
        }
        return null; // Return null if not found so GUI handles fallback
    }
}
