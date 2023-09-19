package com.gps.itunes.media.player.vlcj;

import com.gps.itunes.lib.parser.utils.PropertyManager;
import com.gps.itunes.media.player.vlcj.discovery.NativeDiscoveryStrategyResolver;
import com.gps.itunes.media.player.vlcj.discovery.provider.CustomVlcDirectoryProvider;

import java.util.logging.Level;
import java.util.logging.Logger;
import uk.co.caprica.vlcj.binding.lib.LibVlc;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

import java.io.File;

public class VLCJUtils {

    private static boolean vlcInitSucceeded = false;

    private static final Logger LOGGER = Logger.getLogger(VLCJUtils.class.getName());

    static {
        String path = new File("").getAbsolutePath()
                + PropertyManager.getConfigurationMap().get("vlc-intel-64");

        String pluginsPath = new File("").getAbsolutePath()
                + PropertyManager.getConfigurationMap().get("vlc-intel-64-plugins");

        try {
            System.setProperty("jna.debug_load", "true");

            CustomVlcDirectoryProvider.addToCache(path, pluginsPath);
            vlcInitSucceeded = new NativeDiscovery(NativeDiscoveryStrategyResolver.resolve())
                    .discover();

            LOGGER.info("vlc native library path set to:" + path);

            if (LibVlc.libvlc_get_version() != null) {
                vlcInitSucceeded = true;
            }
        } catch (UnsatisfiedLinkError ex) {
            LOGGER.log(Level.WARNING, "Failed to link binaries.", ex);
            triggerNativeDiscovery(ex);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to link binaries.", ex);
            triggerNativeDiscovery(ex);
        } finally {
            if(vlcInitSucceeded) {
                LOGGER.info(String.format("VLC Engine version %s", getVlcVersion()));
                LOGGER.info(String.format("VLCJ version %s", getVlcJVersion()));
            }
        }

    }

    private static void triggerNativeDiscovery(Throwable ex) {
        LOGGER.log(Level.WARNING, "Failed to load VLC from provided path.", ex);
        LOGGER.info("Switching to Automatic Discovery.");
        vlcInitSucceeded = new NativeDiscovery().discover();
        if(vlcInitSucceeded) {
            LOGGER.info("Auto discovery of VLC libraries succeeded.");
        }
    }

    public static boolean isVlcInitSucceeded() {
        return vlcInitSucceeded;
    }

    public static String getVlcVersion() {
        return LibVlc.libvlc_get_version();
    }

    public static String getVlcJVersion() {
        return "4.7.1";
    }
}
