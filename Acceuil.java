package CY_PUZZLE;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.MouseEvent;
public class Acceuil extends JFrame {

    private final int FRAME_WIDTH = 1200;
    private final int FRAME_HEIGHT = 800;

    private JPanel gridPanel, sideBarPanel;
    private int GRID_SIZE = 4;
    private JLabel pieceLabel, timerLabel;

    public Acceuil() {
        setTitle("CY-PUZZLE");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createSideBarPanel();

        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.getViewport().setBackground(new Color(240, 240, 240));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideBarPanel, scrollPane);
        splitPane.setDividerLocation(300);
        splitPane.setEnabled(false);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private void createSideBarPanel() {
        sideBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                Color color1 = new Color(41, 128, 185);
                Color color2 = new Color(44, 62, 80);

                GradientPaint gradient = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };

        sideBarPanel.setLayout(new BoxLayout(sideBarPanel, BoxLayout.Y_AXIS));
        sideBarPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = createLabel("CY-PUZZLE", 30, Font.BOLD);
        JButton uploadButton = createButton("Télécharger un dossier", new Color(46, 204, 113));
        JButton startButton = createButton("Lancer la résolution", new Color(146, 104, 113));

        pieceLabel = createLabel("Pièces :", 18, Font.PLAIN);
        timerLabel = createLabel("Timer :", 18, Font.PLAIN);

        JPanel statsPanel = createStatsPanel();

        sideBarPanel.add(titleLabel);
        sideBarPanel.add(Box.createVerticalStrut(10));
        sideBarPanel.add(uploadButton);
        sideBarPanel.add(Box.createVerticalStrut(10));
        sideBarPanel.add(startButton);
        sideBarPanel.add(Box.createVerticalStrut(10));
        sideBarPanel.add(statsPanel);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 50), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel statsTitle = createLabel("Informations", 20, Font.BOLD);
        statsPanel.add(statsTitle);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(pieceLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(timerLabel);

        return statsPanel;
    }

    private JLabel createLabel(String text, int fontSize, int fontStyle) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", fontStyle, fontSize));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground()); // Utiliser la couleur actuelle définie par setBackground()
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color); // Définir la couleur de fond initiale
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Désactiver le rendu par défaut
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.darker()); // Changer la couleur au survol
                button.repaint(); // Forcer le rafraîchissement
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(color); // Restaurer la couleur initiale
                button.repaint(); // Forcer le rafraîchissement
            }
        });

        return button;
    }
}
