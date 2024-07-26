package com.devmello.music.util;

import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Song {
    public String title;
    public String artist;
    public String url;
    public String thumbnail;
    public String id;

    public Song(String title, String artist, String url, String thumbnail, String id) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.thumbnail = thumbnail;
        this.id = id;
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

}
