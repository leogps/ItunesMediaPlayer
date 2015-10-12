/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tasks;

/**
 *
 * Task Params that hold Progressing Task's parameters.
 * 
 * @author leogps
 */
public class TaskParams {
    
    private int progress = -1;

    /**
     * 
     * Return progress value.
     * 
     * @return 
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 
     * Set progress value.
     * 
     * @param progress 
     */
    public void setProgress(final int progress) {
        this.progress = progress;
    }    
    
}
