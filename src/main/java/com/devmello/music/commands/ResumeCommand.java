package com.devmello.music.commands;

import com.devmello.music.player.Player;
import com.devmello.music.util.YoutubeExecutor;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class ResumeCommand extends Command {
    public ResumeCommand() {
        super("resume", "Resumes the music.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            YoutubeExecutor.resume();
            info("Resumed");
            return SINGLE_SUCCESS;
        });
    }
}
