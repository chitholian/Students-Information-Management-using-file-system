package team7.cu.sim;

import team7.cu.comps.MyLabel;
import team7.cu.comps.MyPanel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Chitholian on 6/23/2017.
 */
public class HomePage extends MyPanel {
    public HomePage() {
        super(" Students Information Management - Team7");
    }

    @Override
    protected void init() {
        super.init();
        gc.anchor = GridBagConstraints.NORTH;
        gc.weighty = 0;
        insert(new MyLabel(null, null, new ImageIcon(getClass().getResource("/icons/cu-logo.png"))));
        gc.gridy++;
        MyLabel myLabel = new MyLabel("Students Information Management");
        myLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 45));
        insert(myLabel);

        // Team members
        MyPanel memberPanel = new MyPanel() {
            @Override
            protected void init() {
                super.init();
                MyLabel header = new MyLabel("Team Members");
                header.setFont(new Font(Font.DIALOG, Font.BOLD, 35));
                gc.anchor = GridBagConstraints.CENTER;
                insert(header);
                gc.gridy++;
                gc.anchor = GridBagConstraints.LINE_START;

                // Show the list of team members.
                class MemberName extends MyLabel {
                    private MemberName(String name) {
                        super(name, null, null);
                        setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
                    }
                }
                StringBuilder about = new StringBuilder();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/developers.txt")));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        insert(new MemberName(line));
                        gc.gridy++;
                    }
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        gc.anchor = GridBagConstraints.NORTHEAST;
        gc.gridheight = 2;
        gc.gridy = 0;
        gc.gridx++;
        insert(memberPanel);
        setPreferredSize(new Dimension((int) getPreferredSize().getWidth() + 100, 700));
    }
}
