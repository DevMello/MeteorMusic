package com.devmello.music.commands;

import com.devmello.music.util.YoutubeExecutor;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class SkipCommand extends Command {
    public SkipCommand() {
        super("skip", "Skips the current song.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            YoutubeExecutor.next();
            info("Skipping song.");
            return SINGLE_SUCCESS;
        });
    }
}
