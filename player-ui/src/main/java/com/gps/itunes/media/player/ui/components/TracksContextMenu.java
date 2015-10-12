package com.gps.itunes.media.player.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by leogps on 12/18/14.
 */
public class TracksContextMenu extends JPopupMenu {

    public static final String INFORMATION = "Information";
    public static final String PLAY_TEXT = "Play";

    private final int numberOfTracksSelected;

    private final JMenuItem informationMenu = new JMenu(INFORMATION);
    private final JMenuItem playMenu;


    public TracksContextMenu(int numberOfTracksSelected) {
        this.numberOfTracksSelected = numberOfTracksSelected;

        if(numberOfTracksSelected == 1) {
            add(informationMenu);
        }

        this.playMenu = new JMenuItem(PLAY_TEXT);
        add(this.playMenu);
    }

    public JMenuItem getPlayMenu() {
        return playMenu;
    }

    public JMenuItem getInformationMenu() {
        return informationMenu;
    }
}
