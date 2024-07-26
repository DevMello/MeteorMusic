package com.devmello.music;

import com.devmello.music.commands.*;
import com.devmello.music.hud.HudExample;
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

import java.io.File;

public class MusicPlugin extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Example");
    public static final HudGroup HUD_GROUP = new HudGroup("Example");
    public static final String api_key = "AIzaSyBNpjmwdyPybDRJS0YceMc2tcuxgXoF_Bc";
    public static final File FOLDER = new File(MeteorClient.FOLDER, "music");
    public static final String MP3 = "file:///" + MusicPlugin.FOLDER + "\\music.mp3";
    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Music Addon - DevMello");

        if (!MusicPlugin.FOLDER.exists()) {
            LOG.warn("Plugin folder does not exist, creating...");
            if (!MusicPlugin.FOLDER.mkdirs()) {
                LOG.error("Failed to create plugin folder.");
            } else {
                LOG.info("Plugin folder created.");
            }
        }

        if (YoutubeExecutor.checkInstall()) {
            LOG.info("yt-dlp installation found.");
            LOG.info("Attempting to update yt-dlp.");
            if (YoutubeExecutor.install()) {
                LOG.info("yt-dlp updated.");
            } else {
                LOG.error("Failed to update yt-dlp.");
            }
        } else {
            LOG.warn("yt-dlp not installed, installing...");
            if (YoutubeExecutor.install()) {
                LOG.info("yt-dlp installed.");
            } else {
                LOG.error("Failed to install yt-dlp.");
            }
        }
        // Modules
        Modules.get().add(new ModuleExample());

        // Commands
        Commands.add(new CommandExample());
        Commands.add(new MusicCommand());
        Commands.add(new UpdateCommand());
        Commands.add(new PlayCommand());
        Commands.add(new ResumeCommand());
        Commands.add(new PauseCommand());
        Commands.add(new StopCommand());
        Commands.add(new VolumeCommand());

        // HUD
        Hud.get().register(HudExample.INFO);

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
        return new GithubRepo("DevMello", "meteor-music");
    }
}
