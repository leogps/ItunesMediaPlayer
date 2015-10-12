package com.gps.itunes.media.player.vlcj.ui.player.events;

import org.apache.log4j.Logger;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leogps on 10/6/14.
 */
public class VideoPlayerMouseAdapter implements MouseListener {

    private List<UserCommandEventListener> userCommandEventListenerList = new ArrayList<UserCommandEventListener>();
    private static Logger log = Logger.getLogger(VideoPlayerMouseAdapter.class);


    public void addUserCommandEventListener(UserCommandEventListener userCommandEventListener) {
        this.userCommandEventListenerList.add(userCommandEventListener);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
            userCommandEventListener.onAttentionRequested();
        }

        log.debug("Mouse Clicked");
        if (mouseEvent.getClickCount() == 2 && !mouseEvent.isConsumed()) {
            mouseEvent.consume();
            for(UserCommandEventListener userCommandEventListener : userCommandEventListenerList) {
                userCommandEventListener.onFullScreenToggleCommand();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }
}
