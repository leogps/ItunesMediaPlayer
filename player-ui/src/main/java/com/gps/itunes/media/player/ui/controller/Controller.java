/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.controller;

import com.gps.imp.utils.Constants;
import com.gps.itunes.lib.exceptions.LibraryParseException;
import com.gps.itunes.lib.exceptions.NoChildrenException;
import com.gps.itunes.lib.items.tracks.Track;
import com.gps.itunes.lib.parser.ItunesLibraryParsedData;
import com.gps.itunes.lib.parser.utils.PropertyManager;
import com.gps.itunes.lib.tasks.LibraryParser;
import com.gps.itunes.media.player.ui.LibraryFileBrowser;
import com.gps.itunes.media.player.ui.Main;
import com.gps.itunes.media.player.ui.UIFrame;
import com.gps.itunes.media.player.ui.components.CopyInformationTrackerFrame;
import com.gps.itunes.media.player.ui.components.CopyPlaylistConfirmDialog;
import com.gps.itunes.media.player.ui.exceptions.TaskExecutionException;
import com.gps.imp.utils.ui.fileutils.FileBrowserDialogListener;
import com.gps.itunes.media.player.ui.tablehelpers.models.TracksTableModel;
import com.gps.itunes.media.player.ui.tasks.*;
import com.gps.itunes.media.player.vlcj.player.ItunesMediaPlayer;
import com.gps.itunes.media.player.vlcj.ui.player.events.PlayerControlEventListener;
import com.gps.itunes.media.player.vlcj.ui.player.events.PlayerMediaFilesDroppedEventListener;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * Controls the workflow after the UI initialization is complete.
 *
 * @author leogps
 */
public class Controller {

    private static org.apache.log4j.Logger log =
            org.apache.log4j.Logger.getLogger(Controller.class);
    private final UIFrame uiFrame;
    private LibraryParser parser;
    private ItunesLibraryParsedData itunesLibraryParsedData;
    
    private PlayerControlEventListener listener;

    public Controller(final UIFrame uiFrame) throws NoChildrenException, TaskExecutionException {
        this.uiFrame = uiFrame;
    }

    /**
     *
     * After UI init is complete, control is transferred to this method toe
     * further initialize content such as reading the library file.
     *
     * @throws NoChildrenException
     * @throws TaskExecutionException
     */
    public void takeControl() throws NoChildrenException, TaskExecutionException, IOException {
        takeControl(null);
    }

    /**
     *
     * After UI init is complete, control is transferred to this method toe
     * further initialize content such as reading the library file.
     *
     * @throws NoChildrenException
     * @throws TaskExecutionException
     */
    public void takeControl(String libraryFileLocation) throws NoChildrenException, TaskExecutionException, IOException {
        doLoadLibrary(libraryFileLocation);
    }

    /**
     *
     * Reloads the library. Prompts the user to provide a iTunes library file if
     * there isn't a library at the default location provided inside the
     * ui.properties file
     *
     * @throws NoChildrenException
     * @throws TaskExecutionException
     */
    public void reloadLibrary() throws NoChildrenException, TaskExecutionException {
        if(getPlayer() != null) {
            try {
                getPlayer().stopPlay();
            } catch(Exception e) {
                log.error("Could not stop playing!", e);
            }
        }
        letUserSpecifyLibrary();
    }

    private void doLoadLibrary(final String libFileLocation) throws NoChildrenException, TaskExecutionException {

        if (libFileLocation != null
                && !libFileLocation.equals(Constants.EMPTY)) {
            try {
                log.info("Loading library file: " + libFileLocation);
                parser = new LibraryParser();
                parser.addParseConfiguration(
                        PropertyManager.Property.LIBRARY_FILE_LOCATION_PROPERTY.getKey(),
                        libFileLocation);
                loadData();
            } catch (LibraryParseException lpe) {
                log.error("Specified file is not a valid Itunes library file.", lpe);
                log.info("Specified file is not a valid Itunes library file.");
                JOptionPane.showMessageDialog(uiFrame, "Specified file is not a valid Itunes library file.");

            }
        } else {
            try {
                log.info("Loading library...");
                parser = new LibraryParser();
                loadData();
                log.info("Playlists loaded: " + itunesLibraryParsedData.getAllPlaylists().length);
            } catch (LibraryParseException lpe) {
                log.debug(lpe.getMessage(), lpe);
                if (lpe.isLibraryFileNotFound()) {
                    log.info("ITunes Library file not found. Reload manually by going to Library > Reload.");
                    if(promptLibraryFileBrowse()) {
                        letUserSpecifyLibrary();
                    }
                }
            }
        }

    }

    private boolean promptLibraryFileBrowse() {
        String promptLibraryFileBrowseString = PropertyManager.getConfigurationMap().get("prompt.library.file.browse");
        return StringUtils.isNotBlank(promptLibraryFileBrowseString) && Boolean.valueOf(promptLibraryFileBrowseString);
    }

    private void letUserSpecifyLibrary() throws NoChildrenException, TaskExecutionException {
        log.debug("Let user specifiy Library file.");

        LibraryFileBrowser.browseLibraryFileShowBrowser(uiFrame, new FileBrowserDialogListener() {

            @Override
            public void onFileSelected(File file) {
                try {
                    doLoadLibrary(file.getAbsolutePath());
                } catch (NoChildrenException e) {
                    log.error(e);
                } catch (TaskExecutionException e) {
                    log.error(e);
                }
            }

            @Override
            public void onCancel() {
                log.info("Selecting the library file cancelled.");
            }
        });
    }

