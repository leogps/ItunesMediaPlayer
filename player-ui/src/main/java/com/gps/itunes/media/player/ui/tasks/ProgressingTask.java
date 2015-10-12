/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tasks;

import com.gps.itunes.media.player.ui.exceptions.TaskExecutionException;

/**
 *
 * A Task whose progress needs to be tracked.
 *
 * @author leogps
 */
public interface ProgressingTask<T extends TaskParams> {

    /**
     *
     * When a task is submitted, it's progress is tracked while it is run.
     *
     * @param t
     * @throws TaskExecutionException
     */
    public void submitTask(final T t) throws TaskExecutionException;

    /**
     *
     * Runs the task. In order to track the progress, call submitTask() instead.
     *
     * @param t
     */
    public void runTask(final T t);

    /**
     * Notifies task start. Init progress info when the task's progress is
     * indeterminate. <br> Progress is tracked as indeterminate throughout the
     * task run.
     *
     */
    public void notifyEventTrigger();

    /**
     *
     * Notifies task start. Init progress info with progress value. <br>
     * Progress is tracked as a determinate value during the task run.
     *
     * @param i
     */
    public void notifyEventTrigger(final int i);

    /**
     *
     * Called after the task is completed. Sets the progress to complete.
     *
     */
    public void notifyEventCompletion();

    /**
     *
     * Called whenever the progress needs to be set.
     *
     * @param i
     */
    public void setProgress(final int i);
}
