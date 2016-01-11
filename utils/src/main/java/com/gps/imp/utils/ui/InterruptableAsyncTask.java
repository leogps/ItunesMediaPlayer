package com.gps.imp.utils.ui;

/**
 * Created by leogps on 12/22/15.
 */
public interface InterruptableAsyncTask {

    void registerListener(AsyncTaskListener asyncTaskListener);

    void interrupt();

    boolean isInterrupted();
}
