/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tasks;

import com.gps.imp.utils.Constants;
import com.gps.itunes.lib.items.playlists.Playlist;
import com.gps.itunes.lib.items.tracks.Track;
import com.gps.itunes.lib.parser.ItunesLibraryParsedData;
import com.gps.itunes.media.player.dto.PlaylistHolder;
import com.gps.itunes.media.player.dto.TrackHolder;
import com.gps.imp.utils.ui.LabelCell;
import com.gps.itunes.media.player.ui.Main;
import com.gps.itunes.media.player.ui.handlers.ProgressHandler;
import com.gps.itunes.media.player.ui.tablehelpers.models.TracksTableModel;
import com.gps.itunes.media.player.vlcj.player.ItunesMediaPlayer;
import com.gps.itunes.media.player.vlcj.player.events.MediaPlayerEventListener;
import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.*;

/**
 *
 * Loads the tracks for any selected playlists.
 *
 * @author leogps
 */
public class TracksLoader extends ProgressHandler {

    private static final Logger LOGGER = LogManager.getLogger(TracksLoader.class);

    private final ImageIcon currentlyPlayingIcon = new ImageIcon(TracksLoader.class.getClassLoader().getResource("images/play_20x20.png")) {
        @Override
        public String toString() {
            return "Playing";
        }
    };

    private final ImageIcon currentlyPausedIcon = new ImageIcon(TracksLoader.class.getClassLoader().getResource("images/pause_20x20.png")) {
        @Override
        public String toString() {
            return "Paused";
        }
    };

    private final JTable playlistTable;
    private final JTable tracksTable;
    private final JMenuItem copyPlaylistsMenuItem;
    private final ItunesLibraryParsedData itunesLibraryParsedData;
    private final String searchQuery;
    private final JLabel tracksTableHeadingLabel;

    private static final int TRACKS_TABLE_HEADING_LENGTH_LIMIT = 100;

    public TracksLoader(final ItunesLibraryParsedData itunesLibraryParsedData,
                        final JProgressBar progressBar,
            final JTable tracksTable, final JTable playlistTable,
            final JMenuItem copyPlaylistsMenuItem,
            final JLabel tracksTableHeadingLabel) {
        super(progressBar, TaskType.SUB_TASK);
        this.itunesLibraryParsedData = itunesLibraryParsedData;
        this.tracksTable = tracksTable;
        this.playlistTable = playlistTable;
        this.copyPlaylistsMenuItem = copyPlaylistsMenuItem;
        this.searchQuery = null;
        this.tracksTableHeadingLabel = tracksTableHeadingLabel;
    }

    public TracksLoader(final ItunesLibraryParsedData itunesLibraryParsedData, final JProgressBar progressBar,
            final JTable tracksTable, final JTable playlistTable,
            final JMenuItem copyPlaylistsMenuItem,
            final JLabel tracksTableHeadingLabel,
            final String searchQuery) {
        super(progressBar, TaskType.SUB_TASK);
        this.itunesLibraryParsedData = itunesLibraryParsedData;
        this.tracksTable = tracksTable;
        this.playlistTable = playlistTable;
        this.copyPlaylistsMenuItem = copyPlaylistsMenuItem;
        this.searchQuery = searchQuery;
        this.tracksTableHeadingLabel = tracksTableHeadingLabel;
    }

