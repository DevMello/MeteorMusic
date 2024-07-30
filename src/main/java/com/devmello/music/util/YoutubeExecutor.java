package com.devmello.music.util;

import com.devmello.music.MusicPlugin;
import com.devmello.music.player.Player;
import com.devmello.music.youtube.WebUtils;
import com.devmello.music.youtube.search.Item;
import com.devmello.music.youtube.search.Search;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import org.slf4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;


import static com.devmello.music.util.Song.extractVideoID;


public class YoutubeExecutor {
    public static final String os = System.getProperty("os.name").toLowerCase();
    public static final Logger LOG = LogUtils.getLogger();
    public static final String WINDOWS_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp.exe";
    public static final String LINUX_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp_linux";
    public static final String MAC_URL = "https://github.com/yt-dlp/yt-dlp/releases/download/2024.07.25/yt-dlp_macos";
    public static final String FFMPEG_URL = "https://raw.githubusercontent.com/devmello/MeteorMusic/master/utils/ffmpeg.exe";
    public static String exec = MusicPlugin.FOLDER + File.separator + "yt-dlp" + (os.contains("win") ? ".exe" : "");
    public static final ExecutorService executorService = Executors.newFixedThreadPool(50);
    public static List<Song> currentSongList;
    public static int currentSongIndex;
    public static Search currentSearch;
    public static Song currentSong;
    public static FileCleanupScheduler cleanupScheduler = new FileCleanupScheduler();
    public static String JSONPlaylist = "";

    public YoutubeExecutor() {}