    /**
     *
     * Loads the playlists.
     *
     * @throws NoChildrenException
     * @throws TaskExecutionException
     */
    public void loadData() throws NoChildrenException, TaskExecutionException, LibraryParseException {

        itunesLibraryParsedData = parser.parse();

        uiFrame.getPlaylistTable().clearSelection();

        ((TracksTableModel) (uiFrame.getTracksTable().getModel())).clearTable(uiFrame.getTracksTable());


        final PlaylistLoader playlistLoader = new PlaylistLoader(itunesLibraryParsedData, uiFrame.getProgressBar(),
                uiFrame.getPlaylistTable());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    playlistLoader.submitTask(new TaskParams());
                } catch (TaskExecutionException e) {
                    log.error(e);
                }
                uiFrame.getPlaylistTable().repaint();
            }
        });

    }

    /**
     *
     * Loads the tracks of the selected playlists
     *
     * @throws TaskExecutionException
     */
    public void loadTracks() throws TaskExecutionException {
        
        final TracksLoader tracksLoader = new TracksLoader(itunesLibraryParsedData, uiFrame.getProgressBar(), uiFrame.getTracksTable(),
                uiFrame.getPlaylistTable(), uiFrame.getUiMenuBar().getCopyPlaylistsMenuItem(), uiFrame.getTracksTableHeadingLabel());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!MajorTaskInfo.isMajorTaskInProgress()) {
                    try {
                        tracksLoader.submitTask(new TaskParams());
                    } catch (TaskExecutionException e) {
                        log.error(e);
                    }
                } else {
                    tracksLoader.runTask(new TaskParams());
                }
            }
        });

    }

    /**
     * Copies the selected playlists to a destination.
     *
     * @throws TaskExecutionException
     */
    public void copyPlaylists() throws TaskExecutionException {

        final String copyDestFolder = LibraryFileBrowser.selectCopyPlaylistFolder(uiFrame);

        if (copyDestFolder != null) {

            CopyPlaylistConfirmDialog confirmDialog = new CopyPlaylistConfirmDialog();
            confirmDialog.addResponseHandler(new CopyPlaylistConfirmDialog.ResponseHanlder() {
                @Override
                public void onResponseRecieved(boolean cancelled, boolean analyzeDuplicates) {
                    if (!cancelled) {

                        MajorTaskInfo.setMajorTaskInfo(true);

                        log.info("Copying " + uiFrame.getTracksTable().getRowCount() + " track(s) in "
                                + uiFrame.getPlaylistTable().getSelectedRows().length + " playlist(s)...");

                        final CopyTaskParams params = new CopyTaskParams();
                        params.setProgress(0);
                        params.setCopyDestFolder(copyDestFolder);

                        CopyInformationTrackerFrame copyInformationTrackerFrame =
                                new CopyInformationTrackerFrame(new PlaylistCopier(parser, itunesLibraryParsedData, uiFrame.getPlaylistTable(),
                                        uiFrame.getProgressBar(), analyzeDuplicates));

                        copyInformationTrackerFrame.begin(params);

                    }
                }
            });
            confirmDialog.ask();


        } else {
            log.info("Copying cancelled.");
        }

    }

    /**
     * 
     * Searches the tracks with the query provided.
     * 
     * @param searchQuery 
     */
    public void searchTracks(final String searchQuery) throws TaskExecutionException {
        final TracksLoader tracksLoader = new TracksLoader(itunesLibraryParsedData, uiFrame.getProgressBar(), uiFrame.getTracksTable(),
                uiFrame.getPlaylistTable(), uiFrame.getUiMenuBar().getCopyPlaylistsMenuItem(), uiFrame.getTracksTableHeadingLabel(), searchQuery);
        
        if (!MajorTaskInfo.isMajorTaskInProgress()) {
            tracksLoader.submitTask(new TaskParams());
        } else {
            tracksLoader.runTask(new TaskParams());
        }
    }
    
    public void playTracks(final List<Track> trackList) {
        synchronized(this){
            final ItunesMediaPlayer player = getPlayer();
            player.play(trackList);
        }
    }
    
    public ItunesMediaPlayer getPlayer() {
        return Main.getItunesMediaPlayer();
    }

    public void registerPlayerEventListener() {

        getPlayer().registerPlayerControlEventListener(new PlayerControlEventListener() {

            @Override
            public void playClicked() {
                if(!getPlayer().isPlaying()){
                    final List<Track> trackList = uiFrame.getSelectedTracks();
                    playTracks(trackList);
                } else {
                    getPlayer().pause();
                }
            }

            @Override
            public void forwardClicked() {
                getPlayer().next();
            }

            @Override
            public void previousClicked() {
                getPlayer().previous();
            }

            @Override
            public void nowPlayingListToggled() {
                getPlayer().toggleNowPlayingList();
            }
        });

        getPlayer().registerDragAndDropEventListener(new PlayerMediaFilesDroppedEventListener() {
            @Override
            public void onFilesDroppedEvent(List<File> fileList, DropTargetDropEvent dropTargetDropEvent) {
                getPlayer().playFiles(fileList);
            }

            @Override
            public void onException(Exception e, DropTargetDropEvent dropTargetDropEvent) {
                JOptionPane.showMessageDialog(uiFrame, "Action could not be performed at the moment, please see logs for more info.");
            }
        });
    }

    public void handleFileOpenRequest() {
        getPlayer().handleFileOpenEvent();
    }

    public void handleNetworkFileOpenRequest() {
        getPlayer().handleNetworkFileOpenEvent();
    }
}
