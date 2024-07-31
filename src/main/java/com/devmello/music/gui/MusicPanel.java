package com.devmello.music.gui;

import com.devmello.music.util.render.RenderEngine;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MusicPanel extends Screen {
    public MusicPanel() {
        super(Text.of("Music Panel"));
    }
    // Singleton
    public static MusicPanel INSTANCE;

    public int mouseX;
    public int mouseY;
    public int mainX = 100;
    public int mainY = 100;
    private final int mainWidth = 400;
    private int mainHeight = 250;

    public static MusicPanel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MusicPanel();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        renderBackground(context, mouseX, mouseY, delta);
        if (!Utils.canUpdate()) renderBackground(context, mouseX, mouseY, delta);
        context.drawTextWithBackground(mc.textRenderer , Text.of("Music Panel"), mainX + 5, mainY + 5, new Color(255, 255, 255, 255).getRGB(), new Color(0, 0, 0, 255).getRGB());
        this.mouseX = mouseX;
        this.mouseY = mouseY;
//        renderPanel(context, mouseX, mouseY, delta);
//        context.getMatrices().pop();
    }

    public void renderPanel(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderEngine.drawRound(context.getMatrices(), mainX, mainY, mainWidth, mainHeight, 9f, new Color(37, 27, 41, 250));
    }
}
