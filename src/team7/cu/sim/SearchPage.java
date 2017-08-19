package team7.cu.sim;

import chitholian.java.guilibs.DateChooser;
import team7.cu.comps.*;
import team7.cu.utils.MyDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;

import static team7.cu.sim.Student.*;

/**
 * Created by Chitholian on 6/24/2017.
 */
public class SearchPage extends MyPanel {
    private StudentList studentList;
    private SearchPanel searchPanel;

    public SearchPage() {
        super("Search Students");
    }

    @Override
    protected void init() {
        super.init();
        insert(searchPanel = new SearchPanel());
        gc.gridx++;

        insert(studentList = new StudentList());
        studentList.setListOption(StudentList.ListOption.SHOW_SEARCH_RESULTS);
        // studentList.refresh();

    }

    @Override
    public void notifyDataChanged(DataChangeOption changeOption) {
        if (changeOption.nid != notificationId)
            if (changeOption.opt == DataChangeOption.STUDENTS)
                studentList.notifyDataChanged(changeOption);
            else if (changeOption.opt == DataChangeOption.DEPARTMENT)
                searchPanel.notifyDataChanged(changeOption);

        super.notifyDataChanged(changeOption);
    }

    /* ************************ */
    private class SearchPanel extends MyPanel {
        private MyDropdown sessionOpt, sessionStart, sessionEnd, birthOpt;
        private MyButton birthStart, birthEnd, searchButton;
        private MyDropdown department, sortBy, sortOpt;
        private MyTextField keywordField;
        // private SearchButtonListener searchButtonListener;

