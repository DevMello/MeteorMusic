package com.devmello.music.gui;

import com.devmello.music.util.YoutubeExecutor;
import com.devmello.music.util.render.FontRenderer;
import com.devmello.music.util.render.RenderEngine;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MusicPanel extends Screen {
    public MusicPanel() {
        super(Text.of("Music Panel"));
        this.setInstance();
    }
    // Singleton
    public static MusicPanel INSTANCE;
    public boolean isDragging = false;
    public boolean isResizing = false;
    public int mouseX;
    public int mouseY;
    public int mainX = 100;
    public int mainY = 100;
    public int dragX;
    public int dragY;
    private final int mainWidth = 400;
    private int mainHeight = 250;
    public boolean searching = false;
    public String search = "Search";

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
        if (!Utils.canUpdate()) renderBackground(context, mouseX, mouseY, delta);

        context.getMatrices().push();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        renderPanel(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
    }

    public void renderPanel(DrawContext context, int mouseX, int mouseY, float delta) {

        if (isDragging) {
            mainX = mouseX - dragX;
            mainY = mouseY - dragY;
        }

        RenderEngine.drawRound(context.getMatrices(), mainX, mainY, mainWidth, mainHeight, 9f, new Color(36, 25, 37, 250));
        context.getMatrices().push();
        context.getMatrices().scale(0.85f, 0.85f, 1);
        context.getMatrices().translate((mainX + 10) / 0.85, (mainY + 10) / 0.85, 0);
        RenderEngine.Jello.drawString(context.getMatrices(), "Music", 0, 0, Color.WHITE.getRGB());
        context.getMatrices().translate(-(mainX + 10) / 0.85, -(mainY + 10) / 0.85, 0);
        context.getMatrices().scale(1, 1, 1);
        context.getMatrices().pop();

        //SEARCH
        RenderEngine.drawRound(context.getMatrices(), mainX + 250, mainY + 15, 140, 10, 3f, new Color(52, 38, 58, 250));
        RenderEngine.icons.drawString(context.getMatrices(), "s", mainX + 378, mainY + 16, searching ? new Color(0xCBFFFFFF, true).getRGB() : new Color(0x83FFFFFF, true).getRGB());
        RenderEngine.COMFORTAA_SMALL.drawString(context.getMatrices(),  search, mainX + 252, mainY + 16, searching ? new Color(0xCBFFFFFF, true).getRGB() : new Color(0x83FFFFFF, true).getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHoveringItem(mainX, mainY, width, 30, (float) mouseX, (float) mouseY)) {
            dragX = (int) (mouseX - mainX);
            dragY = (int) (mouseY - mainY);
            isDragging = true;
        }
        if (isHoveringItem(mainX + 250, mainY + 13, 140, 10, (float) mouseX, (float) mouseY)) {
            searching = true;
            search = "";
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        isResizing = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }



    @Override
    public boolean shouldPause() {
        return false;
    }

    public boolean isHoveringItem(float x, float y, float x1, float y1, float mouseX, float mouseY) {
        return (mouseX >= x && mouseY >= y && mouseX <= x1 + x && mouseY <= y1 + y);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searching) {
            if (keyCode == 256) {
                searching = false;
                search = "Search";
            } else if (keyCode == 257) {
                if (!search.isEmpty() && !search.equalsIgnoreCase("Search")) {
                    YoutubeExecutor.search(search);
                    searching = false;
                    search = "Search";
                }
            } else if (keyCode == 259) {
                if (!search.isEmpty()) {
                    search = search.substring(0, search.length() - 1);
                }
            } else {
                if (keyCode < 256) {
                    search += (char) keyCode;
                }
            }
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            super.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return false;
    }
}
