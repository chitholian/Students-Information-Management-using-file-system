package team7.cu.sim;

import team7.cu.comps.*;
import team7.cu.utils.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Chitholian on 6/24/2017.
 */
public class StudentList extends ScrollableList {
    private ArrayList<Student> students;
    private ListOption option;
    private JProgressBar progress;
    private MyButton cancelBtn;
    private ListThread thread;
    private Student.SearchConstraints searchConstraints;

    public StudentList() {
        super();
        option = ListOption.SHOW_ALL;
        title = "List of Available Students";
        decorate();
    }

    @Override
    protected void init() {
        super.init();
        students = new ArrayList<>();
        progress = new JProgressBar(JProgressBar.VERTICAL);
        progress.setVisible(false);
        cancelBtn = new MyButton(null, "Stop Loading", new ImageIcon(getClass().getResource("/icons/cancel.png")));

        // Set Cell renderer
        list.setCellRenderer(new StudentListRenderer());
        list.setVisibleRowCount(5);
        scrollPane.setMinimumSize(new Dimension(700, 600));
        scrollPane.setPreferredSize(new Dimension(700, 600));
        progress.setMinimumSize(new Dimension((int) cancelBtn.getPreferredSize().getWidth(), 500));
        progress.setPreferredSize(new Dimension((int) cancelBtn.getPreferredSize().getWidth(), 500));
        // Add Action Listeners
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBtn.setEnabled(false);
                new Dialogs.Builder((MainFrame) container).onDispose(new Dialogs.DisposeListener() {
                    @Override
                    public void listen() {
                        addBtn.setEnabled(true);
                    }
                }).setPanel(new AddStudentForm()).build(scrollPane).setVisible(true);
            }
        });

        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddStudentForm addStudentForm = new AddStudentForm();
                addStudentForm.populateFrom((Student) list.getSelectedValue());
                new Dialogs.Builder((MainFrame) container).setPanel(addStudentForm).build(scrollPane).setVisible(true);
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Dialogs.confirm(deleteBtn, "Are you sure to delete permanently?")) return;
                HashSet<Integer> selectedIds = new HashSet<>();
                for (Object std : list.getSelectedValuesList()) {
                    selectedIds.add(((Student) std).id);
                }
                Student.delete(selectedIds, DataChangeOption.STUDENTS);
                refresh();
                StudentList.super.notifyDataChanged(new DataChangeOption(DataChangeOption.STUDENTS, System.currentTimeMillis()));
            }
        });

        detailsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dialogs.Builder((MainFrame) container).setPanel(new ViewDetails((Student) list.getSelectedValue()))
                        .build(scrollPane).setVisible(true);
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thread.close();
                cancelBtn.setVisible(false);
                progress.setVisible(false);
            }
        });
        cancelBtn.setVisible(false);
    }

    /**
     * Set option for the list
     *
     * @param option "ListOption.SHOW_SEARCH_RESULTS" if want to show search results only
     *               also set search results calling populateWith() method first.
     *               "ListOption.SHOW_ALL" to show all students.
     */
    public void setListOption(ListOption option) {
        this.option = option;
    }

    @Override
    public void decorate() {
        // Insert Cancel button
        gc.anchor = GridBagConstraints.SOUTHEAST;
        gc.weighty = 0;
        gc.gridy = 1;
        insert(cancelBtn);

        // insert progress bar
        gc.gridy++;
        insert(progress);

        // insert scroll pane
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx++;
        gc.gridwidth = 5;
        gc.gridheight = 3;
        gc.gridy = 0;
        insert(scrollPane);
        insert(emptyText);

        // insert buttons
        gc.gridy += 3;
        gc.gridheight = 1;
        gc.gridwidth = 1;
        insert(addBtn);
        gc.gridx++;
        insert(deleteBtn);
        gc.gridx++;
        insert(editBtn);
        gc.gridx++;
        insert(detailsBtn);
        gc.gridx++;
        insert(refreshBtn);
    }

    /**
     * Populates list items from the given array list.
     * This method will not work properly unless "option" is set to "ListOption.SHOW_SEARCH_RESULTS" explicitly.
     *
     * @param students ArrayList of Student entity from which data should be displayed
     */
    public void populateWith(ArrayList<Student> students) {
        this.students = students;
        refresh();
    }

    public void populateWith(Student.SearchConstraints constraints) {
        searchConstraints = constraints;
        refresh();
    }

    @Override
    public void refresh() {
        if (option.equals(ListOption.SHOW_ALL)) { // Retrieve all data
            students = Student.getAll();
        } else {
            students = Student.getAll(searchConstraints);
        }
        if (students.size() == 0) { // Empty !
            triggerEmptyList();
            return;
        }
        triggerNonEmptyList();
        (thread = new ListThread() {
            @Override
            public void atTheEnd() {
                progress.setVisible(false);
                cancelBtn.setVisible(false);
                list.updateUI();
            }

            @Override
            public void onClose() {
                list.updateUI();
                updateUI();
            }
        }).startThread();
    }

    @Override
    public void notifyDataChanged(DataChangeOption changeOption) {
        if (changeOption.opt == DataChangeOption.STUDENTS && changeOption.nid != notificationId) refresh();
        super.notifyDataChanged(changeOption);
    }

    public enum ListOption {
        SHOW_ALL, SHOW_SEARCH_RESULTS
    }

    /* *************************** */
    protected class ListThread extends Thread {
        private volatile Thread controller;

        public synchronized void startThread() {
            controller = new Thread(this);
            controller.start();
            // System.out.println("Starting...");
        }

        public void close() {
            controller = null;
            onClose();
        }

        public void atTheEnd() {
        }

        public void onClose() {
        }

        @Override
        public void run() {
            //list.setListData(students.toArray());
            Thread current = Thread.currentThread();
            // Show progression
            progress.setMinimum(0);
            progress.setMaximum(students.size());
            progress.setVisible(true);
            cancelBtn.setVisible(true);

            // Clean up old data
            listModel.removeAllElements();

            int counter = 0;
            while (controller == current && counter < students.size()) {
                listModel.addElement(students.get(counter++));
                progress.setValue(counter);
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Invoke at the end.
            atTheEnd();
        }
    }

    /* *************************** */
    protected class StudentListRenderer extends MyPanel implements ListCellRenderer<Object> {
        public StudentListRenderer() {
            super();
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            removeAll();
            gc.anchor = GridBagConstraints.NORTHWEST;
            setBorder(null);
            if (value == null) return this;
            MyPanel dataPanel = new MyPanel() {
                @Override
                protected void init() {
                    super.init();
                    gc.anchor = GridBagConstraints.LINE_START;
                    // Insert picture
                    Student student = (Student) value; // Get current student
                    StudentListRenderer.this.setToolTipText(student.id + "");
                    gc.gridheight = 2; // Span two rows
                    MyLabel pic = new MyLabel();
                    pic.setSize(new Dimension(50, 60)); // Set a fixed size
                    // Load the image
                    pic.setIcon(new ImageIcon(new ImageIcon(Helper.AVATAR_LOCATION + student.id + ".png").getImage()
                            .getScaledInstance(50, 60, Image.SCALE_DEFAULT)));
                    insert(pic);

                    // Insert name
                    gc.gridheight = 1;
                    gc.gridx++;
                    gc.gridwidth = 2; // Spans two columns
                    MyLabel name = new MyLabel(student.name);
                    name.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
                    insert(name);

                    // Insert session
                    gc.gridwidth = 1;
                    gc.gridy++;
                    insert(new MyLabel(student.session));

                    // Insert department
                    gc.anchor = GridBagConstraints.LINE_END;
                    if (student.dept != null) {
                        gc.gridx++;
                        insert(new MyLabel(student.dept.fullName));
                    }
                }
            };

            // Set backgrounds
            if (isSelected) {
                dataPanel.setBackground(Color.LIGHT_GRAY);
                setBackground(Color.LIGHT_GRAY);
            } else {
                dataPanel.setBackground(Color.WHITE);
                setBackground(Color.WHITE);
            }

            // Insert it
            insert(dataPanel);

            return this;
        }
    }
}
