package com.devmello.music.commands;

import com.devmello.music.gui.MusicPanel;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.command.CommandSource;

public class TestScreenCommand extends Command {
    public TestScreenCommand() {
        super("test-screen", "Opens the test screen.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(c -> {
            c.getCommand();
            info("Opening test screen.");
            mc.setScreen(MusicPanel.getInstance());
            info("Opened test screen.");
            return SINGLE_SUCCESS;
        });
    }
}
