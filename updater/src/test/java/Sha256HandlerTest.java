import com.gps.itunes.media.player.updater.checksum.Sha256Handler;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Sha256HandlerTest {

    @Test
    public void testHandler() throws NoSuchAlgorithmException, IOException {
        Sha256Handler handler = new Sha256Handler();
        Assert.assertTrue(handler.canHandle("SHA2-256SUMS"));
        File testFile = new File(Objects.requireNonNull(MD5HandlerTest.class.getResource("checksum_test_file")).getFile());
        String checksum = handler.calculateChecksum(testFile);
        Assert.assertEquals("02791abd91e43ddff141b262bad736036c7878e7207df436cac2ebb9edcb9dd5", checksum);
    }
}
