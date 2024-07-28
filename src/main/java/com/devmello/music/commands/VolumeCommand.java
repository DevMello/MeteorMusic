package com.devmello.music.commands;

import com.devmello.music.player.Player;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class VolumeCommand extends Command {
    public VolumeCommand() {
        super("volume", "set the volume of the music");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Volume is " + Player.vol);
            return SINGLE_SUCCESS;
        });

        builder.then(argument("volume", IntegerArgumentType.integer(0, 100)).executes(context -> {
            int volume = IntegerArgumentType.getInteger(context, "volume");
            //TODO: Add volume functionality to YoutubeExecutor
            Player.setVolume(volume);
            info("Volume set to " + volume);
            return SINGLE_SUCCESS;
        }));

    }
}
