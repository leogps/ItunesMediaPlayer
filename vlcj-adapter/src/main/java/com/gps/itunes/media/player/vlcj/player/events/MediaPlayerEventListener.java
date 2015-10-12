/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.vlcj.player.events;

import com.gps.itunes.media.player.vlcj.player.ItunesMediaPlayer;
import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListData;

/**
 *
 * @author leogps
 */
public interface MediaPlayerEventListener {

    /**
     * Invoked when media starts playing.
     *
     * @param player
     * @param currentTrack
     */
    void playing(final ItunesMediaPlayer player, final NowPlayingListData currentTrack);

    void paused(final ItunesMediaPlayer player, final String location);

    void stopped(final ItunesMediaPlayer player, final String location);

    void finished(final ItunesMediaPlayer player, final String location);

    void onPlayProgressed();
}
