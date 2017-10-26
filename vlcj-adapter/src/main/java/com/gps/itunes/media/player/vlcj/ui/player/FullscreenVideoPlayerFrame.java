package com.gps.itunes.media.player.vlcj.ui.player;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by leogps on 10/6/14.
 */
public class FullscreenVideoPlayerFrame extends VideoPlayerFrame {

    private static final Logger LOGGER = Logger.getLogger(FullscreenVideoPlayerFrame.class);

    private static Future future;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final int AUTO_HIDE_HEADER_FOOTER_TIME = 5;

    public FullscreenVideoPlayerFrame() {
        super();
        //headerPanel.setVisible(false);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getFrameCanvas().setFocusable(true);
        getVideoPanel().setFocusable(true);

        headerPanel.addKeyListener(userKeyListener);
        footerPanel.addKeyListener(userKeyListener);
        getVideoPanel().addKeyListener(userKeyListener);
        getSeekbar().addKeyListener(userKeyListener);
        getFrameCanvas().addKeyListener(userKeyListener);

        headerPanel.addMouseMotionListener(mouseMotionListener);
        footerPanel.addMouseMotionListener(mouseMotionListener);
        getVideoPanel().addMouseMotionListener(mouseMotionListener);
        getSeekbar().addMouseMotionListener(mouseMotionListener);
        getFrameCanvas().addMouseMotionListener(mouseMotionListener);
    }

    private void handleUserAttentionRequest() {

        LOGGER.debug("User attention requested.");
        if(!getFooterPanel().isVisible()) {
            LOGGER.debug("Showing footer panel");
            getFooterPanel().setVisible(true);
        }
        reinitAutoHideLater();
    }

    protected void reinitAutoHideLater() {
        cancelAutoHideLaterTask();
        autoHideLater();
    }

    private void cancelAutoHideLaterTask() {
        if(future != null && !future.isCancelled()) {
            synchronized (future) {
                future.cancel(true);
            }
        }
    }

    private void autoHideLater() {
        future = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            public void run() {
                if(!getSeekbar().getValueIsAdjusting()
                        && !basicPlayerControlPanel.getVolumeSlider().getValueIsAdjusting()) {
                    LOGGER.debug("Autohiding... at " + Calendar.getInstance().getTime());
                    headerPanel.setVisible(false);
                    getFooterPanel().setVisible(false);
                }

            }
        }, AUTO_HIDE_HEADER_FOOTER_TIME, AUTO_HIDE_HEADER_FOOTER_TIME, TimeUnit.SECONDS);

    }

    protected KeyListener userKeyListener = new KeyListener() {

        public void keyTyped(KeyEvent keyEvent) {
            handleUserAttentionRequest();
        }

        public void keyPressed(KeyEvent keyEvent) {
            handleUserAttentionRequest();
        }

        public void keyReleased(KeyEvent keyEvent) {
            handleUserAttentionRequest();
        }
    };

    protected MouseMotionListener mouseMotionListener = new MouseMotionListener() {
        public void mouseDragged(MouseEvent mouseEvent) {
            handleUserAttentionRequest();
        }

        public void mouseMoved(MouseEvent mouseEvent) {
            handleUserAttentionRequest();
        }
    };


    public void showSliderAndHideLater() {
        headerPanel.setVisible(true);
        getFooterPanel().setVisible(true);
        reinitAutoHideLater();
    }

    public void drawPaused() {
        //getStatusPanel().drawPaused();
    }

    public void enableVideo(Canvas frameCanvas) {
        super.enableVideo(frameCanvas);
        getFrameCanvas().requestFocus();
        showSliderAndHideLater();
        LOGGER.debug("Enabled Fullscreen frame");
    }

    public void disableVideo() {
        super.disableVideo();
        cancelAutoHideLaterTask();
        LOGGER.debug("Disabled Fullscreen frame");
    }
}
