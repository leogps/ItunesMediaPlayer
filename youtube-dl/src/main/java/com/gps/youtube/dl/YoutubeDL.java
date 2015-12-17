package com.gps.youtube.dl;

import com.gps.youtube.dl.exception.YoutubeDLException;

import java.io.*;

/**
 * Created by leogps on 12/17/15.
 */
public class YoutubeDL {

    public static YoutubeDLResult fetchBest(String youtubeDlExecutable, String input) throws IOException, InterruptedException, YoutubeDLException {
        return fetchURL(youtubeDlExecutable, "best", input);
    }

    private static YoutubeDLResult fetchURL(String youtubeDlExecutable, String format, String input) throws InterruptedException, IOException, YoutubeDLException {
        ProcessBuilder builder = new ProcessBuilder(
                youtubeDlExecutable,
                "--get-title",
                "--get-url",
                "--get-filename",
                "-f",
                format,
                input);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        //FIXME: Timeout after sometime.
        process.waitFor();

        String result = retrieveOutput(process);
        String error = retrieveErrorOutput(process);

        if(process.exitValue() != 0) {
            throw new YoutubeDLException("Exit value returned: " +  process.exitValue());
        }

        if(result != null) {
            String[] resultArray;
            resultArray = result.split("\n");
            String title = retrieveTitle(resultArray);
            String url = retrieveURL(resultArray);
            String filename = retrieveFilename(resultArray);
            return new YoutubeDLResult(title, url, filename, error);
        }

        return new YoutubeDLResult(null, null, null, error);
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
                if(result != null && (result.startsWith("http") || result.startsWith("rtmp"))) {
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
