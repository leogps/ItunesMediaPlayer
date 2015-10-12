package com.gps.itunes.media.player.vlcj.player;

/**
 * Created by leogps on 10/5/14.
 */
public class SeekInfo {

    private final long trackStartValue;
    private final long trackEndValue;
    private final int seekPosition;

    public SeekInfo(long trackStartValue, long trackEndValue, int seekPosition) {
        this.trackStartValue = trackStartValue;
        this.trackEndValue = trackEndValue;
        this.seekPosition = seekPosition;
    }

    public long getTrackStartValue() {
        return trackStartValue;
    }

    public long getTrackEndValue() {
        return trackEndValue;
    }

    public int getSeekPosition() {
        return seekPosition;
    }

    public static int inSecs(long time) {
        Long secsLong = time / 1000;
        return secsLong.intValue();
    }

    public static String convertIntoReadableTime(long time) {

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
