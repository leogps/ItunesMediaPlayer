package com.gps.itunes.media.player.vlcj;

import com.gps.itunes.lib.parser.utils.OSInfo;
import com.gps.itunes.lib.parser.utils.PropertyManager;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.apache.log4j.Logger;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.File;

public class VLCJUtils {

    private static boolean vlcInitSucceeded = false;

    private static Logger log = Logger.getLogger(VLCJUtils.class);

    static {
        //TODO: Use Bitness-checker to determine underlying OS architecture.
        // FIXME: Below will give JRE architecture not OS architecture.
        String path = new File("").getAbsolutePath() + (OSInfo.isArch64()
                ? PropertyManager.getProperties().getProperty("vlc-intel-64")
                : PropertyManager.getProperties().getProperty("vlc-intel-32"));

        String pluginsPath = new File("").getAbsolutePath() + (OSInfo.isArch64()
                ? PropertyManager.getProperties().getProperty("vlc-intel-64-plugins")
                : PropertyManager.getProperties().getProperty("vlc-intel-32-plugins"));

        uk.co.caprica.vlcj.binding.LibC.INSTANCE.setenv("VLC_PLUGIN_PATH", pluginsPath, 1);
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), path);
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        log.debug("vlc native library path set to:" + path);

        try {
            if (LibVlc.INSTANCE != null) {
                vlcInitSucceeded = true;
            }
        } catch (Exception ex) {
            log.error(ex);
            log.debug("Switching to Automatic Discovery.");
            vlcInitSucceeded = new NativeDiscovery().discover();
            if(vlcInitSucceeded) {
                log.debug("Auto discovery of VLC libraries succeeded.");
            }
        } finally {
            if(vlcInitSucceeded) {
                log.debug(String.format("VLC Engine version %s", getVlcVersion()));
            }
        }

    }

    public static boolean isVlcInitSucceeded() {
        return vlcInitSucceeded;
    }

    public static String getVlcVersion() {
        return LibVlc.INSTANCE.libvlc_get_version();
    }
}
