package com.gps.imp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leogps on 11/27/14.
 */
public class YoutubeUrlFetcher {

    public static List<YoutubeLink> fetch(String videoUrl) throws IOException, URLFetchException {
        InputStream inStream = null;
        BufferedReader in = null;

        List<YoutubeLink> youtubeLinks = new ArrayList<YoutubeLink>();

        try {

            Map<String, String> videoQueryMap = getQueryMapFromURL(videoUrl);
            String vid = videoQueryMap.get("v");
            if(vid == null) {
                throw new URLFetchException("video param 'v' not found in the url");
            }

            URL url = new URL("https://www.youtube.com/get_video_info?video_id=" + vid);
            inStream = url.openStream();
            in = new BufferedReader(new InputStreamReader(inStream));
            String response = "", temp;
            while ((temp = in.readLine()) != null) {
                response += temp;
            }

            Map<String, String> parameters = getQueryMap(response);
            if (parameters.containsKey("reason")) {
                throw new URLFetchException(URLDecoder.decode(parameters.get("reason"), "UTF-8"));
            }
            String title = parameters.get("title");
//            String thubnailUrl = parameters.get("thumbnail_url");
//            links.setThumbnailUrl(URLDecoder.decode(thubnailUrl, "UTF-8"));
            title = URLDecoder.decode(title, "UTF-8");
            title = title.replaceAll("[^a-zA-Z0-9\\s]+", "");

            String url_encoded_fmt_stream_map = parameters.get("url_encoded_fmt_stream_map");
            url_encoded_fmt_stream_map = URLDecoder.decode(url_encoded_fmt_stream_map, "UTF-8");
            String[] urls = url_encoded_fmt_stream_map.split(",");
            for (String u : urls) {
                Map<String, String> urlParameters = getQueryMap(u);

                String quality = urlParameters.get("quality");
                quality = URLDecoder.decode(quality, "UTF-8");
                String type = urlParameters.get("type");
                type = URLDecoder.decode(type, "UTF-8");

                YoutubeLink.Extension extension = YoutubeLink.Extension.getExtension(type);
                String fileName = title + "." + extension.getName();
                String ul = urlParameters.get("url");
                ul = URLDecoder.decode(ul, "UTF-8");


                YoutubeLink youtubeLink = new YoutubeLink(ul, YoutubeLink.Quality.getQuality(quality), extension, fileName);

                youtubeLinks.add(youtubeLink);

            }


        } catch (Exception ex) {
            throw new URLFetchException(ex);
        }

        return youtubeLinks;
    }

    private static Map<String, String> getQueryMapFromURL(String url) {
        String query = url.substring(url.indexOf("?") + 1);
        return getQueryMap(query);

    }

    private static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String[] values = param.split("=");
            if (values.length > 1) {
                map.put(values[0], values[1]);
            } else {
                map.put(values[0], "NA");
            }


        }
        return map;
    }

    public static void main(String args[]) throws IOException, URLFetchException {
        List<YoutubeLink> youtubeLinks = new YoutubeUrlFetcher().fetch("https://www.youtube.com/watch?v=snzDwACffzs");
        System.out.println(youtubeLinks);
        System.out.println("Best link: " + getBest(youtubeLinks));

    }

    public static YoutubeLink getBest(List<YoutubeLink> youtubeLinkList) {
        YoutubeLink best = null;
        for(YoutubeLink youtubeLink : youtubeLinkList) {
            if(best == null) {
                best = youtubeLink;
                continue;
            }
            if(compare(youtubeLink.getQuality(), best.getQuality())) {
                best = youtubeLink;
            }
        }

        return best;
    }

    private static boolean compare(YoutubeLink.Quality quality, YoutubeLink.Quality bestQuality) {

        if(bestQuality == YoutubeLink.Quality.THOUSAND_80_P) {
            return false;
        }
        if(bestQuality == YoutubeLink.Quality.SEVEN_20_P && quality != YoutubeLink.Quality.THOUSAND_80_P) {
            return false;
        }
        if(bestQuality == YoutubeLink.Quality.FOUR_80_P && quality != YoutubeLink.Quality.SEVEN_20_P) {
            return false;
        }
        if(bestQuality == YoutubeLink.Quality.THREE_60_P && quality != YoutubeLink.Quality.FOUR_80_P && quality != YoutubeLink.Quality.SEVEN_20_P) {
            return false;
        }
        if(bestQuality == YoutubeLink.Quality.TWO_40_P && quality != YoutubeLink.Quality.THREE_60_P && quality != YoutubeLink.Quality.FOUR_80_P && quality != YoutubeLink.Quality.SEVEN_20_P) {
            return false;
        }

        return true;
    }
}



