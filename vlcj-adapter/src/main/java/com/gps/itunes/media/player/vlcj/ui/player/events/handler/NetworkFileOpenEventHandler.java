package com.gps.itunes.media.player.vlcj.ui.player.events.handler;

import javax.swing.*;

/**
 * Created by leogps on 11/27/14.
 */
public class NetworkFileOpenEventHandler {

    public static String handle() {
        String url = JOptionPane.showInputDialog(null, "Enter URL: ");
        return url;
    }

}
