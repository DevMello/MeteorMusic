package com.devmello.music.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static com.devmello.music.hud.MusicImage.loadImageFromUrl;
import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

/**
 * The Meteor Client command API uses the <a href="https://github.com/Mojang/brigadier">same command system as Minecraft does</a>.
 */
public class CommandExample extends Command {
    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public CommandExample() {
        super("example", "Sends a message.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            loadImageFromUrl("https://t2.genius.com/unsafe/425x425/https%3A%2F%2Fimages.genius.com%2Ffa02d8bc4c7ee74b5a1408c2be032fea.1000x1000x1.png");
            info("hi");
            return SINGLE_SUCCESS;
        });

        builder.then(literal("name").then(argument("nameArgument", StringArgumentType.word()).executes(context -> {
            String argument = StringArgumentType.getString(context, "nameArgument");
            info("hi, " + argument);
            return SINGLE_SUCCESS;
        })));
    }
}
