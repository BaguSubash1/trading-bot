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
        super("api.massive.com", "");

        api_key_query = new Queryable() {
            String apiKey = api_key;
        };
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
            final String response = get("/v3/reference/tickers" + query.uri_query_string(api_key_query)).body();
            return JSON.parse(response);
        });
    }

    public JSON<?> get_ticker(String ticker, Queryable query) throws QueryException {
        return intercept_exceptions(() -> {
            final String response = get("/v3/reference/tickers/" + ticker + query.uri_query_string(api_key_query)).body();
            return JSON.parse(response);
        });
    }

    public enum TimeSpan {

        Second("second"),
        Minute("minute"),
        Hour("hour"),
        Day("day"),
        Week("week"),
        Month("month"),
        Quarter("quarter"),
        Year("year");

        public final String string;

        TimeSpan(String value) {
            this.string = value;
        }

    }

    public JSON<?> get_custom_bars(String ticker, int multiplier, TimeSpan timespan, String from, String to,
                                   Queryable query) throws QueryException {
        return intercept_exceptions(() -> {
            final String response = get(
                    "/v2/aggs/ticker/" + ticker + "/range/" + multiplier + "/" + timespan.string + "/" + from + "/" + to +
                    query.uri_query_string(api_key_query)).body();
            return JSON.parse(response);
        });
    }

}
