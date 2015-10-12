/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tablehelpers.columns;

/**
 *
 * The Tracks table's columns.
 * 
 * 
 * @author leogps
 */
public enum TrackTableColumns {
    
    S_NO("#", 20),
    Name("Name", 120),
    Time("Time", 30),
    Artist("Artist", 80),
    Album("Album", 80),
    Genre("Genre", 40),
    Rating("Rating", 10),
    Plays("Plays", 10);
    
    private final String name;
    private final int width;

    TrackTableColumns(final String name, final int width){
        this.name = name;
        this.width = width;
    }

    /**
     * 
     * Return the name associated with the column.
     * 
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 
     * Returns the name associated with the column.
     * 
     * @return 
     */
    @Override
    public String toString(){
        return getName();
    }
    
    
    /**
     * Array containing all the TrackTableColumns.
     * 
     */
    public static final TrackTableColumns[] COLUMNS = {
       TrackTableColumns.S_NO,
       TrackTableColumns.Name,
       TrackTableColumns.Time,
       TrackTableColumns.Artist,
       TrackTableColumns.Album,
       TrackTableColumns.Genre,
       TrackTableColumns.Rating,
       TrackTableColumns.Plays
    };
}
