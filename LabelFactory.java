package CY_PUZZLE;

import java.awt.*;
import javax.swing.*;

public class LabelFactory {

    public static JLabel createLabel(String text, int fontSize, int fontStyle) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", fontStyle, fontSize));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
