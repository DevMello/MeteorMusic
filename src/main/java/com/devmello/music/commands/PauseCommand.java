package com.devmello.music.commands;

import com.devmello.music.player.Player;
import com.devmello.music.util.YoutubeExecutor;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class PauseCommand extends Command {
    public PauseCommand() {
        super("pause", "Pauses the music.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            YoutubeExecutor.pause();
            info("Paused");
            return SINGLE_SUCCESS;
        });
    }
}
