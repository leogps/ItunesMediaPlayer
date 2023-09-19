import com.gps.itunes.media.player.updater.checksum.MD5Handler;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class MD5HandlerTest {

    @Test
    public void testHandler() throws NoSuchAlgorithmException, IOException {
        MD5Handler handler = new MD5Handler();
        Assert.assertTrue(handler.canHandle("MD5SUMS"));
        File testFile = new File(Objects.requireNonNull(MD5HandlerTest.class.getResource("checksum_test_file")).getFile());
        String checksum = handler.calculateChecksum(testFile);
        Assert.assertEquals("e479bd09463260ef298d7092ca6ed132", checksum);
    }
}
