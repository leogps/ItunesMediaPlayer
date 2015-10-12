package com.gps.itunes.media.player.vlcj.ui.player.utils;

/**
 * Created by leogps on 9/21/15.
 */
public class TrackTime {
    private final int hour;
    private final int min;
    private final int sec;

    public TrackTime(int hour, int min, int sec) {
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }

    @Override
    public String toString() {
        return hour + ":" + min + ":" + sec;
    }

    public static TrackTime get(long time) {
        long secsLong = time / 1000;
        long secs = secsLong % 60;

        long minsLong = secsLong / 60;
        long mins = minsLong % 60;

        long hoursLong = minsLong / 60;
        long hours = hoursLong % 24;

        TrackTime trackTime = new TrackTime(toInt(hours), toInt(mins), toInt(secs));
        return trackTime;
    }

    private static int toInt(Long hours) {
        return hours.intValue();
    }

    public static long valueOf(TrackTime trackTime) {
        long value = trackTime.getHour() * 60 * 60 * 1000;
        value += trackTime.getMin() * 60 * 1000;
        value += trackTime.getSec() * 1000;
        return value;
    }
}
