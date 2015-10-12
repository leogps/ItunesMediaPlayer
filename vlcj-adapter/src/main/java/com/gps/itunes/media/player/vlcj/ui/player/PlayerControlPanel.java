package com.gps.itunes.media.player.vlcj.ui.player;

import com.gps.itunes.media.player.vlcj.ui.player.events.PlayerControlEventListener;

import javax.swing.*;
import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by leogps on 9/20/15.
 */
public class PlayerControlPanel extends JPanel {
    private JPanel wrapperPanel;
    private JPanel seekPanel;
    private JSlider seekbar;
    private BasicPlayerControlPanel basicControlPanel;

    private JLabel startTimeLabel;
    private JLabel endTimeLabel;

    private void createUIComponents() {
        startTimeLabel = new JLabel("00:00");
        endTimeLabel = new JLabel("00:00");

        Dictionary labelTable = new Hashtable();
        labelTable.put(new Integer(0), startTimeLabel);
        labelTable.put(new Integer(100), endTimeLabel);

        seekbar = new JSlider();
        seekbar.setLabelTable(labelTable);
        seekbar.setValue(0);
    }

    public void resetSeekbar() {
        seekbar.setValue(0);
    }

    public void updateSeekbar(int seekvalue) {
        seekbar.setValue(seekvalue);
    }

    public JSlider getSeekbar() {
        return seekbar;
    }

    public void updateSeekbarUI() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                getSeekbar().updateUI();
            }
        });
    }

    public JLabel getStartTimeLabel() {
        return startTimeLabel;
    }

    public JLabel getEndTimeLabel() {
        return endTimeLabel;
    }

    public JSlider getVolumeSlider() {
        return basicControlPanel.getVolumeSlider();
    }

    public void setPaused() {
        basicControlPanel.setPaused();
    }

    public void setPlaying() {
        basicControlPanel.setPlaying();
    }

    public void setPlayerControlEventListener(PlayerControlEventListener playerControlEventListener) {
        basicControlPanel.setPlayerControlEventListener(playerControlEventListener);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.wrapperPanel.setVisible(visible);
    }

}
