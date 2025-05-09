package CY_PUZZLE;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class SideBarFactory {

    public static JPanel createSideBarPanel(JLabel pieceLabel, JLabel timerLabel) {
        JPanel sideBarPanel = new JPanel() {
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

        JLabel titleLabel = LabelFactory.createLabel("CY-PUZZLE", 30, Font.BOLD);
        JButton uploadButton = ButtonFactory.createButton("Télécharger une image", new Color(46, 204, 113));
        JButton startButton = ButtonFactory.createButton("Lancer la résolution", new Color(146, 104, 113));
        JPanel statsPanel = StatsPanelFactory.createStatsPanel(pieceLabel, timerLabel);

        sideBarPanel.add(titleLabel);
        sideBarPanel.add(Box.createVerticalStrut(10));
        sideBarPanel.add(uploadButton);
        sideBarPanel.add(Box.createVerticalStrut(10));
        sideBarPanel.add(startButton);
        sideBarPanel.add(Box.createVerticalStrut(10));
        sideBarPanel.add(statsPanel);

        return sideBarPanel;
    }
}
