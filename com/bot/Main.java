package com.bot;

import com.bot.massive.MassiveClient;
import com.bot.massive.Ticker;
import com.bot.util.DotEnv;
import com.bot.util.Queryable;
import com.bot.util.RestClient;

import java.io.IOException;
import java.util.HashMap;

public class Main {

    static HashMap<String, String> env;
    static MassiveClient massive_client;

    @SuppressWarnings("unused")
    public static void main(String[] args) throws RestClient.ResponseException, IOException {
        env = DotEnv.get(".env");
        massive_client = new MassiveClient(env.get("api_key"));

        final MassiveClient.Response<Ticker> response = massive_client.get_ticker(new Queryable() {
            String market = "stocks";
            boolean active = true;
            String order = "asc";
            int limit = 1;
        });

    }

}