package com.gps.itunes.media.player.vlcj.ui.player;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Dictionary;
import java.util.Hashtable;

public class VideoPlayerFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 8230628555951169075L;

    private static final Logger LOGGER = Logger.getLogger(VideoPlayerFrame.class);

    protected final Canvas canvas;
    protected BasicPlayerControlPanel basicPlayerControlPanel;
    private JSlider seekbar;

    private final JLabel startTimeLabel = new JLabel("0:00");
    private final JLabel endTimeLabel = new JLabel("0:00");

    private JPanel bodyPanel;
    protected JPanel headerPanel;
    private JPanel videoPanel;
    protected JPanel footerPanel;
    private JProgressBar bufferingProgressBar;

    public VideoPlayerFrame() {
        super("Video Screen");

        canvas = new Canvas();
        canvas.setBackground(Color.black);

        getFrameCanvas().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                videoPanel.requestFocusInWindow();
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                videoPanel.requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                videoPanel.requestFocusInWindow();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                videoPanel.requestFocusInWindow();
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                videoPanel.requestFocusInWindow();
            }
        });
        videoPanel.setFocusable(true);
        videoPanel.requestFocusInWindow();
        videoPanel.setLayout(new BorderLayout());
        videoPanel.add(canvas, BorderLayout.CENTER);

        seekbar.setSnapToTicks(true);
        Dictionary labelTable = new Hashtable();
        labelTable.put(0, startTimeLabel);
        labelTable.put(100, endTimeLabel);
        seekbar.setPaintLabels(true);
        seekbar.setLabelTable(labelTable);
        seekbar.setValue(0);

        bodyPanel.add(headerPanel, BorderLayout.NORTH);
        bodyPanel.add(videoPanel, BorderLayout.CENTER, 0);
        bodyPanel.add(footerPanel, BorderLayout.SOUTH);

        add(bodyPanel, BorderLayout.CENTER);

        setSize(600, 480);
        setVisible(false);
        bufferingProgressBar.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                if(bufferingProgressBar.getValue() == 100) {
                    bufferingProgressBar.setString("Buffering complete.");
                    bufferingProgressBar.setStringPainted(false);
                } else {
                    bufferingProgressBar.setStringPainted(true);
                    bufferingProgressBar.setString("Buffering... " + bufferingProgressBar.getValue() + "%");
                }
            }
        });
    }

    public Canvas getFrameCanvas() {
        return canvas;
    }

    public JSlider getSeekbar() {
        return seekbar;
    }

    public JLabel getStartTimeLabel() {
        return startTimeLabel;
    }

    public JLabel getEndTimeLabel() {
        return endTimeLabel;
    }

    public void updateSeekbar() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getSeekbar().updateUI();
            }
        });
    }

    public JPanel getVideoPanel() {
        return videoPanel;
    }

    public JPanel getFooterPanel() {
        return footerPanel;
    }

    public BasicPlayerControlPanel getBasicPlayerControlPanel() {
        return basicPlayerControlPanel;
    }

    public void disableVideo() {
        setVisible(false);
        getVideoPanel().removeAll();
    }

    public void enableVideo(Canvas frameCanvas) {
        getVideoPanel().add(frameCanvas, BorderLayout.CENTER);
        setVisible(true);
    }

    public void setBufferingValue(Float bufferingValue) {
        bufferingProgressBar.setValue(bufferingValue.intValue());
    }
}
