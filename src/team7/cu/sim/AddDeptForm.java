package team7.cu.sim;

import team7.cu.comps.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class AddDeptForm extends MyPanel {
    private MyTextField deptFullName, deptShortName;
    private Department department;

    public AddDeptForm() {
        super("Add New Department");
        decorate();
    }

    @Override
    public void decorate() {
        removeAll();
        gc = getDefaultConstraints();
        gc.anchor = GridBagConstraints.LINE_START;
        insert(new MyLabel("Full Name", "Full Name of the Department", null));
        gc.gridx = 1;
        deptFullName = new MyTextField();
        insert(deptFullName);

        gc.gridy = 1;
        gc.gridx = 0;
        insert(new MyLabel("Short Name", "Short Name of the Department", null));
        gc.gridx = 1;
        deptShortName = new MyTextField();
        insert(deptShortName);

        MyButton submitBtn = new MyButton("Submit");
        gc.gridy++;
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String fName = deptFullName.getText().trim(), sName = deptShortName.getText().trim();
                        if (fName.isEmpty()) {
                            Dialogs.alert(submitBtn, "Please enter full name");
                            deptFullName.requestFocusInWindow();
                            return;
                        }
                        if (Dialogs.confirm(submitBtn, "Are you sure to submit ?")) {
                            if (department == null)
                                department = new Department(fName, sName);
                            else {
                                department.fullName = fName;
                                department.shortName = sName;
                            }
                            if (department.getId() == 0 && Department.getByName(fName) != null && !Dialogs.confirm(submitBtn, "Department with this name already exists. Add Duplicate ?"))
                                return;
                            department.save();
                            AddDeptForm.super.notifyDataChanged(new DataChangeOption(DataChangeOption.DEPARTMENT, System.currentTimeMillis()));
                            // ((CDialog) container).notifyDataUpdated();
                            if (department.getId() == 0)
                                Dialogs.alert(submitBtn, "Operation Successful !");
                            else if (container instanceof Dialogs.Builder) ((Dialogs.Builder) container).dispose();
                        }
                    }
                });
            }
        });
        insert(submitBtn);
    }

    public void populateFrom(Department department) {
        deptFullName.setText(department.fullName);
        deptShortName.setText(department.shortName);
        this.department = department;
        title = "Edit - " + department.fullName;
    }
}
