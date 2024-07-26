package com.devmello.music.commands;

import com.devmello.music.util.YoutubeExecutor;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;


public class SearchCommand extends Command {
    public SearchCommand() {
        super("search", "Search for a song on YouTube.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("query", StringArgumentType.greedyString()).executes(context -> {
            String query = StringArgumentType.getString(context, "query");
            info("Searching for: " + query);
            YoutubeExecutor.search(query).getItems().forEach(item -> info(item.getSnippet().getTitle() + " - " + item.getSnippet().getChannelTitle()));
            return SINGLE_SUCCESS;
        }));
    }
}
