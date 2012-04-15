package fr.free.jnizet.shivadep;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.time.DateUtils;

public class ExpensePanel extends JPanel {

    private MessageHandler messageHandler;
    private JComboBox projectCombo;
    private JTextField descriptionTextField;
    private JSpinner kmSpinner;
    private JComboBox monthCombo;
    private JButton createExpensesButton;
    private DateChooser dateChooser;
    private boolean creating = false;

    public ExpensePanel(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        setLayout(new MigLayout());

        add(new JLabel("Projet :"), "align label");
        projectCombo = new JComboBox();
        add(projectCombo, "width 700px, wrap");

        add(new JLabel("Description :"), "align label");
        descriptionTextField = new JTextField(20);
        add(descriptionTextField, "wrap");

        add(new JLabel("Nombre de km :"), "align label");
        kmSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        add(kmSpinner, "wrap");

        add(new JLabel("Mois :"), "align label");
        Date currentMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
        monthCombo = new JComboBox(new Object[] {currentMonth,
                                                DateUtils.addMonths(currentMonth, -1),
                                                DateUtils.addMonths(currentMonth, -2),
                                                DateUtils.addMonths(currentMonth, -3),
                                                DateUtils.addMonths(currentMonth, -4)});
        monthCombo.setSelectedIndex(0);
        monthCombo.setRenderer(new DefaultListCellRenderer() {
            private DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Date month = (Date) value;
                setText(dateFormat.format(month));
                return this;
            }
        });
        add(monthCombo, "wrap");

        add(new JLabel("Dates :"), "aligny top, alignx label");
        dateChooser = new DateChooser(new Date());
        add(dateChooser, "wrap");

        createExpensesButton = new JButton("Créer les dépenses");
        add(createExpensesButton, "span 2, align right");

        refreshCreateExpensesButton();
        initListeners();
    }

    public void initialize() {
        loadProjects();
    }

    private void loadProjects() {
        SwingWorker<List<Option>, Void> swingWorker = new SwingWorker<List<Option>, Void>() {
            @Override
            protected List<Option> doInBackground() throws Exception {
                return Browser.getInstance().loadProjects();
            }

            @Override
            protected void done() {
                try {
                    List<Option> options = get();
                    projectCombo.setModel(new DefaultComboBoxModel(options.toArray(new Option[options.size()])));
                    messageHandler.clear();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    messageHandler.displayError("Le chargement des projets a échoué.");
                }
                catch (ExecutionException e) {
                    messageHandler.displayError("Le chargement des projets a échoué.");
                }
                finally {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    refreshCreateExpensesButton();
                }
            }
        };

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        messageHandler.displayInfo("Chargement des projets en cours...");
        swingWorker.execute();
    }

    private void initListeners() {
        monthCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateChooser.setMonth((Date) monthCombo.getSelectedItem());
            }
        });

        descriptionTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshCreateExpensesButton();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshCreateExpensesButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshCreateExpensesButton();
            }
        });

        dateChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshCreateExpensesButton();
            }
        });

        createExpensesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createExpenses();
            }
        });
    }

    private void createExpenses() {
        final List<Date> selectedDates = dateChooser.getSelectedDates();
        final String description = descriptionTextField.getText();
        final String project = ((Option) projectCombo.getSelectedItem()).getValue();
        final int km = (Integer) kmSpinner.getValue();
        final ProgressMonitor progressMonitor = new ProgressMonitor(this,
                                                                    "Création des dépenses en cours...",
                                                                    "Création des dépenses en cours. Veuillez patienter...",
                                                                    0,
                                                                    100);
        progressMonitor.setMillisToDecideToPopup(0);
        progressMonitor.setMillisToPopup(0);
        progressMonitor.setProgress(0);

        SwingWorker<Void, Date> swingWorker = new SwingWorker<Void, Date>() {
            @Override
            protected Void doInBackground() throws Exception {
                int i = 0;
                for (Date date : selectedDates) {
                    publish(date);
                    Browser.getInstance().createExpense(date, description, project, km);
                    i++;
                    setProgress((int) (100 * ((double) i / selectedDates.size())));
                }
                return null;
            }

            @Override
            protected void process(List<Date> chunks) {
                if (chunks.size() > 0) {
                    Date lastDate = chunks.get(chunks.size() - 1);
                    String lastDateAsString = new SimpleDateFormat("dd MMM yyyy").format(lastDate);
                    progressMonitor.setNote("Dépense pour le " + lastDateAsString + " en cours de création...");
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    messageHandler.clear();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    messageHandler.displayError("La création d'une dépense à échoué.");
                }
                catch (ExecutionException e) {
                    messageHandler.displayError("La création d'une dépense à échoué.");
                }
                finally {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    creating = false;
                    progressMonitor.close();
                    refreshCreateExpensesButton();
                }
            }
        };
        creating = true;
        refreshCreateExpensesButton();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        messageHandler.displayInfo("Création des dépenses en cours...");
        swingWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    progressMonitor.setProgress((Integer) evt.getNewValue());
                }
            }
        });
        swingWorker.execute();
    }

    private void refreshCreateExpensesButton() {
        createExpensesButton.setEnabled(dateChooser.isDateSelected()
                                     && !descriptionTextField.getText().trim().isEmpty()
                                     && projectCombo.getSelectedItem() != null
                                     && !creating);
    }
}
