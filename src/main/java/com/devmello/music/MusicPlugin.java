package com.devmello.music;

import com.devmello.music.commands.*;
import com.devmello.music.hud.MusicImage;
import com.devmello.music.modules.ModuleExample;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MusicPlugin extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Example");
    public static final HudGroup HUD_GROUP = new HudGroup("Music");
    public static final String api_key = "AIzaSyBNpjmwdyPybDRJS0YceMc2tcuxgXoF_Bc";
    public static final File FOLDER = new File(MeteorClient.FOLDER, "music");
    public static final String MP3 = "file:///" + MusicPlugin.FOLDER + "\\music.mp3";
    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Music Addon - DevMello");

        if(!folderCheck()) {
            LOG.error("Failed to create plugin folder.");
            return;
        }

        if(initFFMPEG() && initYTDL()) {
            LOG.info("Initialization complete.");
        } else {
            LOG.error("Initialization failed.");
            return;
        }


        // Modules
        Modules.get().add(new ModuleExample());

        // Commands
        Commands.add(new ListCommand());
        Commands.add(new CommandExample());
        Commands.add(new SearchCommand());
        Commands.add(new MusicCommand());
        Commands.add(new UpdateCommand());
        Commands.add(new PlayCommand());
        Commands.add(new ResumeCommand());
        Commands.add(new PauseCommand());
        Commands.add(new StopCommand());
        Commands.add(new VolumeCommand());
        Commands.add(new RepeatCommand());

        // HUD
        Hud.get().register(MusicImage.INFO);

        //TAB TEST
        Tabs.add(new MusicTab());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.devmello.music";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("DevMello", "MeteorMusic");
    }

    public boolean initYTDL(){
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

    public boolean initFFMPEG() {
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

    public boolean folderCheck() {
        if (!MusicPlugin.FOLDER.exists()) {
            LOG.warn("Plugin folder does not exist, creating...");
            if (!MusicPlugin.FOLDER.mkdirs()) {
                LOG.error("Failed to create plugin folder.");
                return false;
            } else {
                LOG.info("Plugin folder created.");
                return true;
            }
        }
        return MusicPlugin.FOLDER.exists();
    }
}
