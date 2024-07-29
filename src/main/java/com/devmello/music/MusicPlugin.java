package com.devmello.music;

import com.devmello.music.commands.*;
import com.devmello.music.hud.MusicImage;
import com.devmello.music.hud.MusicText;
import com.devmello.music.util.Secrets;
import com.devmello.music.util.YoutubeExecutor;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.gui.tabs.Tabs;

import org.slf4j.Logger;
import com.devmello.music.gui.MusicTab;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class MusicPlugin extends MeteorAddon {
    public static final String CURRENT_VERSION = "0.1.1";
    public static final String RELEASES_URL = "https://raw.githubusercontent.com/DevMello/MeteorMusic/main/.releases";
    public static final String UPDATE_URL = "https://github.com/DevMello/MeteorMusic/releases";
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Example");
    public static final HudGroup HUD_GROUP = new HudGroup("Music");
    public static String api_key = "AIzaSyBNpjmwdyPybDRJS0YceMc2tcuxgXoF_Bc";
    public static final File FOLDER = new File(MeteorClient.FOLDER, "music");
    public static final String MP3 = "file:///" + MusicPlugin.FOLDER + "\\musicfile.mp3";
    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Music Addon - DevMello");

        if(!folderCheck() && !YoutubeExecutor.init()) return;

        loadAPIs();

        // Modules
//        Modules.get().add(new ModuleExample());

        // Commands
        Commands.add(new ListCommand());
        Commands.add(new CommandExample());
        Commands.add(new SearchCommand());
        Commands.add(new CleanCommand());
        Commands.add(new UpdateCommand());
        Commands.add(new PlayCommand());
        Commands.add(new ResumeCommand());
        Commands.add(new PauseCommand());
        Commands.add(new StopCommand());
        Commands.add(new VolumeCommand());
        Commands.add(new RepeatCommand());

        // HUD
        Hud.get().register(MusicImage.INFO);
        Hud.get().register(MusicText.INFO);
        //TAB TEST
        Tabs.add(new MusicTab());
    }

    @Override
    public String getPackage() {
        return "com.devmello.music";
    }
    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("DevMello", "MeteorMusic");
    }

    public boolean folderCheck() {
        if (!MusicPlugin.FOLDER.exists()) {
            LOG.warn("Plugin folder does not exist, creating...");
            if (!MusicPlugin.FOLDER.mkdirs()) {
                LOG.error("Failed to create plugin folder.");
                return false;
            } else {
                LOG.info("Plugin folder created.");
            }
        }
        if (!Secrets.exists()) {
            LOG.warn("Secrets file does not exist, creating...");
            if (!Secrets.create()) {
                LOG.error("Failed to create secrets file.");
                return false;
            } else {
                LOG.info("Secrets file created.");
            }
        }
        return MusicPlugin.FOLDER.exists() && Secrets.exists() && Secrets.load();
    }

    public static void loadAPIs() {
        if (Secrets.get("youtube") == null) {
            LOG.warn("Youtube API key not found in secrets file.");
        } else {
            api_key = Secrets.get("youtube");
            LOG.info("Youtube API key loaded.");
        }
    }

    public static void checkUpdate() {
        // url for updates:
        try {
            URL url = new URL(RELEASES_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    // Step 2: Read the first line (latest version)
                    String latestVersion = in.readLine().trim();

                    // Step 3: Compare versions
                    if (!CURRENT_VERSION.equals(latestVersion)) {
                        // Versions are different, open the update URL
                        openWebPage(UPDATE_URL);
                    }
                }
            } else {
            System.err.println("Failed to fetch the updates file: HTTP error code " + con.getResponseCode());
            }
        } catch (Exception e) {
            LOG.error("Failed to check for updates.");
        }
    }

    private static void openWebPage(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URL(url).toURI());
            } else {
                System.err.println("Desktop not supported. Cannot open web page.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to open web page: " + e.getMessage());
        }
    }

}
