package com.devmello.music.util;

import com.devmello.music.MusicPlugin;
import com.devmello.music.youtube.WebUtils;
import com.devmello.music.youtube.search.Search;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeExecutor {
    public static final String os = System.getProperty("os.name").toLowerCase();
    public static final Logger LOG = LogUtils.getLogger();
    public static final String WINDOWS_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp.exe";
    public static final String LINUX_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp_linux";
    public static final String MAC_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp_macos";
    public static final String FFMPEG_URL = "https://raw.githubusercontent.com/devmello/MeteorMusic/master/utils/ffmpeg.exe";
    public static String exec = MusicPlugin.FOLDER + File.separator + "yt-dlp" + (os.contains("win") ? ".exe" : "");
    public static Search currentSearch;

    public YoutubeExecutor() {
    }

    public static String extractVideoID(String urlString) {
        try {
            URL url = new URL(urlString);
            String query = url.getQuery();
            if (query != null) {
                Map<String, String> queryPairs = new HashMap<>();
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (idx > 0) {
                        queryPairs.put(pair.substring(0, idx), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                }
                return queryPairs.get("v");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Search search(String query){
        Search currentSearch;
        Gson gson = new Gson();
        currentSearch = gson.fromJson(WebUtils.visitSite("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&q="
            + query.replace(" ", "%20")
            + "&type=video&key="+MusicPlugin.api_key), Search.class);
        currentSearch.getItems().forEach(item -> LOG.info(item.getSnippet().getTitle()));
        LOG.info("Search made");
        LOG.info(gson.toString());
        YoutubeExecutor.currentSearch = currentSearch;
        return currentSearch;
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

    public static boolean installFFMPEG() {
        try {
            URL url = new URL(FFMPEG_URL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = httpConn.getInputStream();
                String fileName = "ffmpeg.exe";
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
                return false;
            }

            httpConn.disconnect();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
