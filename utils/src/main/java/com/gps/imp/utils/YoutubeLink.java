package com.gps.imp.utils;

import java.util.EnumSet;

/**
 * Created by leogps on 11/27/14.
 */
public class YoutubeLink {

    private final String url;
    private final Quality quality;
    private final Extension extension;
    private final String fileName;

    public YoutubeLink(String url, Quality quality, Extension extension, String fileName) {
        this.url = url;
        this.quality = quality;
        this.extension = extension;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public enum Quality {
        TWO_40_P("small", "240p"), THREE_60_P("medium", "360p"), FOUR_80_P("large", "480p"), SEVEN_20_P("hd720", "720p"), THOUSAND_80_P("hd1080", "1080p");

        Quality(String name, String abbrev) {
            this.name = name;
            this.abbrev = abbrev;
        }

        private String name;
        private String abbrev;

        public static Quality getQuality(String quality) {
            for(Quality q : EnumSet.allOf(Quality.class)) {
                if(q.name.equals(quality)) {
                    return q;
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAbbrev() {
            return abbrev;
        }

        public void setAbbrev(String abbrev) {
            this.abbrev = abbrev;
        }
    }

    public enum Extension {
        WEBM("webm", "video/webm"),
        FLV("flv", "video/x-flv"),
        MP4("mp4", "video/mp4"),
        THREE_GPP("3gpp", "video/3gpp"),
        FORMAT("format", "format");

        Extension(String abbrev, String name) {
            this.abbrev = abbrev;
            this.name = name;
        }

        private String abbrev;
        private String name;

        public String getName() {
            return name;
        }

        public String toString() {
            return name;
        }

        public static Extension getExtension(String type) {

            for(Extension ext : EnumSet.allOf(Extension.class)) {
                if(type.contains(ext.name)) {
                    return ext;
                }
            }

            return FORMAT;
        }
    }

    public String getUrl() {
        return url;
    }

    public Quality getQuality() {
        return quality;
    }

    public Extension getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return "YoutubeLink{" +
                "url='" + url + '\'' +
                ", quality=" + quality +
                ", extension=" + extension +
                '}';
    }
}