    public static boolean init() {
        if (!initYTDL()) {
            LOG.error("Failed to initialize yt-dlp.");
            return false;
        }
        if (!initFFMPEG()) {
            LOG.error("Failed to initialize ffmpeg.");
            return false;
        }
        cleanupScheduler.startCleanupTask(MusicPlugin.FOLDER, 0, 30, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cleanupScheduler.shutdown();
            MeteorExecutor.execute(new SongCleanupTask(MusicPlugin.FOLDER, true));
        }));
        return true;
    }

    public static void play(Item item) {
        if (currentSong != null && Objects.equals(currentSong.getId(), item.getId().getVideoId())) {
            Player.resume();
            return;
        }
        currentSong = new Song(item);
        play();
    }

    public static void play(String url) {
        if (currentSong != null && Objects.equals(currentSong.getUrl(), url)) {
            Player.resume();
            return;
        }
        currentSong = new Song("Unknown", "Unknown", url);
        play();
    }

    public static void play() {
        LOG.info("Downloading: {}", currentSong.getUrl());
        currentSong.play();
    }

    public static void clean() {
        MeteorExecutor.execute(new SongCleanupTask(MusicPlugin.FOLDER));
    }

    public static void next() {
        if (currentSongList == null) return;
        if (currentSongIndex < currentSongList.size() - 1) {
            currentSongIndex++;
            currentSong = currentSongList.get(currentSongIndex);
            currentSong.play();
        }
    }

    public static void prev() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            currentSong = currentSongList.get(currentSongIndex);
            currentSong.play();
        }
    }

    public static void pause() {Player.pause();}

    public static void resume() {Player.resume();}

    public static void stop() {Player.stop(); currentSong = null;}

    public static void setVolume(int volume) {Player.setVolume(volume);}


    public static Search search(String query){
        Search currentSearch;
        Gson gson = new Gson();
        currentSearch = gson.fromJson(WebUtils.visitSite("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&q="
            + query.replace(" ", "%20")
            + "&type=video&key="+MusicPlugin.api_key), Search.class);
        currentSearch.getItems().forEach(item -> LOG.info(item.getSnippet().getTitle()));
        LOG.info("Search made");
        LOG.info(gson.toString());
        YoutubeExecutor.currentSearch = currentSearch;
        return currentSearch;
    }

    public static void playPlaylist(String playlistUrl) {
        new Thread(() -> {
            String[] command = {exec, "--flat-playlist", "-J", playlistUrl};
            LOG.info("Command: {}", Arrays.stream(command).toList());
            try {
                Process process = Runtime.getRuntime().exec(command);
                new Thread(() -> readStream(process.getErrorStream(), "ERROR")).start();
                new Thread(() -> readStream(process.getInputStream(), "INFO")).start();
                process.waitFor();
                String jsonOutput =  JSONPlaylist;
                JsonObject jsonObject = JsonParser.parseString(jsonOutput).getAsJsonObject();
                JsonArray entries = jsonObject.getAsJsonArray("entries");
                LOG.info("Playlist entries: {}", entries.size());
                LOG.info("Playlist entries: {}", entries);
                List<Song> songs = new ArrayList<>();
                List<Future<Boolean>> downloadFutures = new ArrayList<>();
                for (int i = 0; i < entries.size(); i++) {
                    JsonObject entry = entries.get(i).getAsJsonObject();
                    String videoId = entry.get("id").getAsString();
                    String title = entry.get("title").getAsString();
                    String uploader = entry.get("uploader").getAsString();
                    LOG.info("Video ID: {}", videoId);
                    LOG.info("Title: {}", title);
                    LOG.info("Uploader: {}", uploader);
                    String outputFileName = String.format("%s.mp3", videoId);
                    String outputPath = MusicPlugin.FOLDER + File.separator + outputFileName;
                    Future<Boolean> downloadFuture = executorService.submit(() -> {
                        try {
                            String[] downloadCommand = {exec, "-x", "--audio-format", "mp3", "--force-overwrites", "-o", outputPath, "https://www.youtube.com/watch?v=" + videoId};
                            LOG.info("Download Command: {}", Arrays.stream(downloadCommand).toList());
                            Process downloadProcess = Runtime.getRuntime().exec(downloadCommand);
                            downloadProcess.waitFor();
                            return true;
                        } catch (Exception e) {
                            LOG.error("Failed to download videoId: {}", videoId);
                            LOG.error(e.getMessage());
                            return false;
                        }
                    });
                    downloadFutures.add(downloadFuture);
                    songs.add(new Song(title, uploader, Song.createVideoURL(videoId)));
                }
                CompletableFuture<Void> firstSongPlayFuture = CompletableFuture.runAsync(() -> {
                    try {
                        if (!songs.isEmpty()) {
                            currentSongList = songs;
                            currentSongIndex = 0;
                            currentSong = songs.getFirst();
                            currentSong.play();
                            LOG.info("Playing first song immediately.");
                        }
                    } catch (Exception e) {
                        LOG.error("Failed to play the first song.");
                        LOG.error(e.getMessage());
                    }
                });
                for (Future<Boolean> future : downloadFutures) {
                    try {
                        future.get(); // Wait for completion
                    } catch (Exception e) {
                        LOG.error("Error waiting for download completion.");
                        LOG.error(e.getMessage());
                    }
                }


            } catch (Exception e) {
                LOG.error("Failed to process playlist");
                LOG.error(e.getMessage());
            }
        }).start();
    }

    private static String readStream(InputStream stream, String type) {
        int count = 0;
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
                count++;
                LOG.info(line);
            }
            LOG.info("Read{} lines from {} stream", count, type);
            if (type.equals("INFO")) JSONPlaylist = output.toString();
            return output.toString();
        } catch (Exception e) {
            LOG.error("Error reading process stream");
            LOG.error(e.getMessage());
        }
        return "";
    }


    public static boolean download(String url) {
        //\yt-dlp.exe -x --audio-format mp3 --force-overwrites -o "music.%(ext)s" url
        //String command = exec + " -x --audio-format mp3 --force-overwrites -o \"" + MusicPlugin.FOLDER + File.separator + "music.%(ext)s\" " + url;
//        String command = exec + " -x --audio-format mp3 --force-overwrites -o \"" + MusicPlugin.FOLDER + File.separator + extractVideoID(url) + ".%(ext)s\" " + url;
        String[] command = {exec, "-x", "--audio-format", "mp3", "--force-overwrites", "-o", MusicPlugin.FOLDER + File.separator + extractVideoID(url) + ".%(ext)s", url};
        LOG.info("Command: {}", Arrays.stream(command).toList() );
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return true;
        } catch (Exception e) {
            LOG.error("Failed to download");
            LOG.error(e.getMessage());
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
                LOG.error("No file to download. Server replied HTTP code: {}", responseCode);
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
        String[] commandArray = command.split(" ");
        LOG.info("Command: " + command);
        try {
            Process p = Runtime.getRuntime().exec(commandArray);
            p.waitFor();
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage());
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
                String saveFilePath = MusicPlugin.FOLDER + File.separator + "ffmpeg.exe";
                OutputStream outputStream = new FileOutputStream(saveFilePath);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                LOG.info(outputStream.toString());
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

    public static boolean initFFMPEG() {
        if (YoutubeExecutor.os.contains("win")) {
            if (!new File(MusicPlugin.FOLDER, "ffmpeg.exe").exists()) {
                LOG.warn("ffmpeg not installed, installing...");
                if (YoutubeExecutor.installFFMPEG()) {
                    LOG.info("ffmpeg installed.");
                    return true;
                } else {
                    LOG.error("Failed to install ffmpeg.");
                    return false;
                }
            } else {
                LOG.info("ffmpeg is installed.");
                return true;
            }
        } else {
            String command = "ffmpeg -version";
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            try {
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    LOG.info(line);
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    LOG.info("ffmpeg is installed.");
                    return true;
                } else {
                    LOG.error("ffmpeg is not installed, install it yourself.");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("Failed to check ffmpeg installation.");
                return false;
            }
        }
    }

    public static boolean initYTDL(){
        if (YoutubeExecutor.checkInstall()) {
            LOG.info("yt-dlp installation found.");
            LOG.info("Attempting to update yt-dlp.");
            if (YoutubeExecutor.update()) {
                LOG.info("yt-dlp updated.");
                return true;
            } else {
                LOG.error("Failed to update yt-dlp.");
                return false;
            }
        } else {
            LOG.warn("yt-dlp not installed, installing...");
            if (YoutubeExecutor.install()) {
                LOG.info("yt-dlp installed.");
                LOG.info("Attempting to update yt-dlp.");
                if (YoutubeExecutor.update()) {
                    LOG.info("yt-dlp updated.");
                } else {
                    LOG.error("Failed to update yt-dlp.");
                }
                return true;
            } else {
                LOG.error("Failed to install yt-dlp.");
                return false;
            }
        }
    }

    public static boolean checkDownloaded(String id) {
        return new File(MusicPlugin.FOLDER, id + ".mp3").exists();
    }
}
