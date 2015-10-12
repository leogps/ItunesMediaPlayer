/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.handlers;

import com.gps.itunes.media.player.ui.controller.MajorTaskInfo;
import com.gps.itunes.media.player.ui.exceptions.TaskExecutionException;
import com.gps.itunes.media.player.ui.tasks.ProgressingTask;
import com.gps.itunes.media.player.ui.tasks.TaskParams;
import com.gps.itunes.media.player.ui.tasks.TaskType;
import java.util.Random;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * Provides a way to run a 'Task' and show it's progress in a progress bar.
 *
 * @author leogps
 */
public abstract class ProgressHandler implements ProgressingTask {

    private final JProgressBar progressBar;
    private final TaskType taskType;
    private static final int COMPLETE = 100;
    private static final int INVALID_PROGRESS = -1;
    private int progress;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ProgressHandler.class);

    public ProgressHandler(final JProgressBar progressBar,
            final TaskType taskType) {
        this.progressBar = progressBar;
        this.taskType = taskType;
    }

    @Override
    public void notifyEventCompletion() {
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }
        setProgress(COMPLETE);

    }

    @Override
    public void notifyEventTrigger(final int i) {
        progressBar.setIndeterminate(false);
        setProgress(i);
    }

    @Override
    public void submitTask(final TaskParams taskParams) throws TaskExecutionException {
        //  If a major task is already running and a subTask is submitted,
        // just run the task without showing progress info.
        if (taskType == TaskType.SUB_TASK
                && MajorTaskInfo.isMajorTaskInProgress()) {

            runTask(taskParams);

        } else {

            // Indeterminate Task
            if (taskParams.getProgress() == INVALID_PROGRESS) {
                notifyEventTrigger();
            } else { // Determinate Task.
                notifyEventTrigger(taskParams.getProgress());
            }

            // Running in a SwingWorker.
            synchronized (this) {
                new TaskRunner(taskParams).execute();
            }
        }


    }

    @Override
    public void notifyEventTrigger() {
        progressBar.setIndeterminate(true);
        setProgress(0);
    }

    @Override
    public void setProgress(final int i) {
        progress = i;
        progressBar.setValue(i);
    }

    /**
     *
     * Returns the underlying progresBar object.
     *
     * @return
     */
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     *
     * Set the progressBar message. Calls the {@link#javax.swing.JProgressBar.setString(msg)}
     * method.
     *
     * @param msg
     */
    public void setProgressMsg(final String msg) {
        if (taskType == TaskType.SUB_TASK
                && MajorTaskInfo.isMajorTaskInProgress()) {
            log.debug(msg);
        } else {
            progressBar.setString(msg);
        }
    }

    private class TaskRunner extends SwingWorker {

        private final TaskParams taskParams;

        public TaskRunner(final TaskParams taskParams) {
            this.taskParams = taskParams;
        }

        @Override
        protected Void doInBackground() throws TaskExecutionException {
            runTask(taskParams);

            final Random random = new Random();
            progress = taskParams.getProgress();
            final int oneSec = 1000;

            while (progress < COMPLETE) {
                try {
                    Thread.sleep(random.nextInt(oneSec));
                } catch (InterruptedException ex) {
                    log.error(ex);
                }
                setProgress(progress);
            }
            return null;
        }

        @Override
        protected void done() {
            notifyEventCompletion();
        }
    }
}
