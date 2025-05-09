package CY_PUZZLE;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class StatsPanelFactory {

    public static JPanel createStatsPanel(JLabel pieceLabel, JLabel timerLabel) {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 50), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel statsTitle = LabelFactory.createLabel("Informations", 20, Font.BOLD);
        statsPanel.add(statsTitle);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(pieceLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(timerLabel);

        return statsPanel;
    }
}