    @Override
    public void runTask(final TaskParams params) {
        try{

            final TracksTableModel model =
                    (TracksTableModel) tracksTable.getModel();

            model.clearTable(tracksTable);

            Main.getItunesMediaPlayer().addMediaPlayerListener(new MediaPlayerEventListener() {
                @Override
                public void playing(ItunesMediaPlayer player, NowPlayingListData currentTrack) {
                    updateStatusCells(true);
                }

                @Override
                public void paused(ItunesMediaPlayer player, String location) {
                    updateStatusCells(false);
                }

                private void updateStatusCells(final boolean isPlaying) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < model.getDataVector().size(); i++) {
                                Vector objectVector = (Vector) model.getDataVector().get(i);
                                TrackHolder trackHolder = (TrackHolder) objectVector.get(TracksTableModel.getHolderIndex());
                                if (Main.getItunesMediaPlayer().isCurrentTrack(trackHolder.getTrack().getTrackId())) {
                                    objectVector.set(0, (isPlaying) ? currentlyPlayingIcon : currentlyPausedIcon);
                                } else {
                                    objectVector.set(0, Constants.EMPTY);
                                }
                                model.fireTableCellUpdated(i, 0); // first column, every row
                            }
                        }
                    });
                }

                @Override
                public void stopped(ItunesMediaPlayer player, String location) {

                }

                @Override
                public void finished(ItunesMediaPlayer player, String location) {

                }

                @Override
                public void onPlayProgressed() {

                }
            });

            tracksTable.getColumnModel().getColumn(0).setCellEditor(new LabelCell());
            tracksTable.getColumnModel().getColumn(0).setCellRenderer(new LabelCell());

            final Map<Long, Track> trackMap = new HashMap<Long, Track>();

            List<Playlist> playlistList = new ArrayList<Playlist>();
            for (final int selectedRow : playlistTable.getSelectedRows()) {

                final Playlist playlist =
                        ((PlaylistHolder) playlistTable.getValueAt(selectedRow, 0)).getPlaylist();
                playlistList.add(playlist);

                final Track[] tracks =
                        itunesLibraryParsedData.getPlaylistTracks(playlist.getPlaylistId());


                for(final Track track : tracks){
                    if(searchQuery == null || Constants.EMPTY.equalsIgnoreCase(searchQuery)){
                        trackMap.put(track.getTrackId(), track);
                    } else if(match(track)){
                        trackMap.put(track.getTrackId(), track);
                    }
                }
            }

            addTracks(model, trackMap.values().toArray(new Track[trackMap.size()]));

            if(playlistTable.getSelectedRows().length > 0
                    && tracksTable.getRowCount() > 0){
                enableCopyPlaylists(true);
            } else {
                enableCopyPlaylists(false);
            }

            String tracksTableHeadingText;
            if(searchQuery == null || Constants.EMPTY.equals(searchQuery)){

                LOGGER.info(trackMap.size() + " track(s) in " + playlistTable.getSelectedRows().length + " selected playlist(s).");
                setProgressMsg("Tracks loaded.");
                tracksTableHeadingText = getTracksTableHeading(playlistList);

            } else {
                LOGGER.info(trackMap.size() + " track(s) found.");
                setProgressMsg("Tracks loaded.");
                tracksTableHeadingText = getTracksTableHeading(playlistList, searchQuery);
            }

            // Setting Track table heading. Truncating the heading based on the limit.
            if(tracksTableHeadingText.length() > TRACKS_TABLE_HEADING_LENGTH_LIMIT) {
                tracksTableHeadingLabel.setText(tracksTableHeadingText.substring(0, TRACKS_TABLE_HEADING_LENGTH_LIMIT) + "...");
                tracksTableHeadingLabel.setToolTipText(tracksTableHeadingText);
            } else {
                tracksTableHeadingLabel.setText(tracksTableHeadingText);
                tracksTableHeadingLabel.setToolTipText(tracksTableHeadingText);
            }

        } catch(Exception ex){
            LOGGER.error("Exception occurred when loading tracks...", ex);
        }

    }

    private String getTracksTableHeading(List<Playlist> playlistList, String searchQuery) {
        if(Constants.EMPTY.equalsIgnoreCase(searchQuery)) {
            getTracksTableHeading(playlistList);
        }
        return String.format("Matches for '%s' in %s", searchQuery, getTracksTableHeading(playlistList));
    }

    private String getTracksTableHeading(List<Playlist> playlistList) {
        if(playlistList.size() == 1) {
            return playlistList.get(0).getName();
        }
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < playlistList.size(); i++) {
            Playlist playlist = playlistList.get(i);
            if(i != 0) {
                buffer.append(", ");
            }
            buffer.append(playlist.getName());
        }

        return buffer.toString();
    }

    private void addTracks(final TracksTableModel model, final Track[] tracks) {

        for (final Track track : tracks) {

            final TrackHolder holder = new TrackHolder(track);

            boolean isCurrentTrackLoaded = Main.getItunesMediaPlayer().isCurrentTrack(track.getTrackId());
            Object status;
            if(isCurrentTrackLoaded) {
                status = Main.getItunesMediaPlayer().isPlaying() ? currentlyPlayingIcon : currentlyPausedIcon;
            } else {
                status = Constants.EMPTY;
            }
            model.addRow(new Object[]{
                    status, //      Status: playing/not-playing
                model.getRowCount() + 1, //S.No.
                holder, //Name
                TrackDataParser.parseTime(
                    track.getAdditionalTrackInfo().getAdditionalInfo(TIME) //Time
                    ),
                track.getAdditionalTrackInfo().getAdditionalInfo(ARTIST), //Artist
                track.getAdditionalTrackInfo().getAdditionalInfo(ALBUM), //Album
                track.getAdditionalTrackInfo().getAdditionalInfo(GENRE), //Genre
                track.getAdditionalTrackInfo().getAdditionalInfo(RATING), //Rating
                track.getAdditionalTrackInfo().getAdditionalInfo(PLAYS), //Plays
            });

        }
    }

    private void enableCopyPlaylists(final boolean isEnable) {
        copyPlaylistsMenuItem.setEnabled(isEnable);
    }

    private static final String ARTIST = "Artist";
    private static final String ALBUM = "Album";
    private static final String TIME = "Total Time";
    private static final String GENRE = "Genre";
    private static final String RATING = "Rating";
    private static final String PLAYS = "Play Count";

    private boolean match(Track track) {

        return (doMatch(track.getTrackName()))
            || (doMatch(track.getAdditionalTrackInfo().getAdditionalInfo(ARTIST)))
            || (doMatch(track.getAdditionalTrackInfo().getAdditionalInfo(ALBUM)));
    }

    private boolean doMatch(final String str){
        return str != null && str.toUpperCase().contains(searchQuery);
    }
}
