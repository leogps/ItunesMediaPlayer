package com.gps.itunes.media.player;

import com.gps.itunes.media.player.ui.Main;
import org.apache.log4j.Logger;

import java.awt.desktop.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leogps on 7/29/17.
 */
public class OSXUtils {

    private static Logger LOG = Logger.getLogger(OSXUtils.class);

    static {
        new NativeAppUtils();
    }

    public static class NativeAppUtils implements OpenFilesHandler, AboutHandler, PreferencesHandler,
            OpenURIHandler, QuitHandler {

        private NativeAppUtils(){
            try {
                Object instance =
                        Class.forName("com.apple.eawt.Application").getMethod("getApplication").invoke(null);
                Class.forName("com.apple.eawt.Application").getMethod("setAboutHandler", AboutHandler.class).invoke(instance, this);
                Class.forName("com.apple.eawt.Application").getMethod("setOpenFileHandler", OpenFilesHandler.class).invoke(instance, this);
                Class.forName("com.apple.eawt.Application").getMethod("setOpenURIHandler", OpenURIHandler.class).invoke(instance, this);
                Class.forName("com.apple.eawt.Application").getMethod("setPreferencesHandler", PreferencesHandler.class).invoke(instance, this);
                Class.forName("com.apple.eawt.Application").getMethod("setQuitHandler", QuitHandler.class).invoke(instance, this);
                
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        @Override
        public void handleAbout(AboutEvent aboutEvent) {
            LOG.debug("TODO: About Event...");
        }

        @Override
        public void openFiles(OpenFilesEvent openFilesEvent) {
            System.out.println("Open File event triggered...");
            LOG.debug("Open File event triggered...");
            LOG.debug(openFilesEvent.getSource());
            LOG.debug(openFilesEvent.getSearchTerm());
            LOG.debug(openFilesEvent.getFiles());
            LOG.debug(openFilesEvent.getSource().getClass());

            Main.getItunesMediaPlayer().playFiles(openFilesEvent.getFiles());
        }

        @Override
        public void openURI(OpenURIEvent openURIEvent) {
            System.out.println("Open File event triggered...");
            LOG.debug("Open File event triggered...");
            LOG.debug(openURIEvent.getSource());
            LOG.debug(openURIEvent.getSource().getClass());

            File uriFile = new File(openURIEvent.getURI());
            List<File> fileList = new ArrayList<File>();
            fileList.add(uriFile);

            Main.getItunesMediaPlayer().playFiles(fileList);
        }

        @Override
        public void handlePreferences(PreferencesEvent preferencesEvent) {
            LOG.debug("Handle Preferences...");
        }

        @Override
        public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
            LOG.debug("Handle Quit...");
        }
    }
}
