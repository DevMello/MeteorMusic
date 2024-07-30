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
import meteordevelopment.meteorclient.gui.tabs.Tabs;

import meteordevelopment.meteorclient.utils.misc.Version;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import com.devmello.music.gui.MusicTab;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MusicPlugin extends MeteorAddon {
    public static Version CURRENT_VERSION = new Version("0.2.0");
    public static final String RELEASES_URL = "https://raw.githubusercontent.com/DevMello/MeteorMusic/main/.releases";
    public static final String UPDATE_URL = "https://github.com/DevMello/MeteorMusic/releases";
    public static boolean updateAvailable = false;
    public static final Logger LOG = LogUtils.getLogger();
    public static final HudGroup HUD_GROUP = new HudGroup("Music");
    public static String api_key = "AIzaSyBNpjmwdyPybDRJS0YceMc2tcuxgXoF_Bc";
    public static final File FOLDER = new File(MeteorClient.FOLDER, "music");
    public static final String MP3 = "file:///" + MusicPlugin.FOLDER + "\\musicfile.mp3";

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Music Addon - DevMello");

        if(!folderCheck() && !YoutubeExecutor.init()) return;
        checkUpdate();
        loadAPIs();

        // Commands
        Commands.add(new PlaylistCommand());
        Commands.add(new SkipCommand());
        Commands.add(new PreviousCommand());
        Commands.add(new SelectCommand());
        Commands.add(new SearchCommand());
        Commands.add(new CleanCommand());
        Commands.add(new UpdateCommand());
        Commands.add(new PlayCommand());
        Commands.add(new ResumeCommand());
        Commands.add(new PauseCommand());
        Commands.add(new StopCommand());
        Commands.add(new VolumeCommand());
        Commands.add(new RepeatCommand());


        Hud.get().register(MusicImage.INFO);
        Hud.get().register(MusicText.INFO);

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
        try {
            URL url = new URL(RELEASES_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String latestVersion = in.readLine().trim();
                    Version latest = new Version(latestVersion);
                    LOG.info("Current version: " + CURRENT_VERSION);
                    LOG.info("Latest version: " + latest);
                    updateAvailable = latest.isHigherThan(CURRENT_VERSION);
                    if (latest.isHigherThan(CURRENT_VERSION)) {
                        openWebPage(UPDATE_URL);
                    }
                }
            } else {
            LOG.error("Failed to fetch the updates file: HTTP error code " + con.getResponseCode());
            }
        } catch (Exception e) {
            LOG.error("Failed to check for updates.");
        }
    }

    private static void openWebPage(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String[] command;
            if (os.contains("win")) {
                command = new String[]{"rundll32", "url.dll,FileProtocolHandler", url};
            } else if (os.contains("mac")) {
                command = new String[]{"open", url};
            } else if (os.contains("nix") || os.contains("nux")) {
                command = new String[]{"xdg-open", url};
            } else if (os.contains("nux")) {
                command = new String[]{"kde-open", url};
            } else {
                throw new UnsupportedOperationException("Unsupported operating system");
            }
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Failed to open web page: " + e.getMessage());
        }
    }


    public static void info(String title, Text message) {
        ChatUtils.forceNextPrefixClass(MusicPlugin.class);
        ChatUtils.sendMsg(title, message);
    }

    public static void warning(String title, String message, Object... args) {
        ChatUtils.forceNextPrefixClass(MusicPlugin.class);
        ChatUtils.warningPrefix(title, message, args);
    }

    public static void error(String title, String message, Object... args) {
        ChatUtils.forceNextPrefixClass(MusicPlugin.class);
        ChatUtils.errorPrefix(title, message, args);
    }
}
