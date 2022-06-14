/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tablehelpers.comparators;

/**
 *
 * @author leogps
 */
class TrackTime {
    
    private final int hrs;
    private final int mins;
    private final int secs;
    private static org.apache.log4j.Logger log =
            org.apache.log4j.LogManager.getLogger(TrackTime.class);
    
    public TrackTime(final int hrs, final int mins, final int secs) {
        this.hrs = hrs;
        this.mins = mins;
        this.secs = secs;
    }
    
    public int getHrs() {
        return hrs;
    }
    
    public int getMins() {
        return mins;
    }
    
    public int getSecs() {
        return secs;
    }
    
    public static TrackTime parseTime(final String obj) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        
        try {
            final String[] timeSplit = obj.split(":");
            
            if (timeSplit.length == 3) {
                
                hours = Integer.parseInt(timeSplit[0]);
                minutes = Integer.parseInt(timeSplit[1]);
                seconds = Integer.parseInt(timeSplit[2]);
            } else {
                minutes = Integer.parseInt(timeSplit[0]);
                seconds = Integer.parseInt(timeSplit[1]);
            }
            
            return new TrackTime(hours, minutes, seconds);
            
        } catch (final Exception e) {
            
            log.error("Invalid time found.", e);
            
            return new TrackTime(hours, minutes, seconds);
        }
    }
}
