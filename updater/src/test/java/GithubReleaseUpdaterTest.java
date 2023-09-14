import com.fasterxml.jackson.databind.ObjectMapper;
import com.gps.itunes.media.player.updater.GithubReleaseUpdater;
import com.gps.itunes.media.player.updater.checksum.ChecksumHandler;
import com.gps.itunes.media.player.updater.github.Release;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by leogps on 2/25/17.
 */
public class GithubReleaseUpdaterTest {

    public Release testGithubReleaseJsonMapper() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = GithubReleaseUpdater.getContent("https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest");
        Release release =
                objectMapper.readValue(content, Release.class);

        Assert.assertNotNull(release);
        Assert.assertNotNull(release.getName());

        Assert.assertNotNull(release.getAssets());
        Assert.assertTrue(release.getAssets().length > 0);
        return release;
    }

    @Test
    public void testChecksumHandlersLoad() {
        final GithubReleaseUpdater updater = new GithubReleaseUpdater();
        List<ChecksumHandler> list = updater.getChecksumHandlers();
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testGithubReleaseFileReplace() throws Exception {
        Release release = testGithubReleaseJsonMapper();

        GithubReleaseUpdater githubReleaseUpdater = new GithubReleaseUpdater();

        String assetUrl = githubReleaseUpdater.resolveAssetURL("yt-dlp_macos", release);
        githubReleaseUpdater.replace("target/youtube-dl", assetUrl);
    }
}
