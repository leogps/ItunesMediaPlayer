package com.gps.imp.utils.process;

import com.gps.imp.utils.ui.AsyncTaskListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leogps on 12/18/15.
 */
public class AsyncProcessImpl implements AsyncProcess<Void> {

    private final String[] command;
    protected Process process;
    private final List<AsyncTaskListener> asyncTaskListeners = new ArrayList<AsyncTaskListener>();
    private boolean interrupted;

    public AsyncProcessImpl(String... command) {
        this.command = command;
    }

    public synchronized Process execute() throws IOException {
        if(isExecuting()) {
            throw new IllegalStateException("Previously submitted process is still executing. Only one process is permitted.");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        process = processBuilder.start();
        waitForAsync();

        return process;
    }

    protected void waitForAsync() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    process.waitFor();
                    informSuccess();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    informFailure();
                }
            }
        }).start();
    }

    public void waitFor() throws InterruptedException {
        process.waitFor();
    }

    protected void informFailure() {
        for(AsyncTaskListener asyncTaskListener : asyncTaskListeners) {
            asyncTaskListener.onFailure(this);
        }
    }

    protected void informSuccess() {
        for(AsyncTaskListener asyncTaskListener : asyncTaskListeners) {
            asyncTaskListener.onSuccess(this);
        }
    }

    public void interrupt() {
        if(isExecuting()) {
            process.destroy();
            interrupted = true;
        }
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public Void getResult() {
        return null;
    }

    public void registerListener(AsyncTaskListener asyncTaskListener) {
        this.asyncTaskListeners.add(asyncTaskListener);
    }

    public boolean isExecuting() {
        if(process != null) {
            try {
                process.exitValue();
            } catch (IllegalThreadStateException  e) {
                return true;
            }
        }
        return false;
    }

    public void registerListeners(List<AsyncTaskListener> asyncTaskListenerList) {
        this.asyncTaskListeners.addAll(asyncTaskListenerList);
    }

    public Process getProcess() {
        return process;
    }

    public String[] getCommand() {
        return command;
    }
}
