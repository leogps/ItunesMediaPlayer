package com.gps.itunes.media.player;

import com.gps.itunes.media.player.ui.Main;
import org.apache.log4j.Logger;

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

    public static class NativeAppUtils implements com.apple.eawt.OpenFilesHandler, com.apple.eawt.AboutHandler, com.apple.eawt.PreferencesHandler,
            com.apple.eawt.OpenURIHandler {

        private NativeAppUtils(){
            com.apple.eawt.Application.getApplication().setAboutHandler(this);
            com.apple.eawt.Application.getApplication().setOpenFileHandler(this);
            com.apple.eawt.Application.getApplication().setOpenURIHandler(this);
            com.apple.eawt.Application.getApplication().setPreferencesHandler(this);
        }

        @Override
        public void handleAbout(com.apple.eawt.AppEvent.AboutEvent aboutEvent) {
            LOG.debug("TODO: About Event...");
        }

        @Override
        public void openFiles(com.apple.eawt.AppEvent.OpenFilesEvent openFilesEvent) {
            System.out.println("Open File event triggered...");
            LOG.debug("Open File event triggered...");
            LOG.debug(openFilesEvent.getSource());
            LOG.debug(openFilesEvent.getSearchTerm());
            LOG.debug(openFilesEvent.getFiles());
            LOG.debug(openFilesEvent.getSource().getClass());
            
            Main.getItunesMediaPlayer().playFiles(openFilesEvent.getFiles());
        }

        @Override
        public void openURI(com.apple.eawt.AppEvent.OpenURIEvent openURIEvent) {
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
        public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent preferencesEvent) {
            LOG.debug("Handle Preferences...");
        }

//        @Override
//        public void handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent quitEvent, com.apple.eawt.QuitResponse quitResponse) {
//
//        }
    }
}
