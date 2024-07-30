package com.devmello.music.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import com.devmello.music.util.YoutubeExecutor;

public class PreviousCommand extends Command {
    public PreviousCommand() {
        super("previous", "Plays the previous song.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            YoutubeExecutor.prev();
            info("Playing previous song.");
            return SINGLE_SUCCESS;
        });
    }
}