        public SearchPanel() {
            super();
            // create options
            sessionOpt = new MyDropdown(new String[]{"All", "Before", "After", "Between", "Exactly"});
            birthOpt = new MyDropdown(new String[]{"All", "Before", "After", "Between"});
            sortBy = new MyDropdown(new String[]{"Name", "Student ID", "Session", "Birth Date", "Department"});
            sortOpt = new MyDropdown(new String[]{"Ascending", "Descending"});

            // Create sessions0
            final int currentYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
            String[] sessions = new String[Math.max(0, currentYear - 1964)];
            for (int i = 0; i < currentYear - 1964; i++) {
                sessions[i] = (currentYear - i) + " - " + (currentYear - i + 1);
            }
            sessionStart = new MyDropdown(sessions);
            sessionEnd = new MyDropdown(sessions);

            // Create birth date buttons
            birthStart = new MyButton("Select");
            birthStart.setBackground(Color.WHITE);
            birthEnd = new MyButton("Select");
            birthEnd.setBackground(Color.WHITE);

            // Keep them hidden
            sessionStart.setVisible(false);
            sessionEnd.setVisible(false);
            birthStart.setVisible(false);
            birthEnd.setVisible(false);

            // Departments
            department = new MyDropdown(new String[]{"All"});
            for (Department dept : Department.getAll()) {
                department.addItem(dept);
            }

            // Create Search Field
            keywordField = new MyTextField();

            // Create search button
            searchButton = new MyButton("Search");
            searchButton.setIcon(new ImageIcon(getClass().getResource("/icons/search.png")));

            // Add action listeners
            birthStart.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    birthStart.setEnabled(false); // disable the button
                    // create date chooser dialog
                    DateChooser dialog = new DateChooser(Launcher.frame) {
                        @Override
                        protected void onCancel() {
                            birthStart.setEnabled(true);
                        }

                        @Override
                        protected void onDateSelect(Calendar calendar) {
                            birthStart.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" +
                                    calendar.get(Calendar.DATE));
                            birthStart.setEnabled(true);
                        }
                    };
                    // try to set the already selected date
                    try {
                        if (birthStart.getText().charAt(0) != 'S') {
                            MyDate date = MyDate.build(birthStart.getText());
                            dialog.setDate(date.get(MyDate.YEAR), date.get(MyDate.MONTH), date.get(MyDate.DATE));
                        }
                    } catch (MyDate.InvalidDateException e1) {
                        e1.printStackTrace();
                    }
                    // show the dialog
                    dialog.display(birthStart);
                }
            });

            birthEnd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    birthEnd.setEnabled(false); // disable the button
                    // create date chooser dialog
                    DateChooser dialog = new DateChooser(Launcher.frame) {
                        @Override
                        protected void onCancel() {
                            birthEnd.setEnabled(true);
                        }

                        @Override
                        protected void onDateSelect(Calendar calendar) {
                            birthEnd.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" +
                                    calendar.get(Calendar.DATE));
                            birthEnd.setEnabled(true);
                        }
                    };
                    // try to set the already selected date
                    try {
                        if (birthEnd.getText().charAt(0) != 'S') {
                            MyDate date = MyDate.build(birthEnd.getText());
                            dialog.setDate(date.get(MyDate.YEAR), date.get(MyDate.MONTH), date.get(MyDate.DATE));
                        }
                    } catch (MyDate.InvalidDateException e1) {
                        e1.printStackTrace();
                    }
                    // show the dialog
                    dialog.display(birthEnd);
                }
            });

            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    studentList.populateWith(buildConstraints());
                }

                // Anonymous function

                private SearchConstraints buildConstraints() {
                    SearchConstraints searchConstraints = new SearchConstraints();
                    SearchOption[] options = new SearchOption[]{SearchOption.ALL, SearchOption.BEFORE, SearchOption.AFTER,
                            SearchOption.BETWEEN, SearchOption.EXACTLY};
                    // Get options by index
                    searchConstraints.dob = options[birthOpt.getSelectedIndex()];
                    searchConstraints.session = options[sessionOpt.getSelectedIndex()];
                    if (department.getSelectedIndex() > 0)
                        searchConstraints.department = (Department) department.getSelectedItem();

                    // Get option values
                    searchConstraints.sessionStart = sessionStart.getSelectedItem().toString();
                    searchConstraints.sessionEnd = sessionEnd.getSelectedItem().toString();
                    if (searchConstraints.sessionStart.compareTo(searchConstraints.sessionEnd) > 0) { // Swap them
                        String tmp = searchConstraints.sessionEnd;
                        searchConstraints.sessionEnd = searchConstraints.sessionStart;
                        searchConstraints.sessionStart = tmp;
                    }
                    // Try to get birth date conditions
                    try {
                        if (birthStart.getText().charAt(0) != 'S')
                            searchConstraints.dobStart = MyDate.build(birthStart.getText());
                        if (birthEnd.getText().charAt(0) != 'S')
                            searchConstraints.dobEnd = MyDate.build(birthEnd.getText());

                        if (birthStart.getText().compareTo(birthEnd.getText()) > 0) { // Swap them
                            MyDate date = searchConstraints.dobEnd;
                            searchConstraints.dobEnd = searchConstraints.dobStart;
                            searchConstraints.dobStart = date;
                        }
                    } catch (MyDate.InvalidDateException ide) {
                        ide.printStackTrace();
                    }

                    // Sorting
                    SortBy[] sorts = new SortBy[]{SortBy.NAME, SortBy.ID, SortBy.SESSION, SortBy.BIRTH, SortBy.DEPARTMENT};
                    searchConstraints.sortBy = sorts[sortBy.getSelectedIndex()];
                    if (sortOpt.getSelectedIndex() == 0) searchConstraints.sortOpt = SortOpt.ASC;
                    else searchConstraints.sortOpt = SortOpt.DESC;

                    // Get keywords
                    searchConstraints.keyword = keywordField.getText();

                    // return it
                    return searchConstraints;
                }
            });

            // Create option listeners
            sessionOpt.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (sessionOpt.getSelectedIndex() != 0) // !All
                            sessionStart.setVisible(true);
                        else
                            sessionStart.setVisible(false);

                        if (sessionOpt.getSelectedIndex() == 3) // Between
                            sessionEnd.setVisible(true);
                        else
                            sessionEnd.setVisible(false);
                    }
                }
            });

            birthOpt.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (birthOpt.getSelectedIndex() != 0) // !All
                            birthStart.setVisible(true);
                        else
                            birthStart.setVisible(false);

                        if (birthOpt.getSelectedIndex() == 3) // Between
                            birthEnd.setVisible(true);
                        else
                            birthEnd.setVisible(false);
                    }
                }
            });


            // Add to surface
            decorate();
        }

        @Override
        public void decorate() {
            removeAll();
            // make a title text
            MyLabel titleText = new MyLabel("Preconditions of Searching");
            titleText.setFont(new Font(null, Font.BOLD, 20));
            gc.gridwidth = 3;
            insert(titleText);

            gc.anchor = GridBagConstraints.LINE_START;
            gc.gridy++;
            gc.gridwidth = 1;

            // Session Constraints
            insert(new MyLabel("Session"));
            gc.gridx++;
            insert(sessionOpt);

            gc.gridy++;
            insert(sessionEnd);
            gc.gridx--;
            insert(sessionStart);

            gc.gridy++;

            // Birth date gc
            insert(new MyLabel("Birth Date"));
            gc.gridx++;
            insert(birthOpt);

            gc.gridy++;
            insert(birthEnd);
            gc.gridx--;
            insert(birthStart);

            gc.gridy++;

            // Department
            insert(new MyLabel("Department"));
            gc.gridwidth = 2;
            gc.gridy++;
            insert(department);

            gc.gridy++;

            // Sorting Options
            gc.gridwidth = 1;
            insert(new MyLabel("Sort By"));
            gc.gridx++;
            insert(sortBy);
            gc.gridx++;
            insert(sortOpt);
            gc.gridx -= 2;
            gc.gridy++;

            // Search Keyword Box & Button
            gc.gridwidth = 2;
            keywordField.setMinimumSize(department.getPreferredSize());
            keywordField.setPreferredSize(department.getPreferredSize());
            insert(keywordField);
            gc.gridwidth = 1;
            gc.gridx += 2;
            insert(searchButton);
        }

        @Override
        public void refresh() {
            // Departments
            department = new MyDropdown(new String[]{"All"});
            for (Department dept : Department.getAll()) {
                department.addItem(dept);
            }
            decorate();
            updateUI();
        }

        @Override
        public void notifyDataChanged(DataChangeOption changeOption) {
            refresh();
            super.notifyDataChanged(changeOption);
        }
    }
}
