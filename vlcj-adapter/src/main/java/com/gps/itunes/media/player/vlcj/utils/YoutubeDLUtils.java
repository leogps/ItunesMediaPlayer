package com.gps.itunes.media.player.vlcj.utils;

import com.gps.itunes.lib.parser.utils.PropertyManager;

import java.io.File;

/**
 * Created by leogps on 2/25/17.
 */
public class YoutubeDLUtils {

    public static String fetchYoutubeDLExecutable() {
        String youtubeDLAbsolutePath = PropertyManager.getConfigurationMap().get("youtube-dl-executable-absolute-path");
        if(youtubeDLAbsolutePath != null) {
            return youtubeDLAbsolutePath;
        }
        return new File("").getAbsolutePath() + PropertyManager.getConfigurationMap().get("youtube-dl-executable");
    }

    public static String fetchAdditionalArgs() {
        String youtubeDLAdditionalArgs = PropertyManager.getConfigurationMap().get("youtube-dl.additional-args");
        if (youtubeDLAdditionalArgs != null) {
            return youtubeDLAdditionalArgs;
        }
        return "";
    }

}