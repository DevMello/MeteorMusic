package com.devmello.music.gui;

import com.devmello.music.util.YoutubeExecutor;
import com.devmello.music.youtube.search.Item;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.client.gui.screen.Screen;
import meteordevelopment.meteorclient.gui.tabs.Tab;

import java.util.List;

public class MusicTab extends Tab {
    public MusicTab() {
        super("Music");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new MusicScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof MusicScreen;
    }

    private static class MusicScreen extends WindowTabScreen {
        public MusicScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {

            add(theme.label("Music Player")).expandX().center().widget();

            WHorizontalList searchList = add(theme.horizontalList()).expandX().widget();

            WTextBox search = searchList.add(theme.textBox("Search")).expandX().widget();
            search.actionOnUnfocused = () -> {
                if (search.get().isEmpty()) search.set("Search");
            };
            search.setFocused(false);

            WPlus add = searchList.add(theme.plus()).widget();
            add.action = () -> {
                String query = search.get().trim();
                if (query.isEmpty() || query.equalsIgnoreCase("Search")) return;
                YoutubeExecutor.search(query);
                if (!(YoutubeExecutor.currentSearch == null)) {
                    search.set("");
                    reload();
                } else {
                    YoutubeExecutor.LOG.error("Failed to search for: {}", query);
                }
            };

            enterAction = add.action;

            add(theme.horizontalSeparator()).expandX();

            WTable table = add(theme.table()).expandX().minWidth(400).widget();
            initTable(table);

            WHorizontalList playlist = add(theme.horizontalList()).expandX().widget();

            WTextBox URL = playlist.add(theme.textBox("URL")).expandX().widget();
            URL.setFocused(false);
            WPlus addPlaylist = playlist.add(theme.plus()).widget();

            addPlaylist.action = () -> {
                String url = URL.get().trim();
                if (url.isEmpty() || url.contains("URL")) return;
                YoutubeExecutor.playPlaylist(url);
                reload();
            };
        }

        private void initTable(WTable table) {
            table.clear();
            List<Item> items = List.of();
            if (!(YoutubeExecutor.currentSearch == null)) {
                items = YoutubeExecutor.currentSearch.getItems();
            }

            for (Item item: items) {
                table.add(theme.label(item.getSnippet().getTitle() + " - " + item.getSnippet().getChannelTitle()));
                WPlus select = table.add(theme.plus()).expandCellX().right().widget();

                select.action = () -> {
                    YoutubeExecutor.play(item);
                    reload();
                };

                table.row();
            }
        }

    }
}
