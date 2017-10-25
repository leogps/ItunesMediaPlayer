package com.gps.youtube.dl.test;

import com.gps.youtube.dl.YoutubeDL;
import com.gps.youtube.dl.YoutubeDLResult;
import com.gps.youtube.dl.event.YoutubeDLResultEvent;
import com.gps.youtube.dl.event.YoutubeDLResultEventListener;
import com.gps.youtube.dl.exception.YoutubeDLException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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

    @Test
    public void testFetchPlaylist() throws InterruptedException, YoutubeDLException, IOException {
        //String playlist = "https://www.youtube.com/playlist?list=RDGMEMQ1dJ7wXfLlqCjwV0xfSNbAVMuMTGcwZxdGg";
        String playlist = "https://www.youtube.com/playlist?list=PLJiVGeJ_KPz7mC8JapyXEJDKInyYIBx7C";
        YoutubeDL.fetchPlaylist(new File("../youtube-dl-exec/youtube-dl").getAbsolutePath(),
                playlist, new YoutubeDLResultEventListener() {

                    @Override
                    public void onYoutubeDLResultEvent(YoutubeDLResultEvent youtubeDLResultEvent) {
                        YoutubeDLResult youtubeDLResult = youtubeDLResultEvent.getYoutubeDLResult();
                        Assert.assertNotNull(youtubeDLResult);
                        System.out.println(youtubeDLResult);
                    }
                });
    }

    @Test
    public void testHasPlaylist() throws URISyntaxException {
        String playlist = "https://www.youtube.com/watch?v=NuF14CgXW5Q&list=PLJiVGeJ_KPz4Yv88aWtJSZxucpnSe7dim";
        boolean hasPlaylist = YoutubeDL.hasPlaylist(playlist);
        Assert.assertTrue(hasPlaylist);
    }

    @Test
    public void testHasPlaylistNull() throws URISyntaxException {
        String playlist = null;
        boolean hasPlaylist = YoutubeDL.hasPlaylist(playlist);
        Assert.assertFalse(hasPlaylist);
    }

    @Test
    public void testHasPlaylistNegative() throws URISyntaxException {
        String playlist = "https://www.youtube.com/watch?v=NuF14CgXW5Q";
        boolean hasPlaylist = YoutubeDL.hasPlaylist(playlist);
        Assert.assertFalse(hasPlaylist);
    }

    @Test
    public void testIsWatchURL() throws URISyntaxException {
        String playlist = "https://www.youtube.com/watch?v=NuF14CgXW5Q";
        boolean isWatchURL = YoutubeDL.isWatchURL(playlist);
        Assert.assertTrue(isWatchURL);
    }

    @Test
    public void testIsWatchURLNegative() throws URISyntaxException {
        String playlist = "https://www.youtube.com/playlist?list=PLJiVGeJ_KPz7mC8JapyXEJDKInyYIBx7C";
        boolean isWatchURL = YoutubeDL.isWatchURL(playlist);
        Assert.assertFalse(isWatchURL);
    }

    @Test
    public void testNormalizeWatchURL() throws URISyntaxException {
        String watch = "https://www.youtube.com/watch?v=NuF14CgXW5Q";
        String watchURL = YoutubeDL.normalizeWatchURL(watch);
        Assert.assertEquals(watchURL, watch);
    }

    @Test
    public void testNormalizeWatchURLFromPlaylist() throws URISyntaxException {
        String watch = "https://www.youtube.com/watch?v=NuF14CgXW5Q&list=PLJiVGeJ_KPz4Yv88aWtJSZxucpnSe7dim";
        String watchURL = YoutubeDL.normalizeWatchURL(watch);
        Assert.assertEquals(watchURL, "https://www.youtube.com/watch?v=NuF14CgXW5Q");
    }

    @Test
    public void testNormalizeWatchURLFromGarbage() throws URISyntaxException {
        String watch = "https://www.youtube.com/watch?v=NuF14CgXW5Q&blahblah=kakaka&kyle=1234";
        String watchURL = YoutubeDL.normalizeWatchURL(watch);
        Assert.assertEquals(watchURL, "https://www.youtube.com/watch?v=NuF14CgXW5Q");
    }

    @Test
    public void testNormalizePlaylistURL() throws URISyntaxException {
        String watch = "https://www.youtube.com/watch?v=NuF14CgXW5Q&list=PLJiVGeJ_KPz4Yv88aWtJSZxucpnSe7dim";
        String playlistURL = YoutubeDL.normalizePlaylistURL(watch);
        Assert.assertEquals(playlistURL, "https://www.youtube.com/playlist?list=PLJiVGeJ_KPz4Yv88aWtJSZxucpnSe7dim");
    }

    @Test
    public void testNormalizePlaylistURLNoChange() throws URISyntaxException {
        String playlist = "https://www.youtube.com/playlist?list=PLJiVGeJ_KPz4Yv88aWtJSZxucpnSe7dim";
        String playlistURL = YoutubeDL.normalizePlaylistURL(playlist);
        Assert.assertEquals(playlistURL, "https://www.youtube.com/playlist?list=PLJiVGeJ_KPz4Yv88aWtJSZxucpnSe7dim");
    }

}
