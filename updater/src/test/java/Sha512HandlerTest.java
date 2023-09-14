import com.gps.itunes.media.player.updater.checksum.Sha256Handler;
import com.gps.itunes.media.player.updater.checksum.Sha512Handler;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Sha512HandlerTest {

    @Test
    public void testHandler() throws NoSuchAlgorithmException, IOException {
        Sha512Handler handler = new Sha512Handler();
        Assert.assertTrue(handler.canHandle("SHA2-512SUMS"));
        File testFile = new File(Objects.requireNonNull(MD5HandlerTest.class.getResource("checksum_test_file")).getFile());
        String checksum = handler.calculateChecksum(testFile);
        Assert.assertEquals("86cec549dafcffd2f3e5faa6d62e24e0420002bafe877fbc1d801508f76bc62d84950469623b0944e13d073ee58e46a7e6ea5f01db1b51a6309e4a0cad762f53", checksum);
    }
}
