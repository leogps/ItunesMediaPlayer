import com.fasterxml.jackson.databind.ObjectMapper;
import com.gps.imp.utils.ssl.HttpClientUtils;
import com.gps.itunes.media.player.updater.GithubReleaseUpdater;
import com.gps.itunes.media.player.updater.github.Release;
import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by leogps on 2/25/17.
 */
@Test
public class GithubReleaseUpdaterTest {

    @Test
    public Release testGithubReleaseJsonMapper() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = GithubReleaseUpdater.getContent("https://api.github.com/repos/rg3/youtube-dl/releases/latest");
        Release release =
                objectMapper.readValue(content, Release.class);

        Assert.assertNotNull(release);
        Assert.assertNotNull(release.getName());

        Assert.assertNotNull(release.getAssets());
        Assert.assertTrue(release.getAssets().length > 0);
        return release;
    }

    @Test
    public void testGithubReleaseFileReplace() throws IOException {
        Release release = testGithubReleaseJsonMapper();

        GithubReleaseUpdater githubReleaseUpdater = new GithubReleaseUpdater();

        String assetUrl = githubReleaseUpdater.resolveAssetURL("youtube-dl", release);
        githubReleaseUpdater.replace("target/youtube-dl", assetUrl);
    }

    @Test
    public void testMD5Checkum() throws IOException, NoSuchAlgorithmException {
        File file = File.createTempFile("tempFile", ".tmp");
        Assert.assertNotNull(file);

        String md5Checksum = GithubReleaseUpdater.getMD5Checksum(file);
        Assert.assertNotNull(md5Checksum);
    }

}
