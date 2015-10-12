/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tasks;

/**
 *
 * CopyPlaylist Task's parameters.
 * 
 * @author leogps
 */
public class CopyTaskParams extends TaskParams {

    private String copyDestFolder;

    /**
     * 
     * Set copy destination.
     * 
     * @param copyDestFolder 
     */
    public void setCopyDestFolder(final String copyDestFolder) {
        this.copyDestFolder = copyDestFolder;
    }
    
    /**
     * 
     * Return copy destination.
     * 
     * @return 
     */
    public String getCopyDestFolder() {
        return copyDestFolder;
    }
}
