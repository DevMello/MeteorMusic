package com.devmello.music.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.systems.hud.HudElement;

import com.devmello.music.MusicPlugin;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MusicImage extends HudElement {
    public static final HudElementInfo<MusicImage> INFO = new HudElementInfo<>(MusicPlugin.HUD_GROUP, "Song Image", "The thumbnail of the song.", MusicImage::new);
    //private static final Identifier textureId = new Identifier("music", "music.png");

    public MusicImage() {
        super(INFO);

    }
//
//    public static void loadImageFromUrl(String urlString) {
//        new Thread(() -> {
//            try {
//                URL url = new URL(urlString);
//                LogUtils.getLogger().info("Loading image from: {}", urlString);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.connect();
//                LogUtils.getLogger().info("Connected to URL");
//
//                try (InputStream inputStream = connection.getInputStream()) {
//                    LogUtils.getLogger().info("loaded image from URL");
//                    NativeImage nativeImage = NativeImage.read(inputStream);
//                    LogUtils.getLogger().info("Image loaded");
//                    MinecraftClient client = MinecraftClient.getInstance();
//                    client.getTextureManager().registerTexture(textureId, new NativeImageBackedTexture(nativeImage));
//                }
//            } catch (IOException e) {
//                LogUtils.getLogger().error("Failed to load image from URL", e);
//            }
//        }).start();
//    }

    @Override
    public void render(HudRenderer renderer) {
//        Identifier texture = new Identifier("music", "icon.png");
        setSize(256, 144);

        //renderer.texture(textureId, x, y, getWidth(), getHeight(), Color.WHITE);

//        // Render background
//        renderer.quad(x, y, getWidth(), getHeight(), Color.LIGHT_GRAY);
//
//        // Render text
//        renderer.text("Example element", x, y, Color.WHITE, true);
    }
}

