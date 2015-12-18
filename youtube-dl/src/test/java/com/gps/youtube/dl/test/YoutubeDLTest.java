package com.gps.youtube.dl.test;

import com.gps.youtube.dl.YoutubeDL;
import com.gps.youtube.dl.YoutubeDLResult;
import com.gps.youtube.dl.exception.YoutubeDLException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by leogps on 12/17/15.
 */
@Test
public class YoutubeDLTest {

    @Test
    public void testFetchBestURL() throws InterruptedException, YoutubeDLException, IOException {
        YoutubeDLResult youtubeDLResult = YoutubeDL.fetchBest(
                new File("../youtube-dl-exec/youtube-dl").getAbsolutePath(),
                "https://www.youtube.com/watch?v=k4YRWT_Aldo");

        Assert.assertNotNull(youtubeDLResult);
        Assert.assertNotNull(youtubeDLResult.getUrl());
        System.out.println(youtubeDLResult);
    }

}
