package team7.cu.sim;

import team7.cu.comps.*;
import team7.cu.utils.Helper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Chitholian on 6/22/2017.
 */
public class LoginDialog extends Dialogs.Builder {
    private boolean loginSuccessful;

    public LoginDialog() {
        super();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPanel(new LoginPage());
        hideCloseButton();

    }

    @Override
    public void dispose() {
        super.dispose();
        if (!loginSuccessful) System.exit(0); // Exit program!
    }

    @Override
    public void setVisible(boolean visible) {

        if (visible) {
            pack();
            setResizable(false);
            setLocationRelativeTo(null);
        }
        super.setVisible(visible);
    }

    /* **************** */
    private final class LoginPage extends MyPanel {
        private MyButton loginButton, exitButton, helpButton;
        private MyTextField usernameField;
        private MyPassField passwordField;

        LoginPage() {
            super("Login Panel");
            decorate();
        }

        @Override
        protected void init() {
            super.init();
            // Create buttons
            loginButton = new MyButton("Login", "Login to Admin Panel",
                    new ImageIcon(getClass().getResource("/icons/forward.png")));
            exitButton = new MyButton("Cancel", "Exit from the app",
                    new ImageIcon(getClass().getResource("/icons/cancel.png")));
            helpButton = new MyButton(null, "Default username & password is \"admin\"",
                    new ImageIcon(getClass().getResource("/icons/info.png")));

            // add action listeners
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Login // Collect input values
                    String user = usernameField.getText().trim();
                    String pass = String.valueOf(passwordField.getPassword());
                    // Validate input
                    if (user.isEmpty() || pass.isEmpty()) {
                        Dialogs.alert(loginButton, "Please enter both username & password.");
                        return;
                    }
                    // Check Credentials
                    if (!Helper.hashifyPassword(pass).equals(Admin.findPasswordOf(user))) { // Incorrect ?
                        Dialogs.alert(loginButton, "Username or Password Incorrect");
                        return;
                    }

                    // Login Ok!
                    loginSuccessful = true;
                    // Close Login Dialog
                    dispose();

                    // Show HomePage
                    Launcher.frame = new MainFrame();
                    HomePage homePage = new HomePage();
                    Launcher.frame.setPanel(homePage);
                    Launcher.frame.moveTo(null).setVisible(true);
                }
            });

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            helpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Dialogs.alert(LoginDialog.this, "Default username & password is \"admin\"");
                }
            });

            // Create Input Fields
            usernameField = new MyTextField();
            passwordField = new MyPassField();

        }

        @Override
        public void decorate() {
            super.decorate();
            // Left column
            insert(new MyLabel("Username"));
            gc.gridy++;
            insert(new MyLabel("Password"));
            gc.gridy++;
            insert(helpButton);

            // Right column
            gc.gridy = 0;
            gc.gridx++;
            gc.gridwidth = 2;
            insert(usernameField);
            gc.gridy++;
            insert(passwordField);

            gc.gridy++;
            gc.gridwidth = 1;
            insert(exitButton);
            gc.gridx++;
            insert(loginButton);
        }
    }
}
