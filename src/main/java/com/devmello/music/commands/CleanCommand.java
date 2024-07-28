package com.devmello.music.commands;

import com.devmello.music.util.YoutubeExecutor;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class CleanCommand extends Command {
    public CleanCommand() {
        super("clean", "Cleans the music folder.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            YoutubeExecutor.clean();
            info("Cleaning now");
            return SINGLE_SUCCESS;
        });

    }
}
