package com.gps.youtube.dl.test;

import com.gps.youtube.dl.YoutubeDL;
import com.gps.youtube.dl.YoutubeDLResult;
import com.gps.youtube.dl.exception.YoutubeDLException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by leogps on 12/17/15.
 */
public class YoutubeDLTest {

    @Test
    public void testFetchBestURL() throws InterruptedException, YoutubeDLException, IOException {
        YoutubeDLResult youtubeDLResult = YoutubeDL.fetchBest(
                "/Users/leogps/Documents/Working_Projects/ILP/Projects/ItunesMediaPlayer/youtube-dl-exec/youtube-dl",
                "https://www.youtube.com/watch?v=k4YRWT_Aldo");

        Assert.assertNotNull(youtubeDLResult);
        Assert.assertNotNull(youtubeDLResult.getUrl());
        System.out.println(youtubeDLResult);
    }

}
