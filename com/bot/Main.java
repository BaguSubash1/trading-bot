package com.bot;

import com.bot.massive.MassiveClient;
import com.bot.massive.Ticker;
import com.bot.util.DotEnv;
import com.bot.util.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    static HashMap<String, String> env;
    static MassiveClient massive_client;

    public static void main(String[] args) throws RestClient.ResponseException, IOException {
        env = DotEnv.get(".env");
        massive_client = new MassiveClient(env.get("api_key"));

         MassiveClient.Response<Ticker> response = massive_client.get_ticker(new HashMap<>(Map.of(
                "market", "stocks",
                "active", "true",
                "order", "asc",
                "limit", "1"
        )));

        System.out.println("hooray");
    }

}