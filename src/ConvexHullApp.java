/*

GameHub
Copyright (C) 2026 to:

-Pb22j 
-lFahadr
-Abdulra7hman
-Waleed Alnajashi

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.

*/
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;

/**
 * The Main Application Controller.
 * Refactored for better readability.
 */
public class ConvexHullApp {

    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    private DrawerPanel drawerPanel;
    private JTextArea upperHullText;
    private JTextArea lowerHullText;
    private JTextArea extremeText;
    private JLabel timeLabel;

    // --- Theme Colors ---
    private final Color Logo_BG = new Color(32, 33, 36);
    private final Color Logo_PANEL = new Color(41, 42, 45);
    private final Color TEXT_WHITE = new Color(232, 234, 237);
    
    private final Color BTN_GREEN = new Color(52, 168, 83);
    private final Color BTN_BLUE  = new Color(66, 133, 244);
    private final Color BTN_RED   = new Color(234, 67, 53);
    private final Color BTN_YELLOW= new Color(251, 188, 4);
    private final Color BTN_PURPLE= new Color(161, 66, 244);
    private final Color BTN_GRAY  = new Color(95, 99, 104); 

    public static void main(String[] args) {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new ConvexHullApp().initUI());
    }

    public void initUI() {
        frame = new JFrame("CSC311 Convex Hull Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createWelcomePanel(), "WELCOME");
        cardPanel.add(createMainAppPanel(), "APP");

        frame.add(cardPanel);
        frame.setVisible(true);
    }

    // ================= WELCOME SCREEN =================
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Logo_BG);

        // 1. Top Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(20, 20, 20, 20));
        topBar.add(createCustomLogoLabel());
        panel.add(topBar, BorderLayout.NORTH);

        // 2. Center Content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        
        JLabel title = new JLabel("CSC311 ConvecHull Project");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(TEXT_WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Instructor: Dr Mishal Aldekhayel");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(Color.CYAN);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        
        JButton btnStart = createStyledButton("    Start Project    ", BTN_GREEN);
        btnStart.setBackground(Color.CYAN);
        btnStart.setForeground(Color.BLACK);
        
        JButton btnCredits = createStyledButton("    Credits    ", BTN_PURPLE);
        btnCredits.setBackground(Color.LIGHT_GRAY);
        btnCredits.setForeground(Color.black);
        
        JButton btnExit = createStyledButton("    Exit    ", BTN_RED);
        
        // Actions
        btnStart.addActionListener(e -> cardLayout.show(cardPanel, "APP"));
        btnCredits.addActionListener(e -> showCredits());
        btnExit.addActionListener(e -> System.exit(0));

        btnPanel.add(btnStart);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(btnCredits);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(btnExit);
        
        // Add everything to center
        center.add(Box.createVerticalGlue());
        center.add(title);
        center.add(Box.createVerticalStrut(10));
        center.add(subtitle);
        center.add(Box.createVerticalStrut(50));
        center.add(btnPanel);
        center.add(Box.createVerticalGlue());

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    // ================= MAIN APP SCREEN =================
    private JPanel createMainAppPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Logo_BG);

        drawerPanel = new DrawerPanel();

        // Create the three main sections
        JPanel controls = createLeftControlPanel();
        JPanel canvasWrap = createCenterCanvas();
        JPanel dashboard = createRightDashboard();

        panel.add(controls, BorderLayout.WEST);
        panel.add(canvasWrap, BorderLayout.CENTER);
        panel.add(dashboard, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLeftControlPanel() {
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBackground(Logo_PANEL);
        controls.setPreferredSize(new Dimension(200, 0));
        controls.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Buttons
        JButton bLoad   = createStyledButton("Load Data", Color.CYAN);
        bLoad.setForeground(Color.BLACK);
        
        JButton bBrute  = createStyledButton("Brute Force", Color.GRAY);
        bBrute.setForeground(Color.BLACK);
        
        JButton bQuick  = createStyledButton("Quick Hull", Color.CYAN);
        bQuick.setForeground(Color.BLACK);
        
        JButton bGraham = createStyledButton("Graham Scan", Color.GRAY);
        bGraham.setForeground(Color.BLACK);
        
        JButton bClear  = createStyledButton("Reset", Color.CYAN);
        bClear.setForeground(Color.BLACK);
        
        JButton bBack   = createStyledButton("Back", new Color(60, 60, 60));

        // Layout
        controls.add(new JLabel("<html><b style='color:white;font-size:15px'>Controls</b></html>"));
        controls.add(Box.createVerticalStrut(15));
        controls.add(bLoad);
        controls.add(Box.createVerticalStrut(10));
        controls.add(bBrute);
        controls.add(Box.createVerticalStrut(10));
        controls.add(bQuick);
        controls.add(Box.createVerticalStrut(10));
        controls.add(bGraham);
        controls.add(Box.createVerticalStrut(30));
        controls.add(bClear);
        controls.add(Box.createVerticalGlue()); 
        controls.add(bBack);

        // Actions (Calls specific helper methods for readability)
        bLoad.addActionListener(e -> handleLoadAction());
        bBrute.addActionListener(e -> runAlgorithm("Brute", () -> drawerPanel.runBruteForce()));
        bQuick.addActionListener(e -> runAlgorithm("Quick", () -> drawerPanel.runQuickHull()));
        bGraham.addActionListener(e -> runAlgorithm("Graham", () -> drawerPanel.runGrahamScan()));
        bClear.addActionListener(e -> handleClearAction());
        bBack.addActionListener(e -> cardLayout.show(cardPanel, "WELCOME"));

        return controls;
    }

    private JPanel createCenterCanvas() {
        JPanel canvasWrap = new JPanel(new BorderLayout());
        canvasWrap.setBorder(new LineBorder(Color.DARK_GRAY));
        canvasWrap.add(drawerPanel);
        return canvasWrap;
    }

    private JPanel createRightDashboard() {
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBackground(Logo_PANEL);
        dashboard.setPreferredSize(new Dimension(250, 0));
        dashboard.setBorder(new EmptyBorder(10, 10, 10, 10));

        timeLabel = new JLabel("Time: 0 Millisecond");
        timeLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        timeLabel.setForeground(BTN_GREEN);

        upperHullText = createStatArea("Upper Hull Points");
        lowerHullText = createStatArea("Lower Hull Points");
        extremeText = createStatArea("Extreme Points");

        dashboard.add(new JLabel("<html><h2 style='color:white'>Dashboard</h2></html>"));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(timeLabel);
        dashboard.add(Box.createVerticalStrut(20));
        dashboard.add(new JScrollPane(upperHullText));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(new JScrollPane(lowerHullText));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(new JScrollPane(extremeText));
        
        return dashboard;
    }

    // ================= ACTIONS & LOGIC =================

    private void handleLoadAction() {
        JFileChooser fc = new JFileChooser(".");
        if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            drawerPanel.loadData(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private void handleClearAction() {
        drawerPanel.clearHull();
        updateDashboard(0);
    }

    private void runAlgorithm(String name, Runnable algo) {
        long start = System.nanoTime();
        algo.run();
        long end = System.nanoTime();
        updateDashboard(end - start);
    }

    private void showCredits() {
        String msg = "ConvexHull Project For CSC311\n\n" +
                     "Instructor:\nDr Mishal Aldekhayel\n\n" +
                     "Developed by:\n" +
                     "Abdulrahamn Almzeal     Id:444100989\n" +
                     "Mohammed Alwanis       Id:444100734\n" +
                     "Waleed Alnajashi             Id:441101493\n" +
                     "Fahad Alsuhaibani           Id:444102498";
        JOptionPane.showMessageDialog(frame, msg, "Credits", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateDashboard(long timeNs) {
        timeLabel.setText("Time: " + timeNs/1000 + " Millisecond");
        upperHullText.setText(formatIndices(drawerPanel.upperHullIndices));
        lowerHullText.setText(formatIndices(drawerPanel.lowerHullIndices));
        extremeText.setText(formatIndices(drawerPanel.extremePointIndices));
    }

    private String formatIndices(java.util.List<Integer> indices) {
        if(indices.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for(int i : indices) {
            String coord = drawerPanel.XYList[i];
            sb.append("P").append(i).append(": (").append(coord).append(")\n");
        }
        return sb.toString();
    }

    // ================= HELPERS =================

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setBorderPainted(false);
        return btn;
    }

    private JTextArea createStatArea(String title) {
        JTextArea ta = new JTextArea(5, 15);
        ta.setBackground(new Color(30, 30, 30));
        ta.setForeground(Color.LIGHT_GRAY);
        TitledBorder tb = BorderFactory.createTitledBorder(
            new LineBorder(Color.GRAY), title, 0, 0, new Font("Segoe UI", Font.PLAIN, 12), Color.WHITE);
        ta.setBorder(tb);
        ta.setEditable(false);
        return ta;
    }
    
    private JLabel createCustomLogoLabel() {
        JLabel logoLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                ImageIcon icon = Reader.getLogo("ksu.png");
                if (icon != null) {
                    g.drawImage(icon.getImage(), 1, 1, 220, 80, this);
                } else {
                    // Fallback Shape if image missing
                    g2.setColor(BTN_BLUE);
                    g2.fillOval(25, 35, 220, 80);
                }
                
                g2.setColor(Color.black);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(0, 0, getWidth(), getHeight());
            }
        };
        logoLabel.setPreferredSize(new Dimension(220, 80));
        return logoLabel;
    }
}
