package com.gps.itunes.media.player.ui.events;

import com.gps.itunes.lib.items.tracks.Track;
import com.gps.itunes.media.player.ui.UIFrame;

import java.util.List;

/**
 * Created by leogps on 10/5/14.
 */
public abstract class UIFrameEventListener {

    public abstract void onPlaylistSelectedEvent(UIFrame uiFrame);

    public abstract void onTracksPlayRequested(List<Track> trackList);

    public abstract void onReloadLibraryRequested(UIFrame uiFrame);

    public abstract void onCopyPlaylistRequested(UIFrame uiFrame);

    public abstract void onSearch(String searchQuery, UIFrame uiFrame);

    public abstract void onFileOpenRequested();

    public abstract void onNetworkFileOpenRequested();
}
