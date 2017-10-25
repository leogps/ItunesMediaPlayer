package com.gps.youtube.dl;

import com.gps.imp.utils.process.AsyncProcess;
import com.gps.imp.utils.process.AsyncProcessImpl;
import com.gps.imp.utils.ui.AsyncTaskListener;
import com.gps.youtube.dl.event.YoutubeDLResultEventListener;
import com.gps.youtube.dl.exception.YoutubeDLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by leogps on 12/17/15.
 */
public class YoutubeDL {

    private static final String YOUTUBE_HOST = "youtube.com";
    private static final String YOUTUBE_HOST_WWW = "www.youtube.com";
    private static final String YOUTUBE_HOST_SHORT = "youtu.be";
    private static final String YOUTUBE_WATCH_PATH = "/watch";
    private static final String YOUTUBE_PLAYLIST_PATH = "/playlist";
    private static final String YOUTUBE_VIDEO_PARAM = "v";
    private static final String YOUTUBE_PLAYLIST_PARAM = "list";

    private static final Logger LOGGER = Logger.getLogger(YoutubeDL.class);

    public static YoutubeDLResult fetchBest(String youtubeDlExecutable, String input) throws IOException, InterruptedException, YoutubeDLException {
        return fetchURL(youtubeDlExecutable, "best", input);
    }

    public static void fetchPlaylist(String youtubeDlExecutable, String input, YoutubeDLResultEventListener... observers) throws InterruptedException, YoutubeDLException, IOException {
        String[] command = buildRetrievalCommand(youtubeDlExecutable, "best", input);
        YoutubeDLProcessor youtubeDLProcessor = new YoutubeDLProcessor(command);
        if(observers != null && observers.length > 0) {
            for(YoutubeDLResultEventListener observer : observers) {
                youtubeDLProcessor.addYoutubeDLResultEventListener(observer);
            }
        }
        youtubeDLProcessor.execute();
        youtubeDLProcessor.waitFor();
    }

    public static AsyncProcess fetchPlaylistAsync(String youtubeDlExecutable, String input, YoutubeDLResultEventListener... observers) throws InterruptedException, YoutubeDLException, IOException {
        String[] command = buildRetrievalCommand(youtubeDlExecutable, "best", input);
        YoutubeDLProcessor youtubeDLProcessor = new YoutubeDLProcessor(command);
        if(observers != null && observers.length > 0) {
            for(YoutubeDLResultEventListener observer : observers) {
                youtubeDLProcessor.addYoutubeDLResultEventListener(observer);
            }
        }
        return youtubeDLProcessor;
    }

