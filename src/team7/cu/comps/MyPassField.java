package team7.cu.comps;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chitholian on 6/22/2017.
 */
public class MyPassField extends JPasswordField {
    public MyPassField() {
        super();
        setMinimumSize(new Dimension(250, 30));
        setPreferredSize(getMinimumSize());
        setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 16));
    }
}
