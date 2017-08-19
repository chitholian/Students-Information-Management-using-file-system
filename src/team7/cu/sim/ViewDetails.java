package team7.cu.sim;

import team7.cu.comps.Dialogs;
import team7.cu.comps.MyButton;
import team7.cu.comps.MyLabel;
import team7.cu.comps.MyPanel;
import team7.cu.utils.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Chitholian on 6/24/2017.
 * Show an individual student details
 */
public class ViewDetails extends MyPanel {
    private MyButton editBtn;
    private Student student; // student to show info of

    public ViewDetails(Student student) {
        this.student = student;
        title = student.name;
        decorate();
    }

    /**
     * Here we show those fields only who are not null;
     * Null values are not to be shown.
     */
    @Override
    public void decorate() {
        removeAll();

        if (student != null) {
            title = student.name; // update title
            // reset layout cell position to (0, 0)
            gc.gridx = gc.gridy = 0;
            // try to show avatar
            try {
                File pic = new File(Helper.AVATAR_LOCATION + student.id + ".png");
                // show this iff exists
                if (pic.isFile()) {
                    MyLabel avatar = new MyLabel(null, null, null);
                    // set sizes
                    avatar.setSize(new Dimension(175, 200));
                    avatar.setMinimumSize(new Dimension(175, 200));
                    //avatar.setPreferredSize(new Dimension(175, 200));
                    // set the icon
                    avatar.setIcon(new ImageIcon(new ImageIcon(pic.getAbsolutePath()).getImage().getScaledInstance(avatar.getWidth(), avatar.getHeight(), Image.SCALE_DEFAULT)));

                    // set gc for picture
                    gc.gridwidth = 3; // Spans three cells
                    gc.anchor = GridBagConstraints.CENTER;
                    // add to surface
                    insert(avatar);
                    gc.gridy++;

                    // restore gc
                    gc.gridwidth = 1;
                    gc.gridx = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            gc.anchor = GridBagConstraints.LINE_START;
            insert("Student ID", String.valueOf(student.id));
            insert("Name", student.name);
            if (student.session != null) insert("Session", student.session);
            if (student.dept != null) insert("Department", student.dept.toString());
            if (student.birth != null) insert("Date of Birth", student.birth.toString());
            if (student.father != null) insert("Father's Name", student.father);
            if (student.mother != null) insert("Mother's Name", student.mother);
            if (student.religion != null) insert("Religion", student.religion);
            if (student.gender != null) insert("Gender", student.gender);
            if (student.permanentAddress != null) insert("Permanent Address", student.permanentAddress);
            if (student.currentAddress != null) insert("Current Address", student.currentAddress);
            if (student.email != null) insert("Email Address", student.email);
            if (student.phone != null) insert("Phone/Mobile", student.phone);

            // add Edit button
            editBtn = new MyButton("Edit Data");
            editBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editBtn.setEnabled(false);
                    AddStudentForm form = new AddStudentForm();
                    form.populateFrom(student);
                    new Dialogs.Builder((Dialogs.Builder) container).setPanel(form).onDispose(new Dialogs.DisposeListener() {
                        @Override
                        public void listen() {
                            editBtn.setEnabled(true);
                        }
                    }).build(container).setVisible(true);
                }
            });
            editBtn.setIcon(new ImageIcon(getClass().getResource("/icons/edit.png")));
            insert(editBtn);

            // clone button
            MyButton clone = new MyButton("Clone", "Copy all information of this student",
                    new ImageIcon(getClass().getResource("/icons/clone.png")));
            clone.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AddStudentForm form = new AddStudentForm();
                    form.populateFrom(student, true);
                    new Dialogs.Builder((Dialogs.Builder) container).setPanel(form).build(container).setVisible(true);
                }
            });
            gc.gridx += 2;
            insert(clone);
        }
    }

    /**
     * @param left  left component to add
     * @param right right component to add
     */
    private void insert(Component left, Component right) {
        insert(left);
        gc.gridx++;
        insert(new MyLabel(" : "));
        gc.gridx++;
        insert(right);

        // reset cell to next row, leftmost column
        gc.gridy++;
        gc.gridx = 0;
    }

    /**
     * Add Properties as "Property"    " : "    "Value"
     *
     * @param leftLabel  Property name i.e. "Name"
     * @param rightLabel Property value i.e. "Atikur Rahman"
     */
    private void insert(String leftLabel, String rightLabel) {
        insert(new MyLabel(leftLabel), new MyLabel(rightLabel));
    }

    @Override
    public void refresh() {
        // Retrieve data again as it might have been changed
        student = Student.findById(student.id);
        decorate();
        updateUI();
    }

    @Override
    public void notifyDataChanged(DataChangeOption changeOption) {
        refresh();
        super.notifyDataChanged(changeOption);
    }
}
