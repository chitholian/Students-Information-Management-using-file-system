package team7.cu.comps;

import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chitholian on 6/22/2017.
 */
public class MyLabel extends JLabel {
    public MyLabel() {
        this(null);
    }

    public MyLabel(String text) {
        this(text, null, null);
    }

    public MyLabel(String text, @Nullable String toolTipText, @Nullable ImageIcon icon) {
        super(text);
        if (toolTipText != null) setToolTipText(toolTipText);
        if (icon != null) setIcon(icon);

        // Set our favourites
        setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
    }
}
