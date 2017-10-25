package com.gps.youtube.dl.event;

import com.gps.youtube.dl.YoutubeDLResult;

/**
 * Created by leogps on 10/24/2017.
 */
public class YoutubeDLResultEvent {

    private final YoutubeDLResult youtubeDLResult;

    public YoutubeDLResultEvent(YoutubeDLResult youtubeDLResult) {
        this.youtubeDLResult = youtubeDLResult;
    }

    public YoutubeDLResult getYoutubeDLResult() {
        return youtubeDLResult;
    }
}
