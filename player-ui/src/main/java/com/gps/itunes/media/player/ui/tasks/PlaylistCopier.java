/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tasks;

import com.gps.itunes.lib.exceptions.FileCopyException;
import com.gps.itunes.lib.exceptions.InvalidPlaylistException;
import com.gps.itunes.lib.items.playlists.Playlist;
import com.gps.itunes.lib.tasks.progressinfo.CopyTrackFailureInformation;
import com.gps.itunes.lib.tasks.progressinfo.ProgressTracker;
import com.gps.itunes.media.player.ui.controller.MajorTaskInfo;
import com.gps.itunes.media.player.ui.exceptions.TaskExecutionException;
import com.gps.itunes.media.player.ui.handlers.ProgressHandler;
import com.gps.itunes.media.player.dto.PlaylistHolder;
import com.gps.itunes.lib.tasks.LibraryParser;
import com.gps.itunes.lib.tasks.ProgressInformer;
import com.gps.itunes.lib.tasks.progressinfo.CopyTrackInformation;
import com.gps.itunes.lib.tasks.progressinfo.ProgressInformation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 *
 * Copies the selected playlists to a destination. Prompts the user to provide a
 * destination.
 *
 * @author leogps
 */
public class PlaylistCopier extends ProgressHandler {

    private final JTable playlistTable;
    private final LibraryParser parser;
    private static org.apache.log4j.Logger log =
            org.apache.log4j.Logger.getLogger(PlaylistCopier.class);

    private final boolean analyzeDuplicates;
    
    private static String currentCopyingPlaylist = "";

    private final List<ProgressTracker> progressTrackerList = new ArrayList<ProgressTracker>();

    public PlaylistCopier(final LibraryParser parser,
            final JTable playlistTable, final JProgressBar progressBar, boolean analyzeDuplicates) {
        super(progressBar, TaskType.MAJOR_TASK);
        this.parser = parser;
        this.playlistTable = playlistTable;
        this.analyzeDuplicates = analyzeDuplicates;
    }

    @Override
    public void runTask(final TaskParams params) {
        try {
            MajorTaskInfo.setMajorTaskInfo(true);
            copyPlaylists(params);
            MajorTaskInfo.setMajorTaskInfo(false);
            log.info("Playlist copied.");
        } catch (TaskExecutionException ex) {
            log.error(ex);
        }
    }

    private void copyPlaylists(final TaskParams params) throws TaskExecutionException {

        final CopyTaskParams cpParams = (CopyTaskParams) params;

        for (final int selectedRow : playlistTable.getSelectedRows()) {

            final Playlist playlist = ((PlaylistHolder) playlistTable.getValueAt(selectedRow, 0)).getPlaylist();

            try {
                log.info("Copying playlist " + playlist.getName() + " now.");
                
                currentCopyingPlaylist = playlist.getName();

                ProgressTracker progressTracker = new ProgressTracker(new CopyProgressInformer(), new CopyProgressInformation());
                List<ProgressTracker> currentProgressTrackerList = new ArrayList<ProgressTracker>();
                currentProgressTrackerList.add(progressTracker);
                currentProgressTrackerList.addAll(progressTrackerList);

                try {
                    parser.copyPlaylists(playlist.getPlaylistId(), cpParams.getCopyDestFolder(),
                            currentProgressTrackerList, analyzeDuplicates);
                } catch (InvalidPlaylistException e) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid playlist found: " + e.getLocalizedMessage(),
                            "Error Occurred!",
                            JOptionPane.ERROR_MESSAGE);
                }

                setProgressMsg("Playlist copied.");

            } catch (IOException ioe) {
                //throw new TaskExecutionException(this, ioe);
                JOptionPane.showMessageDialog(null,
                        "Could not copy playlist: " + ioe.getLocalizedMessage(),
                        "Error Occurred!",
                        JOptionPane.ERROR_MESSAGE);
            } catch (FileCopyException e) {
                JOptionPane.showMessageDialog(null,
                        "Could not copy playlist: " + e.getLocalizedMessage(),
                        "Error Occurred!",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void addProgressTracker(ProgressTracker progressTracker) {
        progressTrackerList.add(progressTracker);
    }

    private class CopyProgressInformer<CopyProgressInformation> implements ProgressInformer<ProgressInformation<CopyTrackInformation>> {

        @Override
        public void informProgress(final ProgressInformation<CopyTrackInformation> t) {
            final CopyTrackInformation info = t.getInformation();

            if(info instanceof CopyTrackFailureInformation) {
                CopyTrackFailureInformation failureInformation = (CopyTrackFailureInformation) info;
                setProgressMsg(String.format("Error occurred when copying file %s. Reason: %s", failureInformation.getCurrentTrack(),
                                failureInformation.getFailureMessage()));
            } else {

                setProgress(info.getProgress());

                setProgressMsg("Copying " + info.getCurrentTrack() + " from the playlist '"
                        + currentCopyingPlaylist + "'...");
                if (info.getProgress() != -1) {
                    log.info("(" + info.getCurrentTrackNo() + "/" + info.getTrackCount() + ") files copied. "
                            + "Copying track: " + info.getCurrentTrack() + " to: " + info.getToDest());
                }
            }
        }
    }

    private class CopyProgressInformation implements ProgressInformation<CopyTrackInformation> {

        private CopyTrackInformation info = new CopyTrackInformation(-1, 100, 0, "", "");

        @Override
        public void setInformation(CopyTrackInformation info) {
            this.info = info;
        }

        @Override
        public CopyTrackInformation getInformation() {
            return info;
        }
    }
}
