/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.exceptions;

import com.gps.itunes.media.player.ui.tasks.ProgressingTask;

/**
 *
 * Any Exception occurred during the task execution of a {@link #com.gps.itunes.lib.parser.ui.tasks.ProgressingTask ProgressingTask}
 * is wrapped inside this exception.
 * 
 * @author leogps
 */
public class TaskExecutionException extends Exception{
    
    private static org.apache.log4j.Logger log = 
            org.apache.log4j.LogManager.getLogger(TaskExecutionException.class);
    
    public TaskExecutionException(final ProgressingTask task, final Exception ex){
        super(ex);
        
        log.error("Exception occured when executing task: " + task);
        log.error(ex);
    }
}
