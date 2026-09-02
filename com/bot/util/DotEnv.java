package com.bot.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class DotEnv {

    public static HashMap<String, String> get(String filename) throws IOException {
        final String content = Files.readString(Path.of(filename));
        final HashMap<String, String> env = new HashMap<>();

        for(final String line : content.split("\n")) {
            final String[] sections = line.split("=");
            env.put(sections[0], sections[1]);
        }

        return env;
    }

}
