package team7.cu.sim;

import chitholian.java.guilibs.DateChooser;
import team7.cu.comps.*;
import team7.cu.utils.Helper;
import team7.cu.utils.MyDate;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class AddStudentForm extends MyPanel {
    private MyTextField idField, nameField, fatherField, motherField, currentAddressField, permanentAddressField;
    private MyTextField phoneField, emailField;
    private MyDropdown deptField, sessionField, genderField, religionField;
    private ArrayList<Department> depts;
    private MyButton avatar, submitBtn, dobField, resetBtn;
    private File picture;
    private Student editing;
    private JProgressBar progressBar;
    private boolean clone;
    private MyDate birthDate;


    public AddStudentForm() {
        super("Add New Student");
        // populateFrom(Student.getAll().get(0));
        idField = new MyTextField();
        nameField = new MyTextField();
        fatherField = new MyTextField();
        religionField = new MyDropdown(new String[]{"Islam", "Hinduism", "Christianity", "Buddhism", "Other"});
        genderField = new MyDropdown(new String[]{"Male", "Female", "Other"});
        motherField = new MyTextField();
        fatherField = new MyTextField();
        currentAddressField = new MyTextField();
        permanentAddressField = new MyTextField();
        phoneField = new MyTextField();
        emailField = new MyTextField();
        dobField = new MyButton();
        dobField.setPreferredSize(motherField.getPreferredSize());
        dobField.setBackground(new Color(250, 250, 220));
        dobField.setIcon(new ImageIcon(getClass().getResource("/icons/calender.png")));

        final int currentYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        String[] sessions = new String[Math.max(0, currentYear - 1964)];
        for (int i = 0; i < currentYear - 1964; i++) {
            sessions[i] = (currentYear - i) + " - " + (currentYear - i + 1);
        }
        sessionField = new MyDropdown(sessions);

        avatar = new MyButton("Click to add pic", "Click to change picture", null);
        avatar.setSize(new Dimension(175, 200));
        avatar.setMinimumSize(new Dimension(175, 200));
        avatar.setPreferredSize(new Dimension(175, 200));
        avatar.setBackground(Color.WHITE);
        avatar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser
                JFileChooser chooser = new JFileChooser(System.getProperty("HOME"));
                chooser.setDialogTitle("Choose an avatar");
                chooser.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return !f.isFile() || f.getName().matches(".*\\.(png|jpg|gif|jpeg)$");
                    }

                    @Override
                    public String getDescription() {
                        return "*.png, *.jpg, *.gif, *.jpeg";
                    }
                });
                chooser.showOpenDialog(avatar);
                File f = chooser.getSelectedFile();
                if (f != null) {
                    String p = f.getAbsolutePath();
                    if (!p.matches(".*\\.(png|jpg|gif|jpeg)$")) {
                        Dialogs.alert(avatar, "Please Select a valid image file");
                        return;
                    }
                    picture = f;
                    avatar.setText(null);
                    avatar.setIcon(new ImageIcon(new ImageIcon(p).getImage().getScaledInstance(avatar.getWidth(), avatar.getHeight(), Image.SCALE_DEFAULT)));
                }
            }
        });

        resetBtn = new MyButton("Reset Form");
        resetBtn.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png")));
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Dialogs.confirm(resetBtn, "Reset ?")) return;
                reset();
            }
        });

        submitBtn = new MyButton("Submit");
        submitBtn.setMinimumSize(new Dimension(200, 35));
        submitBtn.setPreferredSize(new Dimension(200, 35));
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Submit the data
                submitData();
            }
        });
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setMinimumSize(submitBtn.getMinimumSize());
        progressBar.setPreferredSize(submitBtn.getPreferredSize());
        progressBar.setVisible(false); // keep hidden by default

        dobField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dobField.setEnabled(false);
                DateChooser dialog = new DateChooser((Dialogs.Builder) AddStudentForm.this.container) {
                    @Override
                    public void onDateSelect(Calendar calendar) {
                        int day = calendar.get(Calendar.DATE);
                        dobField.setText(monthNames[calendar.get(Calendar.MONTH)] + " " + (day < 10 ? "0" + day : day) +
                                ", " + calendar.get(Calendar.YEAR));
                        try {
                            birthDate = MyDate.build(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) +
                                    "-" + calendar.get(Calendar.DATE));
                        } catch (MyDate.InvalidDateException ide) {
                            ide.printStackTrace();
                        }
                        dobField.setEnabled(true);
                    }

                    @Override
                    public void onCancel() {
                        dobField.setEnabled(true);
                    }
                };

                if (birthDate != null)
                    dialog.setDate(birthDate.get(MyDate.YEAR), birthDate.get(MyDate.MONTH), birthDate.get(MyDate.DATE));
                dialog.display(dobField);
            }
        });

        // decorate now
        decorate();
    }

    @Override
    public void decorate() {
        removeAll();
        gc = getDefaultConstraints();
        gc.anchor = GridBagConstraints.LINE_START;

        insert(new MyLabel("Student ID"));
        gc.gridx++;
        idField.setText(null);
        insert(idField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Student Name"));
        gc.gridx++;
        insert(nameField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Father's Name"));
        gc.gridx++;
        insert(fatherField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Mother's Name"));
        gc.gridx++;
        insert(motherField);

        // Date of Birth
        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Date of Birth"));
        gc.gridx++;
        insert(dobField);
        dobField.setText(null);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Department"));
        // reset Button
        gc.gridy++;
        insert(resetBtn);
        depts = Department.getAll();
        depts.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Department) o1).fullName.compareTo(((Department) o2).fullName);
            }
        });
        gc.gridx++;
        gc.gridy--;
        insert(deptField = MyDropdown.buildWith(depts.toArray()));

        // move to next column
        gc.gridy = 0;
        gc.gridx = 2;

        insert(new MyLabel("Session"));
        gc.gridx++;
        insert(sessionField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Religion"));
        gc.gridx++;
        insert(religionField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Gender"));
        gc.gridx++;
        insert(genderField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Phone/Mobile"));
        gc.gridx++;
        insert(phoneField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Email Address"));
        gc.gridx++;
        insert(emailField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Current Address"));
        gc.gridx++;
        insert(currentAddressField);

        gc.gridy++;
        gc.gridx--;
        insert(new MyLabel("Permanent Address"));
        gc.gridx++;
        insert(permanentAddressField);

        // move to next column
        gc.gridy = 0;
        gc.gridx = 4;

        // add avatar
        gc.gridheight = 6;
        gc.anchor = GridBagConstraints.CENTER;
        insert(avatar);

        gc.gridheight = 1;
        gc.gridy = 6;
        insert(submitBtn);
    }

    private void submitData() {
        // collect input data
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // Validate data
        if (!id.matches("^[1-9]\\d{0,9}$")) {
            Dialogs.alert(submitBtn, "Please insert a valid student ID");
            idField.requestFocusInWindow();
        } else if (!name.matches("^[A-Z].*")) {
            Dialogs.alert(submitBtn, "Please insert a valid student Name");
            nameField.requestFocusInWindow();
        } else if (birthDate == null) {
            Dialogs.alert(submitBtn, "Please select birth date");
        } else if (!phone.isEmpty() && !phone.matches("^\\+?\\d[\\d- ]*\\d$")) {
            Dialogs.alert(submitBtn, "Please insert valid phone number");
            phoneField.requestFocusInWindow();
        } else if (!email.isEmpty() && !email.matches("^[^\\s@]+@[^\\s@]+$")) {
            Dialogs.alert(submitBtn, "Please insert valid email address");
            emailField.requestFocusInWindow();
        } else if (deptField.getSelectedIndex() == -1) {
            Dialogs.alert(submitBtn, "Please select a department");
            deptField.requestFocusInWindow();
        } else { // Confirm and submit data
            Student student = null;
            if (!Dialogs.confirm(submitBtn, "Submit Data ?")) return;
            // show progress bar
            submitBtn.setVisible(false);
            progressBar.setVisible(true);
            // check duplicate. If idField is disabled then we are in edit mode.
            if (idField.isEnabled()) {
                student = Student.findById(Integer.parseInt(id));
                if (student != null && !Dialogs.confirm(submitBtn, "Student with this ID already exists. Overwrite ?")) {
                    // hide progress bar and return
                    progressBar.setVisible(false);
                    submitBtn.setVisible(true);
                    return;
                }
            }
            if (student == null) student = new Student(Integer.parseInt(id));
            // collect rest of data
            Department department = (Department) deptField.getSelectedItem();
            String currentAddress = currentAddressField.getText().trim();
            String permanentAddress = permanentAddressField.getText().trim();
            String session = sessionField.getSelectedItem().toString();
            String gender = genderField.getSelectedItem().toString();
            String religion = religionField.getSelectedItem().toString();
            String father = fatherField.getText().trim();
            String mother = motherField.getText().trim();

            // set properties
            student.name = name;
            student.father = father;
            student.mother = mother;
            student.phone = phone;
            student.email = email;
            student.currentAddress = currentAddress;
            student.permanentAddress = permanentAddress;
            student.dept = department;
            student.session = session;
            student.gender = gender;
            student.religion = religion;
            student.birth = birthDate;

            // Try to copy the avatar pic
            if (picture != null) {
                try {
                    Files.copy(picture.toPath(), new File(Helper.AVATAR_LOCATION + id + ".png").toPath(), REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // save it
            student.save();
            // hide progress bar
            progressBar.setVisible(false);
            submitBtn.setVisible(true);

            // Notify to the container
            notifyDataChanged(new DataChangeOption(DataChangeOption.STUDENTS, System.currentTimeMillis()));
            // If edit mode then dispose the dialog
            if (!idField.isEnabled()) {
                ((Dialogs.Builder) container).dispose();
            } else
                Dialogs.alert(submitBtn, "Operation Successful !");
        }
    }

    public void populateFrom(Student student) {
        populateFrom(student, false);
    }

    public void populateFrom(Student student, boolean clone) {
        this.clone = clone;
        editing = student;
        if (clone)
            title = "Clone - " + student.name;
        else {
            title = "Edit - " + student.name;
            idField.setText(String.valueOf(student.id));
            // disable id field as it is a primary key (should not be modified)
            idField.setEnabled(false);

            idField.setToolTipText("You cannot modify ID. You should (delete it and) submit as another entity if you need to" +
                    " change id");
        }
        nameField.setText(student.name);
        fatherField.setText(student.father);
        motherField.setText(student.mother);
        phoneField.setText(student.phone);
        emailField.setText(student.email);
        currentAddressField.setText(student.currentAddress);
        permanentAddressField.setText(student.permanentAddress);

        // select dept
        int index = 0;
        for (Department dept : depts) {
            if (dept.getId() == student.dept.getId()) break;
            index++;
        }
        deptField.setSelectedIndex(index);
        sessionField.setSelectedItem(student.session);
        genderField.setSelectedItem(student.gender);
        religionField.setSelectedItem(student.religion);

        // Date of birth
        if (student.birth != null) {
            birthDate = MyDate.copy(student.birth);
            dobField.setText(birthDate.toString());
        }
        // set the avatar
        picture = new File(Helper.AVATAR_LOCATION + student.id + ".png");
        avatar.setText(null);
        avatar.setIcon(new ImageIcon(new ImageIcon(picture.getAbsolutePath()).getImage().getScaledInstance(avatar.getWidth(), avatar.getHeight(), Image.SCALE_DEFAULT)));
    }

    private void reset() {
        if (editing != null) populateFrom(editing, clone);
        else decorate();
        updateUI();
    }
}
