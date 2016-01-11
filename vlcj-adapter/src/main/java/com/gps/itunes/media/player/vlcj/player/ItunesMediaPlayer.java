package com.gps.itunes.media.player.vlcj.player;

import com.gps.itunes.lib.items.tracks.Track;
import com.gps.itunes.media.player.vlcj.player.events.MediaPlayerEventListener;
import com.gps.itunes.media.player.vlcj.player.impl.TraversableLinkedList;
import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListData;
import com.gps.itunes.media.player.vlcj.ui.player.events.PlayerControlEventListener;

import java.io.File;
import java.net.URL;
import java.util.List;
import javax.swing.JPanel;

public interface ItunesMediaPlayer extends Runnable {

    void play();

    void play(File file);

    void play(URL url);

    void play(final List<Track> trackList);

    void pause();

    void toggleMute();

    void stopPlay();

    void previous();

    boolean hasPrevious();

    boolean hasNext();

    void next();

    void seekTo(float percentage);

    int getVolume();

    void setVolume(int volume);

    String getNowPlayingUrl();

    TraversableLinkedList<NowPlayingListData> getNowPlaylingList();

    boolean isPlaying();

    void addMediaPlayerListener(final MediaPlayerEventListener listener);

    JPanel getPlayerControlPanel();

    void clearNowPlayingList();

    void handleFileOpenEvent();

    void handleNetworkFileOpenEvent();

    void handleGoToEvent();

    void handleVolumeIncreasedEvent(int increasedBy);

    void handleVolumeDecreasedEvent(int decreasedBy);

    void registerPlayerControlEventListener(PlayerControlEventListener playerControlEventListener);

    boolean isCurrentTrack(long trackId);

    void releaseResources();
}
