package team7.cu.comps;

import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class MyMenu extends JMenu {
    public MyMenu(String text, @Nullable ImageIcon icon) {
        super(text);
        if (icon != null) setIcon(icon);
        setOpaque(true);
        //setBackground(new Color(200, 220, 200));
        setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
    }
}
