package com.gps.itunes.media.player.vlcj.player;

import com.gps.itunes.media.player.vlcj.ui.player.BasicPlayerControlPanel;
import javafx.embed.swing.JFXPanel;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by leogps on 10/1/15.
 */
public interface FXPlayerFrame {
    MediaPlayer getPlayer();

    JSlider getSeekbar();

    JLabel getStartTimeLabel();

    JLabel getEndTimeLabel();

    void updateSeekbar();

    void requestFocus();

    JPanel getVideoPanel();

    JFXPanel getJFXPanel();

    Canvas getFrameCanvas();

    BasicPlayerControlPanel getBasicPlayerControlPanel();

    void play(String location);

    void setTitle(String title);

    void setBufferingValue(float bufferringValue);
}
