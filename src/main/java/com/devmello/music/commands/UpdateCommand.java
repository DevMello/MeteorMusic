package com.devmello.music.commands;

import com.devmello.music.MusicPlugin;
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
    //TODO: build an autoupdater which runs at shutdown to create a thread to install the latest update if needed
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            MusicPlugin.checkUpdate();
            return SINGLE_SUCCESS;
        });
    }
}
