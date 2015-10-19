/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.dto;

import com.gps.itunes.lib.items.playlists.Playlist;

import java.io.UnsupportedEncodingException;

/**
 *
 * Holds the {@link #com.gps.itunes.lib.items.playlists.Playlist Playlist} object.
 * <p>
 * This is used to be set as value for Playlist table column. The {@link #java.Object.toString() toString()}
 * method's return value is shown as the column's value.
 * </p>
 * 
 * @author leogps
 */
public class PlaylistHolder {

    private final Playlist playlist;
    private String name;

    public PlaylistHolder(final Playlist playlist) {
        this.playlist = playlist;
        try {
            name = (this.playlist != null && this.playlist.getName() != null)
                    ? new String(this.playlist.getName().getBytes(), "UTF-8") : null;
        } catch (UnsupportedEncodingException e) {
            name = (this.playlist != null && this.playlist.getName() != null)
                    ? this.playlist.getName() : null;
        }
    }

    /**
     * 
     * Return the underlying {@link #com.gps.itunes.lib.items.playlists.Playlist Playlist} object.
     * 
     * @return 
     */
    public Playlist getPlaylist() {
        return playlist;
    }

    /**
     * 
     * Used to show the value in the Table column.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return name;
    }
}
