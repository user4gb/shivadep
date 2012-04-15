package fr.free.jnizet.shivadep;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;


public class CenteredPanel extends JPanel {

    public CenteredPanel(JPanel content) {
        setLayout(new MigLayout("fill, insets 0", "[center]", "[center]"));
        add(content);
    }
}
