package com.gps.imp.utils;

import java.util.concurrent.*;

/**
 * Created by leogps on 11/13/15.
 */
public class SingleQueuedThreadExecutor {

    private final int KEEP_ALIVE_TIME = 120; // Timeout after 2 minutes.
    private final int WORK_QUEUE_CAPACITY = 1;
    private final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(WORK_QUEUE_CAPACITY);
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);
    private Future lastThread;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void terminateExistingAndInvokeLater(Runnable runnable) {
        terminate();
        purgeAndEnqueue(runnable);
    }

    private void terminate() {
        if(lastThread != null && !lastThread.isDone()) {
            lastThread.cancel(true);
        }
    }

    public void terminateExistingAndScheduleForLater(Runnable runnable, long delay, TimeUnit timeUnit) {
        terminate();
        if(!scheduledExecutorService.isTerminated()) {
            lastThread = scheduledExecutorService.schedule(runnable, delay, timeUnit);
        }
    }

    private void purgeAndEnqueue(Runnable runnable) {
        threadPoolExecutor.purge();
        if(!threadPoolExecutor.isTerminated()) {
            lastThread = threadPoolExecutor.submit(runnable);
        }
    }

}
