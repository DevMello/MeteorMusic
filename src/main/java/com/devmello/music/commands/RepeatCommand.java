package com.devmello.music.commands;

import com.devmello.music.player.Player;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;



public class RepeatCommand extends Command {
    public RepeatCommand() {
        super("repeat", "Repeats the music.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("repeat", BoolArgumentType.bool()).executes(context -> {
            boolean repeat = BoolArgumentType.getBool(context, "repeat");
            //TODO: Add repeat functionality to YoutubeExecutor
            Player.player.setRepeat(repeat);
            info("Repeat set to " + repeat);
            return SINGLE_SUCCESS;
        }));
    }
}
