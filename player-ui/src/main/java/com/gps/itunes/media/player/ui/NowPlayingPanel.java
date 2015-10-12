package com.gps.itunes.media.player.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Created by leogps on 9/17/15.
 */
public class NowPlayingPanel extends JPanel {
    private JPanel backingPanel;
    private JLabel trackNameLabel;
    private JLabel trackAlbumNameLabel;
    private JPanel centeredPanel;
    private JLabel trackArtistNameLabel;

    public JLabel getTrackNameLabel() {
        return trackNameLabel;
    }

    public JLabel getTrackAlbumNameLabel() {
        return trackAlbumNameLabel;
    }

    public JLabel getTrackArtistNameLabel() {
        return trackArtistNameLabel;
    }

}
