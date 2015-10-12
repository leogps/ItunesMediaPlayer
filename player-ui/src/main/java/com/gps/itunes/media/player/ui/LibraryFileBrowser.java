/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui;

import com.gps.itunes.media.player.ui.fileutils.FileBrowserDialog;
import com.gps.itunes.media.player.ui.fileutils.FileBrowserDialogListener;

import java.awt.Component;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * Brings up a browser window for the user to browse.
 * 
 * @author leogps
 */
public class LibraryFileBrowser {

    private static final String BROWSE_LIB_TITLE = "Please select the file (Itunes Music Library.xml)";
    private static final String COPY_PLIST_TITLE = "Please select a folder to copy the playlist";
    
    private static final String[] BROWSE_FILTER = {"XML file", "xml"};

    /**
     * 
     * Lets the user browse for an XML file which is expected to be a proper iTunes library file.
     * 
     * @param uiFrame
     * @return 
     */
    public static String browseLibraryFile(final Component uiFrame) {
        final JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        final FileNameExtensionFilter filter = new FileNameExtensionFilter(
                BROWSE_FILTER[0], BROWSE_FILTER[1]);
        fileChooser.setFileFilter(filter);

        fileChooser.setDialogTitle(BROWSE_LIB_TITLE);


        if (fileChooser.showOpenDialog(uiFrame) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    public static void browseLibraryFileShowBrowser(final JFrame parent,
                                                    FileBrowserDialogListener fileBrowserDialogListener) {
        FileBrowserDialog dialog = new FileBrowserDialog(parent, "Select library file...", null);
        dialog.registerFileBrowserDialogListener(fileBrowserDialogListener);
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * 
     * Lets the user browse and choose a directory as a destination for copying the playlists.
     * 
     * @param uiFrame
     * @return 
     */
    public static String selectCopyPlaylistFolder(final Component uiFrame) {
        final JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(COPY_PLIST_TITLE);

        fileChooser.setAcceptAllFileFilterUsed(false);
        
        if(fileChooser.showOpenDialog(uiFrame) == JFileChooser.APPROVE_OPTION){
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }
}
