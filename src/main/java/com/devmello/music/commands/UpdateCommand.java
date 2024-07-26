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
            if (YoutubeExecutor.checkInstall()) {
                LOG.info("yt-dlp installation found.");
                LOG.info("Attempting to update yt-dlp.");
                info("yt-dlp installation found."
                        + "\nAttempting to update yt-dlp.");
                if (YoutubeExecutor.update()) {
                    LOG.info("yt-dlp updated.");
                    info("yt-dlp updated.");
                } else {
                    LOG.error("Failed to update yt-dlp.");
                    error("Failed to update yt-dlp.");
                }
            } else {
                LOG.warn("yt-dlp not installed, installing...");
                warning("yt-dlp not installed, installing...");
                if (YoutubeExecutor.install()) {
                    LOG.info("yt-dlp installed.");
                    info("yt-dlp installed.");
                } else {
                    LOG.error("Failed to install yt-dlp.");
                    error("Failed to install yt-dlp.");
                }
            }
            return SINGLE_SUCCESS;
        });
    }
}
