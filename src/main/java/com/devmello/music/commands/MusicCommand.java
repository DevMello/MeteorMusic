package com.devmello.music.commands;

import com.devmello.music.MusicPlugin;
import com.devmello.music.player.Player;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.io.File;

public class MusicCommand extends Command {
    public MusicCommand() {
        super("music", "Plays a Song.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info(new File(MusicPlugin.FOLDER, "music.mp3").getAbsolutePath());
            Player.play(MusicPlugin.MP3);
            info("Playing now");
            return SINGLE_SUCCESS;
        });

    }
}
