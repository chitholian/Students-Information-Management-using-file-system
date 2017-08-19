package team7.cu.sim;


import team7.cu.utils.Helper;

import javax.swing.*;

/**
 * Created by Chitholian on 6/22/2017.
 */
public class Launcher {
    public static MainFrame frame;

    /**
     * Start the application
     *
     * @param args Command Line Arguments
     */
    public static void main(String[] args) {
        // First check if all the IO Files are OK.
        Helper.checkFiles();

        /* ************* */
        // Launcher.frame = new MainFrame();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        // Ensure authenticated access.
        new LoginDialog().build(null).setVisible(true);
    }
}
