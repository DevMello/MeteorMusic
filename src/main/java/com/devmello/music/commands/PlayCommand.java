package com.devmello.music.commands;

import com.devmello.music.MusicPlugin;
import com.devmello.music.hud.MusicImage;
import com.devmello.music.player.Player;
import com.devmello.music.util.YoutubeExecutor;
import com.devmello.music.youtube.search.Search;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import org.slf4j.Logger;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlayCommand extends Command {
    public static final Logger LOG = LogUtils.getLogger();
    //we only need one thread to download and play music
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public PlayCommand() {
        super("play", "Plays a Song.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Player.play("file:///" + MusicPlugin.FOLDER + "\\musicfile.mp3");
            info("Playing now");
            return SINGLE_SUCCESS;
        });

        builder.then(argument("url", StringArgumentType.string()).executes(context -> {
            String url = StringArgumentType.getString(context, "url");
            info("Playing: " + url);
            YoutubeExecutor.play(url);
            return SINGLE_SUCCESS;
        }));

    }
}
