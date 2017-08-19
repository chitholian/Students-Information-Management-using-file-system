package team7.cu.comps;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import team7.cu.sim.DataChangeOption;
import team7.cu.sim.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class Dialogs {
    /**
     * Show an alert message
     *
     * @param owner Component who owns the alert.
     * @param alert Message String to show.
     */
    public static void alert(@Nullable Component owner, String alert) {
        JOptionPane.showMessageDialog(owner, alert);
    }

    /**
     * Show a confirmation prompt.
     *
     * @param owner    the component who makes this prompt.
     * @param question String message to show.
     * @return true if OK pressed, otherwise false.
     */
    public static boolean confirm(@Nullable Component owner, String question) {
        return JOptionPane.showConfirmDialog(owner, question, "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
    }

    public interface DisposeListener {
        void listen();
    }

    /* *********************** */
    public static class Builder extends JDialog {
        protected MyPanel panel;
        protected DisposeListener listener;
        protected MyButton closeButton;
        protected Component owner;

        public Builder() {
            super();
            init();
        }

        public Builder(MainFrame owner) {
            super(owner);
            init();
            this.owner = owner;
        }

        public Builder(Builder owner) {
            super(owner);
            init();
            this.owner = owner;
        }

        protected void init() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLayout(new GridBagLayout());
            closeButton = new MyButton("Close", "Exit the dialog",
                    new ImageIcon(getClass().getResource("/icons/cancel.png")));
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }

        public Builder setPanel(@NotNull MyPanel panel) {
            this.panel = panel;
            panel.setContainer(this);
            return this;
        }

        public Builder onDispose(DisposeListener disposeListener) {
            listener = disposeListener;
            return this;
        }

        public Builder hideCloseButton() {
            closeButton = null;
            return this;
        }

        public Builder build(@Nullable Component owner) {
            setLayout(new GridBagLayout());
            GridBagConstraints gc = MyPanel.getDefaultConstraints();
            gc.gridy = gc.gridx = 0;
            add(panel, gc);
            if (closeButton != null) {
                gc.gridy++;
                gc.anchor = GridBagConstraints.SOUTHEAST;
                add(closeButton, gc);
            }
            setTitle(panel.title);
            pack();
            //setResizable(false);
            setLocationRelativeTo(owner);
            return this;
        }

        @Override
        public void dispose() {
            super.dispose();
            if (listener != null) listener.listen();
        }

        public void notifyDataChanged(DataChangeOption changeOption) {
            if (panel != null && panel.notificationId != changeOption.nid) panel.notifyDataChanged(changeOption);
            if (owner instanceof MainFrame) ((MainFrame) owner).notifyDataChanged(changeOption);
            else if (owner instanceof Dialogs.Builder) ((Dialogs.Builder) owner).notifyDataChanged(changeOption);
        }
    }
}
