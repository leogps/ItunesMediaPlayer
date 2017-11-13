package com.gps.itunes.media.player.vlcj;

import com.gps.itunes.lib.parser.utils.OSInfo;
import com.gps.itunes.lib.parser.utils.PropertyManager;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import java.util.logging.Level;
import java.util.logging.Logger;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.File;

public class VLCJUtils {

    private static boolean vlcInitSucceeded = false;

    private static Logger LOGGER = Logger.getLogger(VLCJUtils.class.getName());

    static {
        //TODO: Use Bitness-checker to determine underlying OS architecture.
        // FIXME: Below will give JRE architecture not OS architecture.
        String path = new File("").getAbsolutePath() + (OSInfo.isArch64()
                ? PropertyManager.getConfigurationMap().get("vlc-intel-64")
                : PropertyManager.getConfigurationMap().get("vlc-intel-32"));

        String pluginsPath = new File("").getAbsolutePath() + (OSInfo.isArch64()
                ? PropertyManager.getConfigurationMap().get("vlc-intel-64-plugins")
                : PropertyManager.getConfigurationMap().get("vlc-intel-32-plugins"));

        try {

            uk.co.caprica.vlcj.binding.LibC.INSTANCE.setenv("VLC_PLUGIN_PATH", pluginsPath, 1);
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), path);
            Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

            LOGGER.info("vlc native library path set to:" + path);

            if (LibVlc.INSTANCE != null) {
                vlcInitSucceeded = true;
            }
        } catch (UnsatisfiedLinkError ex) {
            triggerNativeDiscovery(ex);
        } catch (Exception ex) {
            triggerNativeDiscovery(ex);
        } finally {
            if(vlcInitSucceeded) {
                LOGGER.info(String.format("VLC Engine version %s", getVlcVersion()));
            }
        }

    }

    private static void triggerNativeDiscovery(Throwable ex) {
        LOGGER.log(Level.WARNING, "Failed to load VLC from provided path.", ex);
        LOGGER.info("Switching to Automatic Discovery.");
        vlcInitSucceeded = new NativeDiscovery().discover();
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        if(vlcInitSucceeded) {
            LOGGER.info("Auto discovery of VLC libraries succeeded.");
        }
    }

    public static boolean isVlcInitSucceeded() {
        return vlcInitSucceeded;
    }

    public static String getVlcVersion() {
        return LibVlc.INSTANCE.libvlc_get_version();
    }
}
