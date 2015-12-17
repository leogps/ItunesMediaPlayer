package com.gps.youtube.dl;

/**
 * Created by leogps on 12/17/15.
 */
public class YoutubeDLResult {
    private final String title;
    private final String url;
    private final String filename;
    private final String error;

    public YoutubeDLResult(String title, String url, String filename, String error) {
        this.title = title;
        this.url = url;
        this.filename = filename;
        this.error = error;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "YoutubeDLResult{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", filename='" + filename + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
