package com.gps.youtube.dl.process;

import java.io.IOException;
import java.util.List;

/**
 * Created by leogps on 12/18/15.
 */
public interface AsyncProcess {

    Process executeProcess() throws IOException;

    void registerListener(AsyncProcessListener asyncProcessListener);

    void interruptProcess();

    boolean isExecuting();

    void registerListeners(List<AsyncProcessListener> asyncProcessListenerList);

    Process getProcess();

    String[] getCommand();

    boolean isInterrupted();
}
