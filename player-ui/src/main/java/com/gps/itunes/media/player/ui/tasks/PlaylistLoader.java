/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tasks;

import com.gps.itunes.lib.items.playlists.Playlist;
import com.gps.itunes.media.player.ui.exceptions.TaskExecutionException;
import com.gps.itunes.media.player.ui.handlers.ProgressHandler;
import com.gps.itunes.media.player.dto.PlaylistHolder;
import com.gps.itunes.media.player.ui.tablehelpers.models.PlaylistTableModel;
import com.gps.itunes.lib.tasks.LibraryParser;
import javax.swing.JProgressBar;
import javax.swing.JTable;

/**
 *
 * Task to load playlists into the PlaylistTable.
 * 
 * @author leogps
 */
public class PlaylistLoader extends ProgressHandler{
    
    private static org.apache.log4j.Logger log = 
            org.apache.log4j.Logger.getLogger(PlaylistLoader.class);
    
    private final JTable playlistTable;
    
    private final LibraryParser parser;
    
    
    public PlaylistLoader(final LibraryParser parser, final JProgressBar progressBar,
            final JTable playlistTable){
        super(progressBar, TaskType.MAJOR_TASK);
        this.parser = parser;
        this.playlistTable = playlistTable;
    }
    
    
    private void loadPlaylist() throws TaskExecutionException{
        final PlaylistTableModel model = 
                    (PlaylistTableModel) playlistTable.getModel(); 
        

        model.clear();
        
        try {
            
            final Playlist plist[] = parser.getAllPlaylists();
            
            for(final Playlist playlist : plist){
                model.addRow(new PlaylistHolder[]{new PlaylistHolder(playlist)});
            }
            
            setProgressMsg("Playlists loaded.");
            
        } catch (Exception ex) {
            throw new TaskExecutionException(this, ex);
        }
    }

    @Override
    public void runTask(final TaskParams params) {
        try {
            loadPlaylist();
        } catch (TaskExecutionException ex) {
            log.error(ex);
        }
    }
    
}
