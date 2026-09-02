package com.bot.massive;

import com.bot.util.RestClient;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class MassiveClient extends RestClient {

    private final String api_key;

    public MassiveClient(String api_key) {
        super("api.massive.com", "/v3");
        assert api_key != null;
        this.api_key = api_key;
    }

    public static class Response<T> {

        public int count;
        public int next_url;
        public int request_id;
        public ArrayList<T> results;
        public String status;

    }

    private HttpResponse<String> get_with_key(String path) throws ResponseException {
        return get(path + "&apiKey=" + api_key);
    }

    public Response<Ticker> get_ticker(HashMap<String, String> query) throws ResponseException {
        final String response =
                get_with_key("/reference/tickers" + RestClient.stringify_query(query))
                        .body();

        // TODO: parse
        System.out.println(response);
        return new Response<>();
    }

}
