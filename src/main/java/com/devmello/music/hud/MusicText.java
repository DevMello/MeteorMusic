package com.devmello.music.hud;

import com.devmello.music.MusicPlugin;
import com.devmello.music.util.YoutubeExecutor;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;


public class MusicText extends HudElement {
    public static final HudElementInfo<MusicText> INFO = new HudElementInfo<>(MusicPlugin.HUD_GROUP, "Song Text", "The text of the song.", MusicText::new);


    public MusicText() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (YoutubeExecutor.currentSong == null) return;
        setSize(renderer.textWidth(YoutubeExecutor.currentSong.toString()), renderer.textHeight());
        renderer.text(YoutubeExecutor.currentSong.toString(), x, y, Color.WHITE, false);
    }
}
