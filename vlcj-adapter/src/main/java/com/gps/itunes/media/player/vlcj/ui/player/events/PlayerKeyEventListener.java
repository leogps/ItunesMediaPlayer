package com.gps.itunes.media.player.vlcj.ui.player.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leogps on 11/20/14.
 */
public class PlayerKeyEventListener implements KeyListener {

    protected List<UserCommandEventListener> userCommandEventListenerList = new ArrayList<UserCommandEventListener>();

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {

        /**
         * CTRL + O => Open File
         */
        if ((keyEvent.getKeyCode() == KeyEvent.VK_O)
                && ( ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0) || (keyEvent.getModifiers() & KeyEvent.VK_META) != 0)) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onFileOpenCommand();
            }
        }

        /**
         * CTRL + N => Open Network File
         */
        if ((keyEvent.getKeyCode() == KeyEvent.VK_N)
                && ( ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0) || (keyEvent.getModifiers() & KeyEvent.VK_META) != 0)) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onNetworkFileOpenCommand();
            }
        }

        if(keyEvent.getKeyCode() == KeyEvent.VK_V) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onToggleSubtitles();
            }
        }

        /**
         * CTRL + G => Go to specific time in track.
         */
        if ((keyEvent.getKeyCode() == KeyEvent.VK_G)
                && ( ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0) || (keyEvent.getModifiers() & KeyEvent.VK_META) != 0)) {
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.goTo();
            }
        }

    }

    public void keyReleased(KeyEvent keyEvent) {
    }
}
