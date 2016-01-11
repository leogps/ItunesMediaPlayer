package com.gps.youtube.dl.process;

import com.gps.imp.utils.ui.AsyncTaskListener;
import com.gps.imp.utils.ui.InterruptableAsyncTask;

import java.io.IOException;
import java.util.List;

/**
 * Created by leogps on 12/18/15.
 */
public interface AsyncProcess extends InterruptableAsyncTask {

    Process execute() throws IOException;

    boolean isExecuting();

    void registerListeners(List<AsyncTaskListener> asyncTaskListenerList);

    Process getProcess();

    String[] getCommand();
}