    public static boolean hasPlaylist(String url) throws URISyntaxException {
        if(isYoutubeURL(url)) {
            List<NameValuePair> nameValuePairs = new URIBuilder(url).getQueryParams();
            if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
                for (NameValuePair nameValuePair : nameValuePairs) {
                    if (StringUtils.equalsIgnoreCase(YOUTUBE_PLAYLIST_PARAM, nameValuePair.getName()) &&
                            StringUtils.isNotBlank(nameValuePair.getValue())) {
                        LOGGER.debug("HasPlaylist: " + true);
                        return true;
                    }
                }
            }
        }
        LOGGER.debug("HasPlaylist: " + false);
        return false;
    }

    public static boolean isYoutubeURL(String url) throws URISyntaxException {
        if(StringUtils.isNotBlank(url)) {
            String host = new URIBuilder(url).getHost();
            return StringUtils.equalsIgnoreCase(host, YOUTUBE_HOST) ||
                    StringUtils.equalsIgnoreCase(host, YOUTUBE_HOST_WWW) ||
                    StringUtils.equalsIgnoreCase(host, YOUTUBE_HOST_SHORT);
        }
        return false;
    }

    public static boolean isWatchURL(String url) throws URISyntaxException {
        if(isYoutubeURL(url)) {
            URIBuilder uriBuilder = new URIBuilder(url);
            String path = uriBuilder.getPath();
            return StringUtils.equalsIgnoreCase(path, YOUTUBE_WATCH_PATH);
        }
        return false;
    }

    public static AsyncProcess fetchBestAsyncProcess(String youtubeDlExecutable, String input,
                                                     List<AsyncTaskListener> asyncTaskListenerList) throws IOException {
        String[] retrievalCommand = buildRetrievalCommand(youtubeDlExecutable, "best", input);
        AsyncProcess asyncProcess = new AsyncProcessImpl(retrievalCommand);
        asyncProcess.registerListeners(asyncTaskListenerList);
        return asyncProcess;
    }

    private static YoutubeDLResult fetchURL(String youtubeDlExecutable, String format, String input) throws InterruptedException, IOException, YoutubeDLException {
        String[] command = buildRetrievalCommand(youtubeDlExecutable, format, input);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        process.waitFor();
        return retrieveYoutubeDLResult(process);
    }

    public static YoutubeDLResult retrieveYoutubeDLResult(Process process) throws IOException, YoutubeDLException {
        YoutubeDLProcessOutputDTO dto = retrieveFromProcessOutput(process);
        String result = dto.result;
        String error = dto.error;

        if(result != null) {
            String[] resultArray = result.split("\n");
            YoutubeDLResult youtubeDLResult = processResultArrayForSingleResult(resultArray, error);
            return youtubeDLResult;
        }
        return new YoutubeDLResult(null, null, null, error);
    }

    public static YoutubeDLResult processResultArrayForSingleResult(String[] resultArray, String error) {
        if(resultArray != null && resultArray.length >= 3) {
            String title = retrieveTitle(resultArray);
            String url = retrieveURL(resultArray);
            String filename = retrieveFilename(resultArray);
            return new YoutubeDLResult(title, url, filename, error);
        }
        return null;
    }

    private static YoutubeDLProcessOutputDTO retrieveFromProcessOutput(Process process) throws IOException, YoutubeDLException {
        String result = retrieveOutput(process);
        String error = retrieveErrorOutput(process);

        if(process.exitValue() != 0) {
            throw new YoutubeDLException("Failed to retrieve media. Exit value returned : " +  process.exitValue());
        }
        return new YoutubeDLProcessOutputDTO(result, error);
    }

    public static String normalizeWatchURL(String url) throws URISyntaxException {
        if(isYoutubeURL(url) && isWatchURL(url)) {
            URIBuilder uriBuilder = new URIBuilder(url);

            URIBuilder uriBuilderWatch = new URIBuilder();
            String vParamValue = retrieveParam(uriBuilder.getQueryParams(), "v");
            if(vParamValue != null) {
                return uriBuilderWatch
                        .setScheme(uriBuilder.getScheme())
                        .setCharset(uriBuilder.getCharset())
                        .setHost(uriBuilder.getHost())
                        .setPort(uriBuilder.getPort())
                        .setPath(uriBuilder.getPath())
                        .setParameter(YOUTUBE_VIDEO_PARAM, vParamValue).build()
                        .toString();
            }

        }
        return null;
    }

    public static String normalizePlaylistURL(String url) throws URISyntaxException {
        if(hasPlaylist(url)) {
            URIBuilder uriBuilder = new URIBuilder(url);

            URIBuilder uriBuilderWatch = new URIBuilder();
            String vParamValue = retrieveParam(uriBuilder.getQueryParams(), YOUTUBE_PLAYLIST_PARAM);
            if(vParamValue != null) {
                return uriBuilderWatch
                        .setScheme(uriBuilder.getScheme())
                        .setCharset(uriBuilder.getCharset())
                        .setHost(uriBuilder.getHost())
                        .setPort(uriBuilder.getPort())
                        .setPath(YOUTUBE_PLAYLIST_PATH)
                        .setParameter(YOUTUBE_PLAYLIST_PARAM, vParamValue).build()
                        .toString();
            }
        }
        return null;
    }

    private static String retrieveParam(List<NameValuePair> queryParams, String param) {
        if(queryParams != null) {
            for(NameValuePair nameValuePair : queryParams) {
                if(StringUtils.equalsIgnoreCase(param, nameValuePair.getName())) {
                    return nameValuePair.getValue();
                }
            }
        }
        return null;
    }

    private static class YoutubeDLProcessOutputDTO {
        private final String result;
        private final String error;

        private YoutubeDLProcessOutputDTO(String result, String error) {
            this.result = result;
            this.error = error;
        }
    }

    private static String[] buildRetrievalCommand(String youtubeDlExecutable, String format, String input) {
        return new String[]{
                youtubeDlExecutable,
                "--get-title",
                "--get-url",
                "--get-filename",
                "-f",
                format,
                input
            };
    }

    private static String retrieveFilename(String[] resultArray) {
        if(resultArray != null && resultArray.length >= 2 && resultArray[2] != null) {
            return resultArray[2];
        }
        return null;
    }

    private static String retrieveURL(String[] resultArray) {
        if(resultArray != null) {
            for(String result : resultArray) {
                if(result != null && (result.startsWith("http") ||
                        result.startsWith("rtmp") ||
                        result.startsWith("ftp"))) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String retrieveTitle(String[] resultArray) {
        if(resultArray != null && resultArray.length >= 1) {
            return resultArray[0];
        }
        return null;
    }

    private static String retrieveErrorOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                process.getErrorStream()));
        return doRetrieve(reader);
    }

    private static String doRetrieve(BufferedReader reader) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        int count = 0;
        while ((line = reader.readLine ()) != null) {
            if(++count > 1) {
                stringBuffer.append("\n");
            }
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }

    private static String retrieveOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        return doRetrieve(reader);
    }
}
