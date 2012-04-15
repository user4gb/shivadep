package fr.free.jnizet.shivadep;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.time.DateUtils;

public class DateChooser extends JPanel {
    private Date month;
    private List<JToggleButton> dateToggleButtons;

    public DateChooser(Date mois) {
        this.month = DateUtils.truncate(mois, Calendar.MONTH);

        setLayout(new MigLayout("insets 0"));
        dateToggleButtons = new ArrayList<JToggleButton>(31);
        add(new JLabel("Lu"), "align center");
        add(new JLabel("Ma"), "align center");
        add(new JLabel("Me"), "align center");
        add(new JLabel("Je"), "align center");
        add(new JLabel("Ve"), "align center");
        add(new JLabel("Sa"), "align center");
        add(new JLabel("Di"), "align center, wrap");

        // 6 lines of 7 dates
        for (int i = 0; i < 42; i++) {
            JToggleButton dateTogleButton = new JToggleButton();
            dateToggleButtons.add(dateTogleButton);
            String constraint = "grow";
            if (i > 0 && (i + 1) % 7 == 0) {
                constraint = "grow, wrap";
            }
            add(dateTogleButton, constraint);
        }
        refreshDateToggleButtons();

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                fireActionEvent();
            }
        };
        for (JToggleButton dateToggleButton : dateToggleButtons) {
            dateToggleButton.addActionListener(listener);
        }
    }

    private void refreshDateToggleButtons() {
        for (JToggleButton dateToggleButton : dateToggleButtons) {
            dateToggleButton.setEnabled(true);
            dateToggleButton.setText(" ");
            dateToggleButton.setSelected(false);
        }

        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        Date firstDayOfMonth = month;
        int indexOfFirstDay = getIndexFromMonday(firstDayOfMonth);
        for (int i = 0; i < indexOfFirstDay; i++) {
            dateToggleButtons.get(i).setEnabled(false);
        }
        int j = 0;
        for (int i = indexOfFirstDay; i < dateToggleButtons.size(); i++) {
            Date currentDate = DateUtils.addDays(firstDayOfMonth, j);
            if (DateUtils.truncatedEquals(month, currentDate, Calendar.MONTH)) {
                dateToggleButtons.get(i).setText(Integer.toString(j + 1));
                int dayOfWeek = getDayOfWeek(currentDate);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    dateToggleButtons.get(i).setEnabled(false);
                }
                if (currentDate.compareTo(today) > 0) {
                    dateToggleButtons.get(i).setEnabled(false);
                }
            }
            else {
                dateToggleButtons.get(i).setEnabled(false);
            }
            j++;
        }


        fireActionEvent();
    }

    public void setMonth(Date month) {
        this.month = DateUtils.truncate(month, Calendar.MONTH);
        refreshDateToggleButtons();
    }

    private int getIndexFromMonday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int index = c.get(Calendar.DAY_OF_WEEK) -  Calendar.MONDAY;
        if (index < 0) {
            index += 7;
        }
        return index;
    }

    private int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }

    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }

    protected void fireActionEvent() {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "selectedDates");
        for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
            listener.actionPerformed(e);
        }
    }

    public List<Date> getSelectedDates() {
        List<Date> result = new ArrayList<Date>();
        for (JToggleButton dateToggleButton : dateToggleButtons) {
            if (dateToggleButton.isEnabled() && dateToggleButton.isSelected()) {
                String text = dateToggleButton.getText();
                int dayOfMonth = Integer.parseInt(text);
                Date d = DateUtils.addDays(month, dayOfMonth - 1);
                result.add(d);
            }
        }
        return result;
    }

    public boolean isDateSelected() {
        for (JToggleButton dateToggleButton : dateToggleButtons) {
            if (dateToggleButton.isEnabled() && dateToggleButton.isSelected()) {
                return true;
            }
        }
        return false;
    }
}
