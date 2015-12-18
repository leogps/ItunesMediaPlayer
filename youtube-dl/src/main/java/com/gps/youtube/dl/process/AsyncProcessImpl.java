package com.gps.youtube.dl.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leogps on 12/18/15.
 */
public class AsyncProcessImpl implements AsyncProcess {

    private final String[] command;
    private Process process;
    private final List<AsyncProcessListener> asyncProcessListeners = new ArrayList<AsyncProcessListener>();
    private boolean interrupted;

    public AsyncProcessImpl(String... command) {
        this.command = command;
    }

    public Process executeProcess() throws IOException {
        if(isExecuting()) {
            throw new IllegalStateException("Previously submitted process is still executing. Only one process is permitted.");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        process = processBuilder.start();
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

        return process;
    }

    private void informFailure() {
        for(AsyncProcessListener asyncProcessListener : asyncProcessListeners) {
            asyncProcessListener.onFailure(this);
        }
    }

    private void informSuccess() {
        for(AsyncProcessListener asyncProcessListener : asyncProcessListeners) {
            asyncProcessListener.onSuccess(this);
        }
    }

    public void interruptProcess() {
        if(isExecuting()) {
            process.destroy();
            interrupted = true;
        }
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void registerListener(AsyncProcessListener asyncProcessListener) {
        this.asyncProcessListeners.add(asyncProcessListener);
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

    public void registerListeners(List<AsyncProcessListener> asyncProcessListenerList) {
        this.asyncProcessListeners.addAll(asyncProcessListenerList);
    }

    public Process getProcess() {
        return process;
    }

    public String[] getCommand() {
        return command;
    }
}
