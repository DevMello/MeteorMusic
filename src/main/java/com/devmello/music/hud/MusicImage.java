package com.devmello.music.hud;

import meteordevelopment.meteorclient.systems.hud.HudElement;

import com.devmello.music.MusicPlugin;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Identifier;

public class MusicImage extends HudElement {
    public static final HudElementInfo<MusicImage> INFO = new HudElementInfo<>(MusicPlugin.HUD_GROUP, "Song Image", "The thumbnail of the song.", MusicImage::new);

    public MusicImage() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        Identifier texture = new Identifier("music", "icon.png");
        setSize(256, 256);

        renderer.texture(texture, x, y, getWidth(), getHeight(), Color.WHITE);

//        // Render background
//        renderer.quad(x, y, getWidth(), getHeight(), Color.LIGHT_GRAY);
//
//        // Render text
//        renderer.text("Example element", x, y, Color.WHITE, true);
    }
}

