/*-****************************-*\
*    Atikur Rahman Chitholian    *
*         Department of          *
* Computer Science & Engineering *
*      Session : 2015 - 2016     *
*    University of Chittagong    *
\*-****************************-*/

package chitholian.java.guilibs;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public abstract class DateChooser extends JDialog {
    public static String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private Button cancelButton;
    private Button chooseButton;
    private Button monthBack;
    private Button monthForward;
    private Button yearBack;
    private Button yearForward;
    private Button monthButton;
    private Button yearButton;
    private int currentMonth;
    private int currentYear;
    private int currentDate;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private DateChooser.DateButton selectedButton;
    private DateChooser.CalenderPane calenderPane;
    private DateChooser.DateToolBar dateToolBar;
    private boolean selected;
    private JLabel display;

    public DateChooser() {
        this.init();
    }

    public DateChooser(@NotNull JFrame owner) {
        super(owner);
        this.init();
    }

    public DateChooser(@NotNull JDialog owner) {
        super(owner);
        this.init();
    }

    private void init() {
        this.setResizable(false);
        this.setDefaultCloseOperation(2);
        this.setAlwaysOnTop(true);
        this.setTitle("Choose a Date - Chitholian Date Chooser");
        this.display = new JLabel();
        this.display.setFont(new Font("Monospaced", 1, 18));
        Calendar calendar = Calendar.getInstance();
        this.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
        this.cancelButton = new Button("Cancel", "Close the dialog.", new ImageIcon(this.getClass().getResource("/icons/cancel.png")));
        this.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DateChooser.this.dispose();
                DateChooser.this.onCancel();
            }
        });
        this.chooseButton = new Button("Choose", "Choose this date.", new ImageIcon(this.getClass().getResource("/icons/tick.png")));
        this.chooseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(DateChooser.this.selectedYear, DateChooser.this.selectedMonth - 1, DateChooser.this.selectedDay);
                selected = true;
                DateChooser.this.dispose();
                DateChooser.this.onDateSelect(calendar);
            }
        });
        this.calenderPane = new DateChooser.CalenderPane();
        this.dateToolBar = new DateChooser.DateToolBar();
        this.decorate();
    }

    private void decorate() {
        class Container extends JPanel {
            private Container() {
                this.setLayout(new GridBagLayout());
                GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 10, 0, new Insets(2, 2, 2, 2), 2, 2);
                this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                gc.gridwidth = 2;
                this.add(DateChooser.this.dateToolBar, gc);
                ++gc.gridy;
                this.add(DateChooser.this.calenderPane, gc);
                ++gc.gridy;
                gc.gridwidth = 1;
                gc.anchor = 17;
                this.add(DateChooser.this.cancelButton, gc);
                gc.anchor = 13;
                ++gc.gridx;
                this.add(DateChooser.this.chooseButton, gc);
            }
        }

        this.setContentPane(new Container());
    }

    public DateChooser setDate(int year, int month, int day) {
        this.selectedDay = this.currentDate = day;
        this.selectedMonth = this.currentMonth = month;
        this.selectedYear = this.currentYear = year;
        return this;
    }

    public void display(@Nullable Component onTopOf) {
        this.calenderPane.decorate();
        this.dateToolBar.decorate();
        this.pack();
        this.setLocationRelativeTo(onTopOf);
        this.setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (!this.selected)
            this.onCancel();
    }

    protected abstract void onCancel();

    protected abstract void onDateSelect(Calendar calendar);

    private abstract class SelectorList extends JDialog {
        private JList<Object> list;
        private boolean selected;

        private SelectorList(@Nullable String title, @Nullable DateChooser dialog, Object[] data, @Nullable Component onTopOf, int selectedIndex) {
            super(dialog);
            this.setTitle(title);
            JScrollPane pane = new JScrollPane(this.list = new JList<>(data));
            this.setContentPane(pane);
            this.list.setCellRenderer(new DateChooser.SelectorList.ListRenderer());
            this.list.setSelectedIndex(selectedIndex);
            this.pack();
            this.setLocationRelativeTo(onTopOf);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.list.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting() && SelectorList.this.list.getSelectedIndex() != -1) {
                        SelectorList.this.selected = true;
                        SelectorList.this.dispose();
                        SelectorList.this.onIndexChosen(SelectorList.this.list.getSelectedIndex());
                    }
                }
            });
        }

        public void dispose() {
            super.dispose();
            if (!this.selected) {
                this.onNothingChosen();
            }
        }

        abstract void onNothingChosen();

        abstract void onIndexChosen(int selectedIndex);

        class ListRenderer extends JLabel implements ListCellRenderer<Object> {
            private ListRenderer() {
                this.setOpaque(true);
            }

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                this.setText(value.toString());
                this.setFont(new Font("Monospaced", 1, 18));
                this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
                if (isSelected) {
                    this.setBackground(Color.CYAN);
                } else {
                    this.setBackground(Color.WHITE);
                }
                return this;
            }
        }
    }

    private class DayButton extends JButton {
        private DayButton(String day) {
            super(day);
            this.setFont(new Font("Monospaced", Font.BOLD, 18));
            this.setBackground(new Color(100, 100, 120));
            this.setEnabled(false);
        }
    }

    private class DateButton extends JButton {
        private DateButton(final int date) {
            super(date < 10 ? "0" + date : "" + date);
            this.setFont(new Font("Monospaced", Font.BOLD, 18));
            this.setBackground(Color.CYAN);
            this.setForeground(Color.BLACK);
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (DateChooser.this.selectedButton != null) {
                        DateChooser.this.selectedButton.setBackground(Color.CYAN);
                    }
                    DateChooser.this.selectedDay = date;
                    DateChooser.this.selectedMonth = DateChooser.this.currentMonth;
                    DateChooser.this.selectedYear = DateChooser.this.currentYear;
                    DateButton.this.setBackground(Color.PINK);
                    DateChooser.this.selectedButton = DateButton.this;
                    DateChooser.this.display.setText(DateChooser.monthNames[DateChooser.this.selectedMonth - 1] + " " + (DateChooser.this.selectedDay < 10 ? "0" + DateChooser.this.selectedDay : Integer.valueOf(DateChooser.this.selectedDay)) + ", " + DateChooser.this.selectedYear);
                }
            });
        }
    }

    class CalenderPane extends JPanel {
        DateChooser.DateButton[] dateButtons;
        DateChooser.DayButton[] dayButtons;
        int[] days;

        private CalenderPane() {
            this.days = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            this.init();
            this.decorate();
        }

        void init() {
            this.dateButtons = new DateChooser.DateButton[31];

            for (int i = 0; i < 31; ++i) {
                this.dateButtons[i] = DateChooser.this.new DateButton(i + 1);
            }

            this.dayButtons = new DateChooser.DayButton[]{DateChooser.this.new DayButton("SU"), DateChooser.this.new DayButton("MO"), DateChooser.this.new DayButton("TU"), DateChooser.this.new DayButton("WE"), DateChooser.this.new DayButton("TH"), DateChooser.this.new DayButton("FR"), DateChooser.this.new DayButton("SA")};
        }

        void decorate() {
            this.removeAll();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, DateChooser.this.currentYear);
            calendar.set(Calendar.MONTH, DateChooser.this.currentMonth - 1);
            calendar.set(Calendar.DATE, DateChooser.this.currentDate);
            this.setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints(0, 0, 7, 1, 1.0D, 1.0D, 10, 0, new Insets(2, 2, 2, 2), 2, 2);
            this.add(DateChooser.this.display, gc);
            ++gc.gridy;
            gc.gridwidth = 1;

            int remaining;
            for (remaining = 0; remaining < 7; ++remaining) {
                this.add(this.dayButtons[remaining], gc);
                ++gc.gridx;
            }

            ++gc.gridy;
            remaining = DateChooser.this.currentDate % 7;
            remaining = (((calendar.get(Calendar.DAY_OF_WEEK) - remaining) % 7) + 7) % 7;
            gc.gridx = remaining;

            for (int i = 0; i < this.days[DateChooser.this.currentMonth]; ++i) {
                this.add(this.dateButtons[i], gc);
                ++gc.gridx;
                if (gc.gridx % 7 == 0) {
                    gc.gridx = 0;
                    ++gc.gridy;
                }
            }

            if (DateChooser.this.currentMonth == 2 && DateChooser.this.currentYear % 4 == 0) {
                this.add(this.dateButtons[28], gc);
            }

            if (DateChooser.this.currentMonth == DateChooser.this.selectedMonth && DateChooser.this.currentYear == DateChooser.this.selectedYear) {
                if (DateChooser.this.selectedButton != null) {
                    DateChooser.this.selectedButton.setBackground(Color.CYAN);
                }

                (DateChooser.this.selectedButton = this.dateButtons[DateChooser.this.selectedDay - 1]).setBackground(Color.PINK);
            }

            if (DateChooser.this.currentMonth != DateChooser.this.selectedMonth || DateChooser.this.currentYear != DateChooser.this.selectedYear) {
                DateChooser.this.selectedButton.setBackground(Color.CYAN);
            }

            DateChooser.this.display.setText(DateChooser.monthNames[DateChooser.this.selectedMonth - 1] + " " + (DateChooser.this.selectedDay < 10 ? "0" + DateChooser.this.selectedDay : Integer.valueOf(DateChooser.this.selectedDay)) + ", " + DateChooser.this.selectedYear);
            this.updateUI();
        }
    }

    class DateToolBar extends JToolBar {
        private DateToolBar() {
            this.setFloatable(false);
            DateChooser.this.monthBack = new Button("<");
            DateChooser.this.monthForward = new Button(">");
            DateChooser.this.yearBack = new Button("<");
            DateChooser.this.yearForward = new Button(">");
            DateChooser.this.monthButton = new Button();
            DateChooser.this.yearButton = new Button();
            this.init();
            this.decorate();
        }

        void init() {
            this.setLayout(new GridBagLayout());
            DateChooser.this.monthBack.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DateChooser.this.currentMonth--;
                    if (DateChooser.this.currentMonth == 0) {
                        DateChooser.this.currentMonth = 12;
                        DateChooser.this.currentYear--;
                        DateChooser.this.yearButton.setText(DateChooser.this.currentYear + "");
                    }

                    DateChooser.this.calenderPane.decorate();
                    DateChooser.this.monthButton.setText(DateChooser.monthNames[DateChooser.this.currentMonth - 1]);
                    DateChooser.this.pack();
                }
            });
            DateChooser.this.monthForward.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DateChooser.this.currentMonth++;
                    if (DateChooser.this.currentMonth == 13) {
                        DateChooser.this.currentMonth = 1;
                        DateChooser.this.currentYear++;
                        DateChooser.this.yearButton.setText(DateChooser.this.currentYear + "");
                    }

                    DateChooser.this.calenderPane.decorate();
                    DateChooser.this.monthButton.setText(DateChooser.monthNames[DateChooser.this.currentMonth - 1]);
                    DateChooser.this.pack();
                }
            });
            DateChooser.this.yearBack.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (DateChooser.this.currentYear != 0) {
                        DateChooser.this.currentYear--;
                        DateChooser.this.yearButton.setText(DateChooser.this.currentYear + "");
                        DateChooser.this.calenderPane.decorate();
                        DateChooser.this.pack();
                    }

                }
            });
            DateChooser.this.yearForward.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (DateChooser.this.currentYear != Integer.MAX_VALUE) {
                        DateChooser.this.currentYear++;
                        DateChooser.this.yearButton.setText(DateChooser.this.currentYear + "");
                        DateChooser.this.calenderPane.decorate();
                        DateChooser.this.pack();
                    }

                }
            });
            DateChooser.this.monthButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    (new DateChooser.SelectorList("Select a Month", DateChooser.this, DateChooser.monthNames, DateChooser.this.monthButton, DateChooser.this.currentMonth - 1) {
                        void onIndexChosen(int index) {
                            DateChooser.this.currentMonth = index + 1;
                            DateChooser.this.monthButton.setText(DateChooser.monthNames[index]);
                            DateChooser.this.calenderPane.decorate();
                            DateChooser.this.pack();
                        }

                        void onNothingChosen() {
                        }
                    }).setVisible(true);
                }
            });
            DateChooser.this.yearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Integer[] years = new Integer[100];

                    int i;
                    for (i = 0; i < 50; ++i) {
                        years[i] = DateChooser.this.currentYear + 50 - i;
                    }

                    for (i = 50; i < 100; ++i) {
                        years[i] = DateChooser.this.currentYear + 50 - i;
                    }

                    (new DateChooser.SelectorList("Select an Year", DateChooser.this, years, DateChooser.this.yearButton, 50) {
                        void onIndexChosen(int index) {
                            DateChooser.this.currentYear = DateChooser.this.currentYear + (50 - index);
                            DateChooser.this.yearButton.setText(DateChooser.this.currentYear + "");
                            DateChooser.this.calenderPane.decorate();
                            DateChooser.this.pack();
                        }

                        void onNothingChosen() {
                        }
                    }).setVisible(true);
                }
            });
        }

        private void decorate() {
            this.removeAll();
            GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(2, 2, 2, 2), 2, 2);
            this.setPreferredSize(new Dimension((int) DateChooser.this.calenderPane.getPreferredSize().getWidth(), 45));
            DateChooser.this.monthButton.setText(DateChooser.monthNames[DateChooser.this.currentMonth - 1]);
            DateChooser.this.yearButton.setText(DateChooser.this.currentYear + "");
            this.add(DateChooser.this.monthBack, gc);
            ++gc.gridx;
            this.add(DateChooser.this.monthButton, gc);
            ++gc.gridx;
            this.add(DateChooser.this.monthForward, gc);
            ++gc.gridx;
            gc.anchor = 13;
            this.add(DateChooser.this.yearBack, gc);
            ++gc.gridx;
            this.add(DateChooser.this.yearButton, gc);
            ++gc.gridx;
            this.add(DateChooser.this.yearForward, gc);
        }
    }

    private class Button extends JButton {

        private Button() {
            this(null, null, null);
        }

        private Button(String text) {
            this(text, null, null);
        }

        private Button(String text, String toolTip, ImageIcon icon) {
            super(text);
            if (toolTip != null) setToolTipText(toolTip);
            if (icon != null) setIcon(icon);
            setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
            setBackground(new Color(200, 210, 220));
        }
    }
}
