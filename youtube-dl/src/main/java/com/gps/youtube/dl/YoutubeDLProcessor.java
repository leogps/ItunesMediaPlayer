package com.gps.youtube.dl;

import com.gps.imp.utils.process.AsyncProcessImpl;
import com.gps.youtube.dl.event.YoutubeDLResultEvent;
import com.gps.youtube.dl.event.YoutubeDLResultEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.gps.youtube.dl.YoutubeDL.processResultArrayForSingleResult;

/**
 * Created by leogps on 10/24/2017.
 */
public class YoutubeDLProcessor extends AsyncProcessImpl {

    private InputStream inputStream;
    private InputStream errorStream;
    private static final int YOUTUBE_DL_RESULT_CHUNK = 3;
    private Thread inputStreamThread;
    private Thread errorStreamThread;
    private final List<YoutubeDLResultEventListener> youtubeDLResultEventListeners = new ArrayList<YoutubeDLResultEventListener>();

    private static final Logger LOGGER = LogManager.getLogger(YoutubeDLProcessor.class);

    public YoutubeDLProcessor(String[] command) {
        super(command);
    }

    public synchronized Process execute() throws IOException {
        if(isExecuting()) {
            throw new IllegalStateException("Previously submitted process is still executing. Only one process is permitted.");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(getCommand());
        process = processBuilder.start();
        this.inputStream = process.getInputStream();
        this.errorStream = process.getErrorStream();
        this.inputStreamThread = new Thread(getInputStreamTask());
        this.errorStreamThread = new Thread(getErrorStreamTask());

        inputStreamThread.start();
        errorStreamThread.start();
        waitForAsync();

        return process;
    }

    public Process getProcess() {
        return process;
    }

    public Runnable getInputStreamTask() {
        return new Runnable() {
            @Override
            public void run() {

                BufferedReader reader = buildReader(inputStream);
                String line;
                int counter = 0;

                String resultArray[] = new String[YOUTUBE_DL_RESULT_CHUNK];
                try {
                    while ((line = reader.readLine()) != null) {

                        ++counter;
                        int index = counter % YOUTUBE_DL_RESULT_CHUNK;
                        resultArray[index] = line;
                        if(counter % YOUTUBE_DL_RESULT_CHUNK == 0) {
                            YoutubeDLResult youtubeDLResult = processResultArrayForSingleResult(resultArray, null);
                            LOGGER.debug("Fetched Youtube Playlist URL: " + youtubeDLResult);
                            resultArray = new String[YOUTUBE_DL_RESULT_CHUNK];
                            YoutubeDLResultEvent youtubeDLResultEvent = new YoutubeDLResultEvent(youtubeDLResult);
                            informResult(youtubeDLResultEvent);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeSilently(reader, inputStream);
                }
            }
        };
    }

    private void informResult(YoutubeDLResultEvent youtubeDLResultEvent) {
        for(YoutubeDLResultEventListener youtubeDLResultEventListener : youtubeDLResultEventListeners) {
            youtubeDLResultEventListener.onYoutubeDLResultEvent(youtubeDLResultEvent);
        }
    }

    private void closeSilently(Closeable... closeables) {
        if(closeables != null && closeables.length > 0) {
            for(Closeable closeable : closeables) {
                if(closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private BufferedReader buildReader(InputStream inputStream) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
        return bufferedReader;
    }

    public Runnable getErrorStreamTask() {
        return new Runnable() {
            @Override
            public void run() {

                BufferedReader reader = buildReader(errorStream);
                String line;

                try {
                    while ((line = reader.readLine()) != null) {
                        LOGGER.warn(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeSilently(reader, errorStream);
                }
            }
        };
    }

    @Override
    public void interrupt() {
        super.interrupt();
        closeSilently(inputStream, errorStream);
    }

    public void addYoutubeDLResultEventListener(YoutubeDLResultEventListener youtubeDLResultEventListener) {
        this.youtubeDLResultEventListeners.add(youtubeDLResultEventListener);
    }
}
