package com.devmello.music.util.files;

import com.devmello.music.util.YoutubeExecutor;

import java.io.File;

public class SongCleanupTask implements Runnable {
    private final File directory;
    private final boolean cleanAll;

    public SongCleanupTask(File directory, boolean cleanAll) {
        this.directory = directory;
        this.cleanAll = cleanAll;
    }

    public SongCleanupTask(File directory) {
        this(directory, false); // Default to non-recursive cleanup
    }

    @Override
    public void run() {
        if (directory.exists() && directory.isDirectory()) {
            if (cleanAll) {
                cleanupFilesRecursively(directory);
            } else {
                cleanupFiles(directory);
            }
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }
    }

    private void cleanupFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".mp3")) {
                    if (YoutubeExecutor.currentSong != null && file.getName().equals(YoutubeExecutor.currentSong.getId())) {
                        continue;
                    }
                    if (file.delete()) {
                        YoutubeExecutor.LOG.info("Deleted file: {}", file.getName());
                    } else {
                        YoutubeExecutor.LOG.info("Failed to delete file: {}", file.getName());
                    }
                }
            }
        }
    }

    private void cleanupFilesRecursively(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".mp3")) {
                    if (file.delete()) {
                        YoutubeExecutor.LOG.info("Deleted file: {}", file.getName());
                    } else {
                        YoutubeExecutor.LOG.info("Failed to delete file: {}", file.getName());
                    }
                } else if (file.isDirectory()) {

                    cleanupFilesRecursively(file);
                }
            }
        }
    }
}
