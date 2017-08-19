package team7.cu.comps;

import team7.cu.sim.DataChangeOption;
import team7.cu.sim.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chitholian on 6/22/2017.
 */
public class MyPanel extends JPanel {
    public String title;
    protected GridBagConstraints gc;
    protected Component container;
    protected long notificationId; // required to control stack overflow errors when data set changes and we are to refresh.

    public MyPanel() {
        this(null);
    }

    public MyPanel(String title) {
        super(new GridBagLayout()); // Yes this, our favourite one!
        //setBackground(new Color(220, 255, 220));
        this.title = title;
        init();
    }

    /**
     * Creates and returns GridBagConstraints with our favourite defaults
     *
     * @return New Instance of GridBagConstraints
     */
    public static GridBagConstraints getDefaultConstraints() {
        return new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, 0, new Insets(5, 5, 5, 5), 5, 5);
    }

    protected void init() {
        gc = getDefaultConstraints();
    }

    /**
     * Decorates the panel with necessary components.
     * It first removes all already added components
     * as well as resets the layout constraints.
     * So, In the sub-classes overridden methods call it very first.
     */
    public void decorate() {
        removeAll(); // Remove all if already existed component
        gc = getDefaultConstraints(); // Reset constraints
    }

    /**
     * Sets the Component who contains this panel.
     *
     * @param container container component.
     */
    public void setContainer(Component container) {
        this.container = container;
    }

    /**
     * Adds components to the panel with Layout Constraints (GridBagConstraints)
     *
     * @param component Component to add
     */
    protected void insert(Component component) {
        add(component, gc);
    }

    public void refresh() {
    }

    public void notifyDataChanged(DataChangeOption changeOption) {
        if (changeOption.nid != notificationId) {
            notificationId = changeOption.nid;
            if (container instanceof Dialogs.Builder)
                ((Dialogs.Builder) container).notifyDataChanged(changeOption);
            if (container instanceof MainFrame)
                ((MainFrame) container).notifyDataChanged(changeOption);
        }
    }

    @Override
    public void removeAll() {
        super.removeAll();
        gc = getDefaultConstraints();
    }
}
