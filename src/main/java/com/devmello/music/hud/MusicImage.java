package com.devmello.music.hud;


import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;

import com.devmello.music.MusicPlugin;

import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MusicImage extends HudElement {
    public static final HudElementInfo<MusicImage> INFO = new HudElementInfo<>(MusicPlugin.HUD_GROUP, "Song Image", "The thumbnail of the song.", MusicImage::new);
    private static final Identifier textureId = new Identifier("music", "music.png");

    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    public final Setting<ImageSize> imageSize = sgGeneral.add(new EnumSetting.Builder<ImageSize>()
        .name("image-size")
        .description("The size of the image.")
        .defaultValue(ImageSize.SMALL)
        .build());

    public MusicImage() {
        super(INFO);
    }

    public static void loadImageFromUrl(String urlString) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                LogUtils.getLogger().info("Loading image from: {}", urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                LogUtils.getLogger().info("Connected to URL");

                try (InputStream inputStream = connection.getInputStream()) {
                    LogUtils.getLogger().info("loaded image from URL");
                    BufferedImage jpegImage = ImageIO.read(inputStream);
                    if (jpegImage == null) {
                        throw new IOException("Failed to read JPEG image");
                    }
                    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(jpegImage, "PNG", pngOutputStream);
                    ByteArrayInputStream pngInputStream = new ByteArrayInputStream(pngOutputStream.toByteArray());
                    NativeImage nativeImage = NativeImage.read(pngInputStream);
                    LogUtils.getLogger().info("Image loaded");
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.getTextureManager().registerTexture(textureId, new NativeImageBackedTexture(nativeImage));
                }
            } catch (IOException e) {
                LogUtils.getLogger().error("Failed to load image from URL", e);
            }
        }).start();
    }

    @Override
    public void render(HudRenderer renderer) {
        setSize(imageSize.get().width, imageSize.get().height);
        if (MinecraftClient.getInstance().getTextureManager().getTexture(textureId) == null) return;
        renderer.texture(textureId, x, y, getWidth(), getHeight(), Color.WHITE);
    }

    public enum ImageSize {
        SMALL(120, 90),
        MEDIUM(240, 180),
        LARGE(480, 360);

        public final int width;
        public final int height;

        ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}

