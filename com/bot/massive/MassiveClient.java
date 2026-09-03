package com.bot.massive;

import com.bot.util.JSONParser;
import com.bot.util.Queryable;
import com.bot.util.RestClient;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class MassiveClient extends RestClient {

    private final String api_key;

    public MassiveClient(String api_key) {
        super("api.massive.com", "/v3");
        assert api_key != null;
        this.api_key = api_key;
    }

    public static class Response<T> {

        public int count;
        public String next_url;
        public String request_id;
        public String status;
        public ArrayList<T> results = new ArrayList<>();

        @SuppressWarnings("unchecked")
        public Response(HashMap<String, Object> json, Function<HashMap<String, Object>, T> result_builder) {
            count = (Integer) json.get("count");
            next_url = (String) json.get("next_url");
            request_id = (String) json.get("request_id");
            status = (String) json.get("status");

            for(final Object builder_map : (ArrayList<?>) json.get("results")) {
                results.add(result_builder.apply((HashMap<String, Object>) builder_map));
            }
        }

    }

    private HttpResponse<String> get_with_key(String path) throws ResponseException {
        return get(path + "&apiKey=" + api_key);
    }

    @SuppressWarnings("unchecked")
    public Response<Ticker> get_ticker(Queryable query) throws ResponseException {
        final String response =
                get_with_key("/reference/tickers" + query.uri_query_string())
                        .body();

        try {
            return new Response<>((HashMap<String, Object>) new JSONParser(response).parse(), Ticker::new);
        } catch (JSONParser.MalformedJSONException e) {
            throw new ResponseException(400);
        }
    }

}
