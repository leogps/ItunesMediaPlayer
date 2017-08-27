package com.gps.itunes.media.player.ui.events;

import com.gps.itunes.lib.items.tracks.Track;
import com.gps.itunes.media.player.ui.UIFrame;

import java.util.List;

/**
 * Created by leogps on 10/5/14.
 */
public interface UIFrameEventListener {

    void onPlaylistSelectedEvent(UIFrame uiFrame);

    void onTracksPlayRequested(List<Track> trackList);

    void onReloadLibraryRequested(UIFrame uiFrame);

    void onCopyPlaylistRequested(UIFrame uiFrame);

    void onSearch(String searchQuery, UIFrame uiFrame);

    void onFileOpenRequested();

    void onNetworkFileOpenRequested();
}
