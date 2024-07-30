package com.devmello.music.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.devmello.music.util.YoutubeExecutor;

public class PlaylistCommand extends Command {
    public PlaylistCommand() {
        super("playlist", "Plays a playlist.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("playlist", StringArgumentType.string()).executes(context -> {
            String playlist = StringArgumentType.getString(context, "playlist");
            YoutubeExecutor.playPlaylist(playlist);
            info("Playing playlist " + playlist);
            return SINGLE_SUCCESS;
        }));
    }

}
