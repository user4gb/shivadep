package fr.free.jnizet.shivadep;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

public class MessageHandlerComponent extends JPanel implements MessageHandler {

    private JLabel label;

    public MessageHandlerComponent() {
        setLayout(new MigLayout("insets 0", "[grow, fill]", ""));
        setBackground(Color.WHITE);
        this.label = new JLabel(" ");
        this.label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(this.label, "gapx 10, growx, wrap");
        add(new JSeparator(), "growx");
    }

    @Override
    public void clear() {
        label.setText(" ");
    }

    @Override
    public void displayError(String message) {
        label.setForeground(Color.RED);
        label.setText(message);
    }

    @Override
    public void displayInfo(String message) {
        label.setForeground(Color.BLACK);
        label.setText(message);
    }
}
