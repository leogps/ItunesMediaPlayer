package com.gps.itunes.media.player.vlcj.ui.player.events;

/**
 * Created by leogps on 10/5/14.
 */
public abstract class UserCommandEventListener {

    /**
     * Executed when fullscreen toggle is requested.
     */
    public abstract void onFullScreenToggleCommand();

    /**
     * Executed when play/pause toggle requested.
     */
    public abstract void onPlayToggleCommand();

    public abstract void onVolumeIncreaseCommand(int increasedBy);

    public abstract void onVolumeDecreaseCommand(int decreasedBy);

    public abstract void onFastForwardCommand();

    public abstract void onFastReverseCommand();

    public abstract void onSkipForwardCommand();

    public abstract void onSkipReverseCommand();

    public abstract void onExitFullscreenCommand();

    public abstract void onAttentionRequested();

    public abstract void onFileOpenCommand();

    public abstract void onNetworkFileOpenCommand();

    public abstract void onSeekDecreasedCommand(int decreasedBy);

    public abstract void onSeekIncreasedCommand(int increasedBy);

    public abstract void onToggleSubtitles();

    public abstract void onMuteToggleCommand();

    public abstract void goTo();
}
