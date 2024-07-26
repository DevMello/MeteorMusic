package com.devmello.music.commands;

import com.devmello.music.MusicPlugin;
import com.devmello.music.hud.MusicImage;
import com.devmello.music.player.Player;
import com.devmello.music.util.YoutubeExecutor;
import com.devmello.music.youtube.search.Search;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListCommand extends Command {
    public static final Logger LOG = LogUtils.getLogger();;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    public ListCommand() {
        super("list", "List all songs in the queue. and play a specific song.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Listing all songs in the queue.");
            Search search = YoutubeExecutor.currentSearch;
            if (search == null) {
                error("No search results");
                return SINGLE_SUCCESS;
            }
            search.getItems().forEach(item -> info(item.getSnippet().getTitle() + " - " + item.getSnippet().getChannelTitle()));
            return SINGLE_SUCCESS;
        });

        builder.then(argument("number", IntegerArgumentType.integer()).executes(context -> {
            int number = IntegerArgumentType.getInteger(context, "number");
            Search search = YoutubeExecutor.currentSearch;
            if (search == null) {
                error("No search results");
                return SINGLE_SUCCESS;
            }
            if (number < 0 || number >= search.getItems().size()) {
                error("Invalid number");
                return SINGLE_SUCCESS;
            }
            warning("Stopping current song");
            //release windows file handle lock to allow deletion
            YoutubeExecutor.play(search.getItems().get(number));
//            Player.stop();
//            String url = "https://www.youtube.com/watch?v=" + search.getItems().get(number).getId().getVideoId();
//            MusicImage.loadImageFromUrl(search.getItems().get(number).getSnippet().getThumbnails().getHigh().getUrl());
//            LOG.info(search.getItems().get(number).getSnippet().getThumbnails().getHigh().getUrl());
//
//            LOG.info("Playing: {}", url);
//            info("Playing: " + url);
//            Future<Boolean> future = executorService.submit(() -> YoutubeExecutor.download(url));
//
//            executorService.submit(() -> {
//                try {
//                    boolean success = future.get();
//                    if (success) {
//                        LOG.info("Downloaded");
//                        info("Downloaded");
//                        Player.play(MusicPlugin.MP3);
//                        LOG.info("Playing: " + MusicPlugin.MP3);
//                        info("Playing now");
//                    } else {
//                        error("Failed to download");
//                        LOG.error("Failed to download");
//                    }
//                } catch (Exception e) {
//                    error("Exception during download");
//                    LOG.error("Exception during download", e);
//                }
//            });
            return SINGLE_SUCCESS;
        }));
    }

}
