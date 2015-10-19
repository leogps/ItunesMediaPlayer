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

    //FIXME: Add 'ID' and make it unique, update equals method. This will make this list editable.

    private final long trackId;
    private final String name;
    private final String artist;
    private final String album;
    private final String location;
    private final boolean isMovie;

    public NowPlayingListData(final long trackId, final String name, final String artist,
            final String album, final String location, final boolean isMovie) {
        this.trackId = trackId;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.location = location;
        this.isMovie = isMovie;
    }

    public long getTrackId() {
        return trackId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NowPlayingListData)) return false;

        NowPlayingListData that = (NowPlayingListData) o;

        if (trackId != that.trackId) return false;
        if (isMovie != that.isMovie) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (artist != null ? !artist.equals(that.artist) : that.artist != null) return false;
        if (album != null ? !album.equals(that.album) : that.album != null) return false;
        return !(location != null ? !location.equals(that.location) : that.location != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (trackId ^ (trackId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (isMovie ? 1 : 0);
        return result;
    }
}
