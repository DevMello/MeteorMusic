package com.devmello.music.util.files;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileCleanupScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startCleanupTask(File directory, long initialDelay, long period, TimeUnit unit) {
        Runnable cleanupTask = new SongCleanupTask(directory);
        scheduler.scheduleAtFixedRate(cleanupTask, initialDelay, period, unit);
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
