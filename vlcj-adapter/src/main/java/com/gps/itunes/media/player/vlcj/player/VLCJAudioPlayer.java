package com.gps.itunes.media.player.vlcj.player;

import com.gps.itunes.media.player.vlcj.ui.player.PlayerControlPanel;
import com.gps.itunes.media.player.vlcj.ui.player.events.*;
import org.apache.log4j.Logger;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class VLCJAudioPlayer implements VLCJPlayer {

    private static Logger log = Logger.getLogger(VLCJAudioPlayer.class);
	
	private EmbeddedMediaPlayer player;
    private PlayerControlPanel playerControlPanel;
    private AtomicBoolean ignoreSeekbarChange = new AtomicBoolean(false);

    private final List<SeekEventListener> seekEventListenerList = new ArrayList<SeekEventListener>();
	
	public VLCJAudioPlayer(MediaPlayerFactory mediaPlayerFactory, final PlayerControlPanel playerControlPanel) {
		player = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        this.playerControlPanel = playerControlPanel;

        playerControlPanel.getSeekbar().addChangeListener(
                new SeekChangeListener(ignoreSeekbarChange,
                        playerControlPanel.getSeekbar(),
                        seekEventListenerList));
	}

    public MediaPlayer getPlayer() {
		return player;
	}

    public void resetSeekbar() {
        ignoreSeekbarChange.set(true);
        playerControlPanel.resetSeekbar();
        ignoreSeekbarChange.set(false);
    }

    public void registerSeekEventListener(SeekEventListener seekEventListener) {
        synchronized (seekEventListenerList) {
            seekEventListenerList.add(seekEventListener);
        }
    }

    public void unRegisterSeekEventListener(SeekEventListener seekEventListener) {
        synchronized (seekEventListenerList) {
            Iterator iterator = seekEventListenerList.iterator();
            while(iterator.hasNext()) {
                if(seekEventListener == iterator.next()) {
                    iterator.remove();
                }
            }
        }
    }

    public void updateSeekbar(SeekInfo seekInfo) {
        ignoreSeekbarChange.set(true);
        playerControlPanel.getStartTimeLabel().setText(SeekInfo.convertIntoReadableTime(seekInfo.getTrackStartValue()));
        playerControlPanel.getSeekbar().setValue(seekInfo.getSeekPosition());
        playerControlPanel.getEndTimeLabel().setText(SeekInfo.convertIntoReadableTime(seekInfo.getTrackEndValue()));
        if(!playerControlPanel.getSeekbar().getValueIsAdjusting()) {
            playerControlPanel.updateSeekbarUI();
        }
        playerControlPanel.updateSeekbar(seekInfo.getSeekPosition());
        ignoreSeekbarChange.set(false);
    }

    public boolean isSeekValueAdjusting() {
        return playerControlPanel.getSeekbar().getValueIsAdjusting();
    }

    public void attachCommandListener(VideoPlayerKeyListener videoPlayerKeyListener) {
        //TODO: Should we?
    }

    public void attachCommandListener(VideoPlayerMouseAdapter videoPlayerMouseAdapter) {

    }

    public void attachCommandListener(VideoPlayerMouseWheelListener videoPlayerMouseWheelListener) {

    }

    public void setPaused() {
        playerControlPanel.setPaused();
    }

    public void setPlaying() {
        playerControlPanel.setPlaying();
    }

    public void registerPlayerControlEventListener(PlayerControlEventListener playerControlEventListener) {
        playerControlPanel.setPlayerControlEventListener(playerControlEventListener);
    }

    public void setBufferingValue(float bufferingValue) {
        //TODO: Use Buffering Value.
    }

    public void exitFullscreen() {
        // NOOP
    }

    public void registerDragAndDropEvent(DropTarget dropTarget) {
        // NOOP
    }

}
