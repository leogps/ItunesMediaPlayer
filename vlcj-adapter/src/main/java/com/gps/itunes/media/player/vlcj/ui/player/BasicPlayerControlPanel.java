package com.gps.itunes.media.player.vlcj.ui.player;

import com.gps.itunes.media.player.vlcj.ui.player.events.PlayerControlEventListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

/**
 * Created by leogps on 10/6/14.
 */
public class BasicPlayerControlPanel extends JPanel {

    private PlayerControlEventListener listener;
    private JLabel previousIcon;
    private JLabel playIcon;
    private JLabel forwardIcon;

    private static Logger log =
            Logger.getLogger(BasicPlayerControlPanel.class);

    //    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("vlcjAdapterUi.properties");
    private static final ImageIcon previousImg, previousPressedImg, playImage, playPressedImg, forwardImage, forwardPressedImg, pauseImage, pausePressedImage;

    static {
        previousImg = new ImageIcon(BasicPlayerControlPanel.class.getClassLoader().getResource("icons/previous.png"));

        previousPressedImg = new ImageIcon(BasicPlayerControlPanel.class.getClassLoader().getResource("icons/previous-pressed.png"));

        playImage = new ImageIcon(BasicPlayerControlPanel.class.getClassLoader().getResource("icons/play.png"));

        playPressedImg = new ImageIcon(BasicPlayerControlPanel.class.getClassLoader().getResource("icons/play-pressed.png"));

        forwardImage = new ImageIcon(BasicPlayerControlPanel.class.getClassLoader().getResource("icons/forward.png"));

        forwardPressedImg = new ImageIcon(BasicPlayerControlPanel.class.getClassLoader().getResource("icons/forward-pressed.png"));

        pauseImage = new ImageIcon(BasicPlayerControlPanel.class.getClassLoader().getResource("icons/pause.png"));

        pausePressedImage = new ImageIcon(BasicPlayerControlPanel.class.getClassLoader().getResource("icons/pause-pressed.png"));
    }

    public static final int VOL_MIN = 0;
    public static final int VOL_MAX = 200;
    public static final int VOL_INIT = 75;

    private JSlider volumeSlider;

    private static boolean pauseIconShown = false;

    protected JPanel panel;
    private JPanel wrapperPanel;
    private JPanel iconsPanel;
    private JPanel volumePanel;
    private JLabel volumeValueLabel;

    public BasicPlayerControlPanel() {


        playIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (listener != null) {
                    listener.playClicked();
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (!pauseIconShown) {
                    playIcon.setIcon(getPlayPressedIcon());
                } else {
                    playIcon.setIcon(getPausePressedIcon());
                }
            }
        });
        previousIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (listener != null) {
                    listener.previousClicked();
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                previousIcon.setIcon(previousPressedImg);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                previousIcon.setIcon(previousImg);
            }
        });
        forwardIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (listener != null) {
                    listener.forwardClicked();
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                forwardIcon.setIcon(forwardPressedImg);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                forwardIcon.setIcon(forwardImage);
            }
        });
    }

    private static ImageIcon getPreviousIcon() {
        return previousImg;
    }

    private static ImageIcon getPlayIcon() {
        return playImage;
    }

    private static ImageIcon getPlayPressedIcon() {
        return playPressedImg;
    }

    private static ImageIcon getForwardIcon() {
        return forwardImage;
    }

    private static ImageIcon getPauseIcon() {
        return pauseImage;
    }

    private static ImageIcon getPausePressedIcon() {
        return pausePressedImage;
    }

    public void setPaused() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                log.debug("Changing icon to: play");
                playIcon.setIcon(getPlayIcon());
                pauseIconShown = false;
            }
        });

    }

    public void setPlaying() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                log.debug("Changing icon to: pause");
                playIcon.setIcon(getPauseIcon());
                pauseIconShown = true;
            }
        });

    }

    public void setPlayerControlEventListener(final PlayerControlEventListener listener) {
        this.listener = listener;
    }

    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    private void createUIComponents() {
        volumeSlider = new JSlider(JSlider.HORIZONTAL, VOL_MIN, VOL_MAX, VOL_INIT);
        volumeValueLabel = new JLabel(String.valueOf(VOL_INIT));
        volumeValueLabel.setText(String.valueOf(VOL_INIT));
        volumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                if (!String.valueOf(volumeSlider.getValue()).equals(volumeValueLabel.getText())) {
                    volumeValueLabel.setText(String.valueOf(volumeSlider.getValue()));
                }
            }
        });

    }

}
