package com.gps.itunes.media.player.vlcj.ui.player.events;

import com.gps.imp.utils.Constants;
import com.gps.imp.utils.ui.ApplicationExitHandler;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Listener class for keyboard shortcuts on the application(JFrame).
 * <br/>
 *
 * Created by leogps on 10/5/14.
 */
public class VideoPlayerKeyListener extends PlayerKeyEventListener {

    private final JFrame videoPlayerFrame;

    public VideoPlayerKeyListener(JFrame videoPlayerFrame) {
        this.videoPlayerFrame = videoPlayerFrame;
    }

    public void addUserCommandEventListener(UserCommandEventListener userCommandEventListener) {
        this.userCommandEventListenerList.add(userCommandEventListener);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

        super.keyPressed(keyEvent);

        /**
         * ESC => Exit Fullscreen
         */
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onExitFullscreenCommand();
            }
        }

        /**
         * CTRL + Enter => Fullscreen <= CMD + Enter
         */
        if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                && ( ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0) || (keyEvent.getModifiers() & KeyEvent.VK_META) != 0)) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onFullScreenToggleCommand();
            }
        }

        /**
         * SPACE => Pause/Play
         */
        if(keyEvent.getKeyCode() == keyEvent.VK_SPACE) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onPlayToggleCommand();
            }
        }

        /**
         * M => Mute/UnMute
         */
        if(keyEvent.getKeyCode() == keyEvent.VK_M) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onMuteToggleCommand();
            }
        }

        /**
         * Shift + UP => Volume Increase by 10.
         */
        if ((keyEvent.getKeyCode() == KeyEvent.VK_UP || keyEvent.getKeyCode() == KeyEvent.VK_KP_UP)
                && ((keyEvent.getModifiers() & KeyEvent.SHIFT_MASK) != 0)) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onVolumeIncreaseCommand(Constants.DEFAULT_VOLUME_CHANGE);
            }
        }

        /**
         * Shift + Down => Volume Decrease by 10.
         */
        if ((keyEvent.getKeyCode() == KeyEvent.VK_DOWN || keyEvent.getKeyCode() == KeyEvent.VK_KP_DOWN)
                && ((keyEvent.getModifiers() & KeyEvent.SHIFT_MASK) != 0)) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onVolumeDecreaseCommand(Constants.DEFAULT_VOLUME_CHANGE);
            }
        }

        /**
         * Shift + Right => Fast Forward by 10.
         */
        if ((keyEvent.getKeyCode() == KeyEvent.VK_RIGHT || keyEvent.getKeyCode() == KeyEvent.VK_KP_RIGHT)
                && ((keyEvent.getModifiers() & KeyEvent.SHIFT_MASK) != 0)) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onFastForwardCommand();
            }
        }

        /**
         * Right => Skip Forward by 3sec.
         */
        if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT || keyEvent.getKeyCode() == KeyEvent.VK_KP_RIGHT) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onSkipForwardCommand();
            }
        }

        /**
         * Shift + Left => Fast Reverse by 10.
         */
        if ((keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_KP_LEFT)
                && ((keyEvent.getModifiers() & KeyEvent.SHIFT_MASK) != 0)) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onFastReverseCommand();
            }
        }

        /**
         * Left => Skip Reverse by 3sec.
         */
        if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_KP_LEFT) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onSkipReverseCommand();
            }
        }

        /**
         * App Exit.
         */
        if (keyEvent.getKeyCode() == KeyEvent.VK_Q
                && (((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0) || (keyEvent.getModifiers() & KeyEvent.VK_META) != 0)) {
            ApplicationExitHandler.handle(videoPlayerFrame);
        }
    }
}
