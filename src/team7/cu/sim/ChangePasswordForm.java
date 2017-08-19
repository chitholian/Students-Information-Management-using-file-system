package team7.cu.sim;

import team7.cu.comps.*;
import team7.cu.utils.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class ChangePasswordForm extends MyPanel {
    private MyPassField oldPass, newPassOne, newPassTwo;
    private MyButton submitButton, cancelButton;
    private Dialogs.Builder dialog;

    public ChangePasswordForm(Dialogs.Builder owner) {
        super("Change Admin Password");
        dialog = owner;
        decorate();
    }

    @Override
    protected void init() {
        super.init();
        // Create password fields
        oldPass = new MyPassField();
        newPassOne = new MyPassField();
        newPassTwo = new MyPassField();

        // Create submit button
        submitButton = new MyButton("Change Password", null,
                new ImageIcon(getClass().getResource("/icons/edit.png")));
        // add action listener
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Collect input data
                String old_pass = String.valueOf(oldPass.getPassword());
                String new_pass1 = String.valueOf(newPassOne.getPassword());
                String new_pass2 = String.valueOf(newPassTwo.getPassword());
                // Ensure all the fields was input
                if (old_pass.isEmpty() || new_pass1.isEmpty() || new_pass2.isEmpty()) {
                    Dialogs.alert(submitButton, "Please enter all the fields");
                    oldPass.requestFocusInWindow();
                    return;
                }

                if (!new_pass1.equals(new_pass2)) { // Passwords do not match.
                    Dialogs.alert(submitButton, "New Passwords did not match");
                    newPassTwo.requestFocusInWindow();
                    return;
                }

                // Check credentials
                if (!Helper.hashifyPassword(old_pass).equals(Admin.findPasswordOf("admin"))) {
                    Dialogs.alert(submitButton, "Current password incorrect");
                    oldPass.requestFocusInWindow();
                    return;
                }

                // All are OK, update password now.
                Admin admin = new Admin("admin");
                admin.setPassword(new_pass1);
                admin.save();
                Dialogs.alert(submitButton, "Password Changed Successfully");
                if (dialog != null)
                    dialog.dispose();
            }
        });

        cancelButton = new MyButton("Cancel", "Close the dialog", new ImageIcon(getClass().getResource("/icons/cancel.png")));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dialog != null) dialog.dispose();
                onCancel();
            }
        });
    }

    @Override
    public void decorate() {
        gc = getDefaultConstraints();
        gc.anchor = GridBagConstraints.LINE_START;
        insert(new MyLabel("Current Password"));
        gc.gridy++;
        insert(new MyLabel("New Password"));
        gc.gridy++;
        insert(new MyLabel("New Password Again"));
        gc.gridx++;
        gc.gridy = 0;
        insert(oldPass);
        gc.gridy++;
        insert(newPassOne);
        gc.gridy++;
        insert(newPassTwo);
        gc.gridy++;
        gc.gridx--;
        insert(cancelButton);
        gc.gridx++;
        submitButton.setPreferredSize(new Dimension((int) newPassTwo.getPreferredSize().getWidth(), (int) submitButton.getPreferredSize().getHeight()));
        insert(submitButton);
    }

    protected void onCancel() {
    }
}
