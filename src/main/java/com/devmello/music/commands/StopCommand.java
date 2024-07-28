package com.devmello.music.commands;

import com.devmello.music.player.Player;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop", "Stops the music.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            //TODO: Add stop functionality to YoutubeExecutor
            Player.stop();
            info("Stopped");
            return SINGLE_SUCCESS;
        });
    }
}
