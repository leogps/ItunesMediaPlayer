package com.gps.imp.utils.ui;

/**
 * Created by leogps on 12/18/15.
 */
public interface AsyncTaskListener {

    void onSuccess(InterruptableAsyncTask interruptableAsyncTask);

    void onFailure(InterruptableAsyncTask interruptableAsyncTask);

}
