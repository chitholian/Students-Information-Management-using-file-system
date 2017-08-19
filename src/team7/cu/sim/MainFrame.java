package team7.cu.sim;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import team7.cu.comps.Dialogs;
import team7.cu.comps.MyMenu;
import team7.cu.comps.MyMenuItem;
import team7.cu.comps.MyPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class MainFrame extends JFrame {
    private MyPanel currentPanel; // Active Panel
    private MyPanel contentPane; // Panel Holder

    public MainFrame() {
        // Exit if closed
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(contentPane = new MyPanel());
        createMenus();
    }

    /**
     * Create menu bar and menus.
     * Then add them to the menu bar.
     */
    private void createMenus() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.LIGHT_GRAY);
        // Add to frame
        setJMenuBar(menuBar);

        // Create menus
        MyMenu studentMenu = new MyMenu("Student", new ImageIcon(getClass().getResource("/icons/student.png")));
        MyMenu departmentMenu = new MyMenu("Department", new ImageIcon(getClass().getResource("/icons/light.png")));
        MyMenu adminMenu = new MyMenu("Admin", new ImageIcon(getClass().getResource("/icons/admin.png")));
        MyMenu helpMenu = new MyMenu("More", new ImageIcon(getClass().getResource("/icons/info.png")));
        // add to menu bar
        menuBar.add(studentMenu);
        menuBar.add(departmentMenu);
        menuBar.add(adminMenu);
        menuBar.add(helpMenu);

        // Create menu items
        MyMenuItem addStudent = new MyMenuItem("Add Student", new ImageIcon(getClass().getResource("/icons/add.png")));
        MyMenuItem viewStudent = new MyMenuItem("View Students", new ImageIcon(getClass().getResource("/icons/view.png")));
        MyMenuItem searchStudent = new MyMenuItem("Search Student", new ImageIcon(getClass().getResource("/icons/search.png")));

        MyMenuItem addDept = new MyMenuItem("Add Department", new ImageIcon(getClass().getResource("/icons/add.png")));
        MyMenuItem viewDept = new MyMenuItem("View Departments", new ImageIcon(getClass().getResource("/icons/view.png")));

        MyMenuItem changePassMenu = new MyMenuItem("Change Password", new ImageIcon(getClass().getResource("/icons/edit.png")));

        MyMenuItem homeBtn = new MyMenuItem("Main Page", new ImageIcon(getClass().getResource("/icons/home.png")));
        MyMenuItem helpBtn = new MyMenuItem("About", new ImageIcon(getClass().getResource("/icons/help.png")));

        // Add to menu
        studentMenu.add(addStudent);
        studentMenu.add(viewStudent);
        studentMenu.add(searchStudent);

        departmentMenu.add(addDept);
        departmentMenu.add(viewDept);

        adminMenu.add(changePassMenu);

        helpMenu.add(homeBtn);
        helpMenu.add(helpBtn);

        // Set Action Listeners
        addStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent.setEnabled(false);
                AddStudentForm addStudentForm = new AddStudentForm();
                Dialogs.Builder dialog = new Dialogs.Builder(MainFrame.this).setPanel(addStudentForm).onDispose(new Dialogs.DisposeListener() {
                    @Override
                    public void listen() {
                        addStudent.setEnabled(true);
                    }
                }).build(MainFrame.this);
                dialog.setVisible(true);
            }
        });
        viewStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentList studentList = new StudentList();
                setPanel(studentList);
                studentList.refresh();
            }
        });
        searchStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPanel(new SearchPage());
            }
        });

        addDept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDept.setEnabled(false);
                Dialogs.Builder dialog = new Dialogs.Builder(MainFrame.this).setPanel(new AddDeptForm()).onDispose(new Dialogs.DisposeListener() {
                    @Override
                    public void listen() {
                        addDept.setEnabled(true);
                    }
                }).build(departmentMenu);// Appears near the departmentMenu
                dialog.setVisible(true);
            }
        });
        viewDept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeptList deptList = new DeptList();
                deptList.refresh();
                setPanel(deptList);
            }
        });

        changePassMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassMenu.setEnabled(false);
                Dialogs.Builder dialog =
                        new Dialogs.Builder(MainFrame.this);
                dialog.setPanel(new ChangePasswordForm(dialog) {
                    @Override
                    protected void onCancel() {
                        dialog.dispose();
                    }
                }).onDispose(new Dialogs.DisposeListener() {
                    @Override
                    public void listen() {
                        changePassMenu.setEnabled(true);
                    }
                }).hideCloseButton().build(adminMenu);// Appears near the adminMenu
                dialog.setResizable(false);
                dialog.setVisible(true);
            }
        });

        homeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPanel(new HomePage());
            }
        });
        helpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPanel(new HelpPage());
            }
        });


        // Add exit button
        MyMenuItem exitButton = new MyMenuItem("Exit", new ImageIcon(getClass().getResource("/icons/cancel.png")));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Dialogs.confirm(helpMenu, "Are you sure to exit ?"))
                    terminate(0);
            }
        });
        // add to menu
        helpMenu.add(exitButton);
    }

    /**
     * Exit the program
     *
     * @param exitCode exit code.
     */
    public void terminate(int exitCode) {
        super.dispose();
        System.exit(exitCode);
    }

    @Override
    public void dispose() {
        System.out.print("Disposing....");
        //if (Dialogs.confirm(null, "Are you sure to exit ?")) terminate(0);
        // else super.dispose();
        terminate(0);
    }

    /**
     * Set the main content holder
     *
     * @param panel Panel holding other components
     */
    public void setPanel(@NotNull MyPanel panel) {
        setTitle(panel.title);
        currentPanel = panel;
        contentPane.removeAll();
        contentPane.add(panel);
        contentPane.updateUI();
        panel.setContainer(this);
    }

    /**
     * Set location on the screen
     *
     * @param component If null then move to the mid-screen
     */
    public MainFrame moveTo(@Nullable Component component) {
        pack(); // Pack components
        setLocationRelativeTo(component);
        return this;
    }

    public void notifyDataChanged(DataChangeOption changeOption) {
        if (currentPanel != null)
            currentPanel.notifyDataChanged(changeOption);
    }
}
