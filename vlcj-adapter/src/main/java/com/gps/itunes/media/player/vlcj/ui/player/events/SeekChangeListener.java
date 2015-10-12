package com.gps.itunes.media.player.vlcj.ui.player.events;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Changelistener that's invoked when user seeks.
 *
 *
 * Created by leogps on 10/6/14.
 */
public class SeekChangeListener implements ChangeListener {

    private final AtomicBoolean ignoreSeekbarChange;
    private final JSlider jSlider;
    private List<SeekEventListener> seekEventListenerList = new ArrayList<SeekEventListener>();

    public SeekChangeListener(AtomicBoolean ignoreSeekbarChange,
                              JSlider jSlider, List<SeekEventListener> seekEventListenerList) {
        this.ignoreSeekbarChange = ignoreSeekbarChange;
        this.jSlider = jSlider;
        this.seekEventListenerList = seekEventListenerList;
    }


    public void stateChanged(ChangeEvent changeEvent) {

        int newSeekValue = jSlider.getValue();
        if(!ignoreSeekbarChange.get()) {
            for (SeekEventListener seekEventListener : seekEventListenerList) {
                seekEventListener.onSeeked(newSeekValue);
            }
        }
    }
}
