/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.dto;

import com.gps.itunes.lib.items.tracks.Track;

import java.io.UnsupportedEncodingException;

/**
 *
 * Holds the {@link com.gps.itunes.lib.items.tracks.Track Track} object.
 * <p>
 * This is used to be set as value for Playlist table column. The {@link java.lang.Object#toString() toString()}
 * method's return value is shown as the column's value.
 * </p>
 * 
 * @author leogps
 */
public class TrackHolder {
    
    private final Track track;
    private String name;
    
    
    public TrackHolder(final Track track){
        this.track = track;
        try {
            name = (this.track != null && this.track.getTrackName() != null)
                    ? new String(this.track.getTrackName().getBytes(), "UTF-8") : null;
        } catch (UnsupportedEncodingException e) {
            name = (this.track != null && this.track.getTrackName() != null)
                    ? this.track.getTrackName() : null;
        }
    }

    /**
     * Get the track the object is holding.
     * 
     * @return 
     */
    public Track getTrack() {
        return track;
    }
    
    
    /**
     * 
     * 
     * Used to show the value in the Table column.
     * 
     * @return 
     */
    @Override
    public String toString(){
        return name;
    }
    
}
