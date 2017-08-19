package team7.cu.sim;

import team7.cu.comps.Dialogs;
import team7.cu.comps.MyLabel;
import team7.cu.comps.ScrollableList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class DeptList extends ScrollableList {
    public DeptList() {
        super();
        decorate();
        title = "List of Available Departments";
    }

    @Override
    protected void init() {
        super.init();
        // Set cell renderer
        list.setCellRenderer(new DeptListRenderer());
        // list.setVisibleRowCount(15);
        // Set action listener to the buttons
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBtn.setEnabled(false);
                new Dialogs.Builder((MainFrame) container).onDispose(new Dialogs.DisposeListener() {
                    @Override
                    public void listen() {
                        addBtn.setEnabled(true);
                    }
                }).setPanel(new AddDeptForm()).build(addBtn).setVisible(true);
            }
        });

        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddDeptForm addDeptForm = new AddDeptForm();
                addDeptForm.populateFrom((Department) list.getSelectedValue());
                new Dialogs.Builder((MainFrame) container).setPanel(addDeptForm).build(editBtn).setVisible(true);
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Dialogs.confirm(deleteBtn, "Are you sure to delete permanently?")) return;
                HashSet<Integer> selectedIds = new HashSet<>();
                for (Object dept : list.getSelectedValuesList()) {
                    selectedIds.add(((Department) dept).getId());
                }
                Department.delete(selectedIds);
                refresh();
                DeptList.super.notifyDataChanged(new DataChangeOption(DataChangeOption.DEPARTMENT, System.currentTimeMillis()));
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
    }

    @Override
    public void decorate() {
        gc.gridwidth = 4;
        insert(scrollPane);
        insert(emptyText);
        gc.gridy++;
        gc.gridwidth = 1;
        insert(addBtn);
        gc.gridx++;
        insert(deleteBtn);
        gc.gridx++;
        insert(editBtn);
        gc.gridx++;
        insert(refreshBtn);
    }

    @Override
    public void refresh() {
        ArrayList<Department> departments = Department.getAll();
        if (departments.size() == 0) {
            triggerEmptyList(); // Disable edit & delete button, show empty text, hide the list.
            return;
        }
        triggerNonEmptyList(); // show the list
        // Sort by name
        departments.sort(new Comparator<Department>() {
            @Override
            public int compare(Department o1, Department o2) {
                return o1.fullName.compareTo(o2.fullName);
            }
        });
        listModel.removeAllElements();
        for (Department department : departments) {
            listModel.addElement(department);
        }
        list.updateUI();
        scrollPane.updateUI();
    }

    @Override
    public void notifyDataChanged(DataChangeOption changeOption) {
        refresh();
        super.notifyDataChanged(changeOption);
    }

    /* *********************** */
    private class DeptListRenderer extends MyLabel implements ListCellRenderer<Object> {
        public DeptListRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.toString());
            setFont(new Font(Font.DIALOG, Font.BOLD, 20));
            setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
            //if (!list.getValueIsAdjusting()) {
            // BGs
            if (isSelected) {
                setBackground(Color.LIGHT_GRAY);
                //setForeground(Color.BLACK);
            } else {
                setBackground(Color.WHITE);
                //setForeground(Color.BLACK);
            }
            //}
            return this;
        }
    }
}
