package com.devmello.music.commands;

import com.devmello.music.util.YoutubeExecutor;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import org.slf4j.Logger;

public class UpdateCommand extends Command {
    public static final Logger LOG = LogUtils.getLogger();
    public UpdateCommand() {
        super("update", "Updates the yt-dlp.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            //TODO: Add update functionality to MusicPlugin to check for MOD updates
            YoutubeExecutor.initYTDL();
            return SINGLE_SUCCESS;
        });
    }
}
