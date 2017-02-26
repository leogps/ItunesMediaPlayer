package com.gps.imp.utils.ui;

/**
 * Created by leogps on 12/22/15.
 */
public interface InterruptableAsyncTask<T, R> {

    T execute() throws Exception;

    void registerListener(AsyncTaskListener asyncTaskListener);

    void interrupt();

    boolean isInterrupted();

    R getResult();
}
