package CY_PUZZLE;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

public class ButtonFactory {

    public static JButton createButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.darker());
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
                button.repaint();
            }
        });

        return button;
    }
}
