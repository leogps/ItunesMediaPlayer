/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.vlcj.ui.player;

/**
 *
 * @author leogps
 */
public class NowPlayingListData {

    private final String name;
    private final String artist;
    private final String album;
    private final String location;
    private final boolean isMovie;

    public NowPlayingListData(final String name, final String artist,
            final String album, final String location, final boolean isMovie) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.location = location;
        this.isMovie = isMovie;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public boolean isMovie() {
        return isMovie;
    }

    @Override
    public String toString() {
        return name;
    }
}
