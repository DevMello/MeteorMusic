package com.devmello.music.util.render.utilities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RendererUtils {
    private static final char RND_START = 'a';
    private static final char RND_END = 'z';
    private static final Random RND = new Random();


    public static int getGuiScale() {
        return (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
    }

    @Contract(value = "-> new", pure = true)
    public static Identifier randomIdentifier() {
        return Identifier.of("renderer", "temp/" + randomString(32));
    }

    private static String randomString(int length) {
        return IntStream.range(0, length)
            .mapToObj(operand -> String.valueOf((char) RND.nextInt(RND_START, RND_END + 1)))
            .collect(Collectors.joining());
    }
}
