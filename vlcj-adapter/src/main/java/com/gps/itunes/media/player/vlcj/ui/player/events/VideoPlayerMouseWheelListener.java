package com.gps.itunes.media.player.vlcj.ui.player.events;

import org.apache.log4j.Logger;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leogps on 10/13/14.
 */
public class VideoPlayerMouseWheelListener implements MouseWheelListener {

    private List<UserCommandEventListener> userCommandEventListenerList = new ArrayList<UserCommandEventListener>();
    private static Logger log = Logger.getLogger(VideoPlayerMouseAdapter.class);

    public void addUserCommandEventListener(UserCommandEventListener userCommandEventListener) {
        this.userCommandEventListenerList.add(userCommandEventListener);
    }

    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        int notches = mouseWheelEvent.getWheelRotation();

        for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
            userCommandEventListener.onAttentionRequested();
            if(mouseWheelEvent.isShiftDown()) {
                if(notches < 0) {
                    log.debug("Mouse wheel moved RIGHT "
                            + notches + " notch(es)");
                    userCommandEventListener.onSeekIncreasedCommand(-notches);
                } else {
                    log.debug("Mouse wheel moved LEFT "
                            + -notches + " notch(es)");
                    userCommandEventListener.onSeekDecreasedCommand(-notches);
                }

            } else {
                if (notches < 0) {
                    log.debug("Mouse wheel moved UP "
                            + -notches + " notch(es)");
                    userCommandEventListener.onVolumeDecreaseCommand(-notches);
                } else {
                    log.debug("Mouse wheel moved DOWN "
                            + notches + " notch(es)");
                    userCommandEventListener.onVolumeIncreaseCommand(notches);
                }
            }
        }
    }
}
