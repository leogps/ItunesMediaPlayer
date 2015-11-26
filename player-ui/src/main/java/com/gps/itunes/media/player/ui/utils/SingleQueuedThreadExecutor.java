package com.gps.itunes.media.player.ui.utils;

import java.util.concurrent.*;

/**
 * Created by leogps on 11/13/15.
 */
public class SingleQueuedThreadExecutor {

    private int KEEP_ALIVE_TIME = 120; // Timeout after 2 minutes.
    private int WORK_QUEUE_CAPACITY = 1;
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(WORK_QUEUE_CAPACITY);
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);
    private Future lastThread;

    public void terminateExistingAndInvokeLater(Runnable runnable) {
        if(lastThread != null && !lastThread.isDone()) {
            lastThread.cancel(true);
        }
        purgeAndEnqueue(runnable);
    }

    private void purgeAndEnqueue(Runnable runnable) {
        threadPoolExecutor.purge();
        if(!threadPoolExecutor.isTerminated()) {
            lastThread = threadPoolExecutor.submit(runnable);
        }
    }

}
