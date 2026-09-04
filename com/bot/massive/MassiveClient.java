package com.bot.massive;

import com.bot.util.JSON;
import com.bot.util.JSONParser;
import com.bot.util.Queryable;
import com.bot.util.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class MassiveClient extends RestClient {

    private final Queryable api_key_query;

    public MassiveClient(String api_key) {
        super("api.massive.com", "/v3");

        api_key_query = new Queryable() {
            String apiKey = api_key;
        };
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

    public static class QueryException extends Exception {
        public QueryException(String message, Exception parent) {
            super(message);
            this.setStackTrace(parent.getStackTrace());
        }
    }

    private interface ExceptionInterceptor<T> {
        T run() throws ResponseException, JSONParser.MalformedJSONException;
    }

    private static <T> T intercept_exceptions(ExceptionInterceptor<T> interceptor) throws QueryException {
        try {
            return interceptor.run();
        } catch (ResponseException exception) {
            throw new QueryException("http status: " + exception.status, exception);
        } catch (JSONParser.MalformedJSONException exception) {
            throw new QueryException("JSON parsing failed (likely malformed data)", exception);
        }
    }

    public JSON<?> query_tickers(Queryable query) throws QueryException {
        return intercept_exceptions(() -> {
            final String response = get("/reference/tickers" + query.uri_query_string(api_key_query)).body();
            return JSON.parse(response);
        });
    }

    public JSON<?> get_ticker(String ticker, Queryable query) throws QueryException {
        return intercept_exceptions(() -> {
            final String response = get("/reference/tickers/" + ticker + query.uri_query_string(api_key_query)).body();
            return JSON.parse(response);
        });
    }

}
