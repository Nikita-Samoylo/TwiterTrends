import Map.PolygonDrawer;
import Map.State;
import Map.StatesParser;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<State> states = StatesParser.parse("states.json");

            JFrame frame = new JFrame("Polygon Drawer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.add(new PolygonDrawer(states));
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}