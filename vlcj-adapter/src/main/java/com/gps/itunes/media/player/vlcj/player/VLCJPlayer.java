package com.gps.itunes.media.player.vlcj.player;

import com.gps.itunes.media.player.vlcj.ui.player.events.*;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 * Created by leogps on 10/4/14.
 */
public interface VLCJPlayer {

    MediaPlayer getPlayer();

    void resetSeekbar();

    void registerSeekEventListener(SeekEventListener seekEventListener);

    void unRegisterSeekEventListener(SeekEventListener seekEventListener);

    void updateSeekbar(SeekInfo seekInfo);

    boolean isSeekValueAdjusting();

    void attachCommandListener(VideoPlayerKeyListener videoPlayerKeyListener);

    void attachCommandListener(VideoPlayerMouseAdapter videoPlayerMouseAdapter);

    void attachCommandListener(VideoPlayerMouseWheelListener videoPlayerMouseWheelListener);

    void setPaused();

    void setPlaying();

    void registerPlayerControlEventListener(PlayerControlEventListener playerControlEventListener);

    void setBufferingValue(float bufferingValue);

    void exitFullscreen();
}
