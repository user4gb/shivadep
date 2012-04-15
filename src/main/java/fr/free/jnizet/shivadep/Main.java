package fr.free.jnizet.shivadep;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (ClassNotFoundException e) {
                }
                catch (InstantiationException e) {
                }
                catch (IllegalAccessException e) {
                }
                catch (UnsupportedLookAndFeelException e) {
                }
                MainFrame f = new MainFrame();
                f.setVisible(true);
            }
        });
    }
}
