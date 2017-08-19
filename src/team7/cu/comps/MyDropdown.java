package team7.cu.comps;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class MyDropdown extends JComboBox<Object> {
    public MyDropdown(Object[] items) {
        super(items);
        setOpaque(true);
        setRenderer(new SimpleCellRenderer());
        setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        setBackground(new Color(220, 220, 220));
    }

    public static MyDropdown buildWith(Object[] items) {
        return new MyDropdown(items);
    }

    public class SimpleCellRenderer extends MyLabel implements ListCellRenderer<Object> {
        public SimpleCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value == null) return this;
            if (!list.getValueIsAdjusting()) {
                setText(value.toString());
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
                setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
                if (isSelected) {
                    setBackground(Color.GRAY);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
            }
            return this;
        }
    }
}