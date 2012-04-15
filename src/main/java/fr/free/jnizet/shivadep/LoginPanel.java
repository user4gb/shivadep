package fr.free.jnizet.shivadep;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

public class LoginPanel extends JPanel {

    private MessageHandler messageHandler;
    private JTextField userTextField;
    private JPasswordField passwordField;
    private JButton okButton;

    public LoginPanel(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        setLayout(new MigLayout());
        add(new JLabel("Utilisateur :"), "align label");
        userTextField = new JTextField(10);
        add(userTextField, "wrap");
        add(new JLabel("Mot de passe :"), "align label");
        passwordField = new JPasswordField(10);
        add(passwordField, "wrap");
        okButton = new JButton("OK");
        add(okButton, "span 2, align right");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticate(userTextField.getText(), new String(passwordField.getPassword()));
            }
        });
    }

    private void authenticate(final String user, final String password) {
        SwingWorker<Boolean, Void> swingWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return Browser.getInstance().authenticate(user, password);
            }

            @Override
            protected void done() {
                try {
                    Boolean authenticated = get();
                    if (authenticated) {
                        fireActionEvent();
                    }
                    else {
                        messageHandler.displayError("L'authentification a échoué.");
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    messageHandler.displayError("L'authentification a échoué.");
                }
                catch (ExecutionException e) {
                    messageHandler.displayError("L'authentification a échoué.");
                }
                finally {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    okButton.setEnabled(true);
                }
            }
        };
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        messageHandler.displayInfo("Authentification en cours...");
        okButton.setEnabled(false);
        swingWorker.execute();
    }

    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }

    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }

    protected void fireActionEvent() {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login");
        for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
            listener.actionPerformed(e);
        }
    }
}
