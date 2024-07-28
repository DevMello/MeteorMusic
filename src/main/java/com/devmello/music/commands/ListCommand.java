package com.devmello.music.commands;


import com.devmello.music.util.YoutubeExecutor;
import com.devmello.music.youtube.search.Search;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class ListCommand extends Command {
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
            YoutubeExecutor.play(search.getItems().get(number));
            return SINGLE_SUCCESS;
        }));
    }

}
