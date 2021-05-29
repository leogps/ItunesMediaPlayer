package com.gps.itunes.media.player.vlcj.ui.player.events.handler;

import javax.swing.*;
import java.awt.*;

/**
 * Created by leogps on 11/27/14.
 */
public class NetworkFileOpenEventHandler {

    public static String handle() {
        JPanel panel = new JPanel();
        panel.setSize(new Dimension(450, 100));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter URL:");
        label.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(label, BorderLayout.WEST);

        Dimension defaultSize = (Dimension) UIManager.get("OptionPane.minimumSize");
        UIManager.put("OptionPane.minimumSize", new Dimension(450, 100));

        String url = JOptionPane.showInputDialog(null, panel, "URL", JOptionPane.PLAIN_MESSAGE);
        UIManager.put("OptionPane.minimumSize", defaultSize);
        return url;
    }

}
