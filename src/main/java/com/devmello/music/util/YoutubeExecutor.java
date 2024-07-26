package com.devmello.music.util;

import com.devmello.music.MusicPlugin;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class YoutubeExecutor {
    public static final String os = System.getProperty("os.name").toLowerCase();
    public static final Logger LOG = LogUtils.getLogger();
    public static final String WINDOWS_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp.exe";
    public static final String LINUX_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp_linux";
    public static final String MAC_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp_macos";
    public static String exec = MusicPlugin.FOLDER + File.separator + "yt-dlp" + (os.contains("win") ? ".exe" : "");
    public YoutubeExecutor() {
    }



    public static boolean download(String url) {
        //\yt-dlp.exe -x --audio-format mp3 --force-overwrites -o "music.%(ext)s" url
        String command = exec + " -x --audio-format mp3 --force-overwrites -o \"" + MusicPlugin.FOLDER + File.separator + "music.%(ext)s\" " + url;
        LOG.info("Command: " + command);
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return true;
        } catch (Exception e) {
            LOG.error("Failed to download");
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkInstall() {
        if (os.contains("win")) {
            return new File(MusicPlugin.FOLDER, "yt-dlp.exe").exists();
        } else if (os.contains("nix") || os.contains("nux")) {
            return new File(MusicPlugin.FOLDER, "yt-dlp").exists();
        } else if (os.contains("mac")) {
            return new File(MusicPlugin.FOLDER, "yt-dlp").exists();
        }
        return false;
    }

    public static boolean install() {
        try {
            String link = "";
            if (os.contains("win")) {
                link = WINDOWS_URL;
                LOG.info("Windows");
            } else if (os.contains("nix") || os.contains("nux")) {
                link = LINUX_URL;
                LOG.info("Linux");
            } else if (os.contains("mac")) {
                link = MAC_URL;
                LOG.info("Mac");
            }
            URL url = new URL(link);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = httpConn.getInputStream();
                String fileName = "yt-dlp" + (os.contains("win") ? ".exe" : "");
                String saveFilePath = MusicPlugin.FOLDER + File.separator + fileName;
                OutputStream outputStream = new FileOutputStream(saveFilePath);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
                LOG.info("File downloaded");
            } else {
                LOG.error("No file to download. Server replied HTTP code: " + responseCode);
            }

            httpConn.disconnect();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update() {
        String command = exec + " -U";
        LOG.info("Command: " + command);
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}