/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tasks;

/**
 *
 * Track data retrieved from the library file needs to be parsed to be shown
 * appropriately. This class provides the parsers for such data.
 *
 * @author leogps
 */
public class TrackDataParser {

    /**
     * 
     * Parse the time that is a long value into hrs:mins:secs format.
     * <note>If hrs is zero, format returned is mins:secs.</note>
     * 
     * @param totalTime
     * @return 
     */
    public static String parseTime(String totalTime) {
        if(totalTime == null) {
            return "";
        }
        return parseTime(Long.parseLong(totalTime));
    }

    public static String parseTime(long time) {

        long secsLong = time / 1000;
        long secs = secsLong % 60;

        long minsLong = secsLong / 60;
        long mins = minsLong % 60;

        long hoursLong = minsLong / 60;
        long hours = hoursLong % 24;

        return (hours == 0) ? getReadableVal(mins) + ":" + getReadableVal(secs) : getReadableVal(hours) + ":" + getReadableVal(mins) + ":" + getReadableVal(secs);
    }

    private static String getReadableVal(long time) {
        if(time < 1) {
            return "00";
        }
        if(time < 10) {
            return "0" + new Long(time).intValue();
        }

        return String.valueOf(new Long(time).intValue());
    }
}
