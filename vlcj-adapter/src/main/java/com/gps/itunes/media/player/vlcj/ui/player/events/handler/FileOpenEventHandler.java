package com.gps.itunes.media.player.vlcj.ui.player.events.handler;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;

/**
 * Created by leogps on 11/20/14.
 */
public class FileOpenEventHandler {

    private final JFileChooser fileChooser = new JFileChooser();
    private final File userDir = new File(System.getProperty("user.home"));

    private static final Logger LOGGER = Logger.getLogger(FileOpenEventHandler.class);

    public File handle() {

        fileChooser.setCurrentDirectory(userDir);

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            LOGGER.debug("Selected file: " + selectedFile);

            return selectedFile;
        }
        return null;
    }

}
