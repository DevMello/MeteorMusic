package com.devmello.music.util;

import com.devmello.music.MusicPlugin;
import com.devmello.music.hud.MusicImage;
import com.devmello.music.player.Player;
import com.devmello.music.youtube.search.Item;
import com.mojang.logging.LogUtils;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Song {
    public String title = "";
    public String artist = "";
    public String url;
    public String thumbnail;
    public String id;
    public static final Logger LOG = LogUtils.getLogger();
    //we only need one thread to download and play music


    public static String extractThumbnail(String url) {
        return "https://img.youtube.com/vi/" + extractVideoID(url) + "/hqdefault.jpg";
    }

    public Song(Item item) {
        this.title = item.getSnippet().getTitle();
        this.artist = item.getSnippet().getChannelTitle();
        this.url = "https://www.youtube.com/watch?v=" + item.getId().getVideoId();
        this.thumbnail = item.getSnippet().getThumbnails().getHigh().getUrl();
        this.id = item.getId().getVideoId();
    }

    public Song(String title, String artist, String url) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.thumbnail = extractThumbnail(url);
        this.id = extractVideoID(url);
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getId() {
        return extractVideoID(url);
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

    public String toString() {
        return title + " - " + artist;
    }

    public void play() {
        Future<Boolean> future = YoutubeExecutor.executorService.submit(() -> YoutubeExecutor.download(url));
        MusicImage.loadImageFromUrl(thumbnail);
        YoutubeExecutor.executorService.submit(() -> {
            try {
                boolean success = future.get();
                if (success) {
                    MusicPlugin.info("Downloaded", Text.of("Playing: " + MusicPlugin.MP3.replace("musicfile", id)));
                    LOG.info("Downloaded");
                    Player.play(MusicPlugin.MP3.replace("musicfile", id));
                } else {
                    MusicPlugin.info("Failed to download", Text.of("Failed to download"));
                    LOG.error("Failed to download");
                }
            } catch (Exception e) {
                MusicPlugin.info("Exception during download", Text.of("Failed to download"));
                LOG.error("Exception during download", e);
            }
        });
    }

}
