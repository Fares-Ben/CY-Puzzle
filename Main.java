package CY_PUZZLE;

import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to set LookAndFeel", ex);
        }

        Acceuil game = new Acceuil();
        game.setVisible(true);
    }
}
