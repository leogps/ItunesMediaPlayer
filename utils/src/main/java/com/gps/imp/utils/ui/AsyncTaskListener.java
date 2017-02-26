package com.gps.imp.utils.ui;

/**
 * Created by leogps on 12/18/15.
 */
public interface AsyncTaskListener<S, V> {

    void onSuccess(InterruptableAsyncTask<S, V> interruptableAsyncTask);

    void onFailure(InterruptableAsyncTask<S, V> interruptableAsyncTask);

}
