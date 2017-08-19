package team7.cu.sim;

import team7.cu.comps.MyLabel;
import team7.cu.comps.MyPanel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Chitholian on 6/24/2017.
 */
public class HelpPage extends MyPanel {
    public HelpPage() {
        super("About");
    }

    @Override
    protected void init() {
        super.init();
        StringBuilder about = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/about.html")));
            String line;
            while ((line = reader.readLine()) != null) about.append(line);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        insert(new MyLabel(about.toString()));
    }
}
