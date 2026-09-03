package com.bot;

import com.bot.massive.MassiveClient;
import com.bot.massive.Ticker;
import com.bot.util.DotEnv;
import com.bot.util.JSONParser;
import com.bot.util.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static HashMap<String, String> env;
    static MassiveClient massive_client;

    public static class Test {

        public String field;

    }

    public static void main(String[] args) throws RestClient.ResponseException, IOException, JSONParser.MalformedJSONException {
        env = DotEnv.get(".env");
        massive_client = new MassiveClient(env.get("api_key"));

        MassiveClient.Response<Ticker> response = massive_client.get_ticker(new HashMap<>(Map.of(
                "market", "stocks",
                "active", "true",
                "order", "asc",
                "limit", "1"
        )));


    }

}