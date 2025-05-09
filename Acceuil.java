package CY_PUZZLE;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

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

        pieceLabel = LabelFactory.createLabel("Pièces :", 18, Font.PLAIN);
        timerLabel = LabelFactory.createLabel("Timer :", 18, Font.PLAIN);

        sideBarPanel = SideBarFactory.createSideBarPanel(pieceLabel, timerLabel);

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
}
