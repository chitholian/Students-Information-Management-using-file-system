package team7.cu.comps;

import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chitholian on 6/22/2017.
 */
public class MyButton extends JButton {
    public MyButton() {
        this(null);
    }

    public MyButton(String text) {
        this(text, null, null);
    }

    public MyButton(String text, @Nullable String toolTipText, @Nullable ImageIcon icon) {
        super(text);
        if (icon != null) setIcon(icon);

        // Set our favourites
        setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        //setBackground(new Color(230,230, 250));
        // setForeground(Color.WHITE);
    }
}
