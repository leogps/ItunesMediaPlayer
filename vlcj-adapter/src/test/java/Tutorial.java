/**
 * Created by leogps on 3/27/18.
 */
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.testng.annotations.Test;
import uk.co.caprica.vlcj.binding.lib.LibVlc;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;

public class Tutorial {

    private static final String NATIVE_LIBRARY_SEARCH_PATH = "/Users/leogps/Documents/Working_Projects/ILP/Projects/ItunesMediaPlayer/LibVLC/macOs/intel-64_3.0.1/lib";
    private static final String PLUGINS_PATH = "/Users/leogps/Documents/Working_Projects/ILP/Projects/ItunesMediaPlayer/LibVLC/macOs/intel-64_3.0.1/plugins";

    @Test
    public void test() {
        uk.co.caprica.vlcj.binding.lib.LibC.INSTANCE.setenv("VLC_PLUGIN_PATH", PLUGINS_PATH, 1);
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
        LibVlc libVlc = (LibVlc) Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        System.out.println(libVlc.libvlc_get_version());
    }
}