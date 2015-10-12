import com.gps.itunes.media.player.vlcj.ui.player.BasicPlayerControlPanel;
import org.testng.annotations.Test;

import javax.swing.*;
import java.awt.*;

/**
 * Created by leogps on 10/4/14.
 */
public class PlayerControlPanelTest {


    @Test
    public void test() throws InterruptedException {
        JFrame frame = new JFrame();
        JPanel jPanel = new JPanel(new BorderLayout());

        jPanel.add(new BasicPlayerControlPanel(), BorderLayout.CENTER);
        frame.setSize(1000, 200);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }



}
