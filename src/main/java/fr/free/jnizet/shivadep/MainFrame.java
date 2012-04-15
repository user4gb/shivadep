package fr.free.jnizet.shivadep;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;


public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("ShivaDep");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new MigLayout());
        MessageHandlerComponent messageHandler = new MessageHandlerComponent();
        add(messageHandler, "dock north");
        JPanel mainPanel = createMainPanel(messageHandler);
        add(mainPanel, "dock center");
        pack();
    }

    private JPanel createMainPanel(MessageHandler messageHandler) {
        final JPanel panel = new JPanel();
        final CardLayout cardLayout = new CardLayout();
        panel.setLayout(cardLayout);

        LoginPanel loginPanel = new LoginPanel(messageHandler);
        final ExpensePanel expensePanel = new ExpensePanel(messageHandler);
        panel.add(new CenteredPanel(loginPanel), "login");
        panel.add(new CenteredPanel(expensePanel), "expense");

        loginPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel, "expense");
                expensePanel.initialize();
            }
        });

        cardLayout.show(panel, "login");
        return panel;
    }
}
