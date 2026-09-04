package com.bot.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestClient {

    private final HttpClient http_client = HttpClient.newHttpClient();
    private final String base;

    public RestClient(String host, String base_path) {
        base = "https://" + host + base_path;
    }

    public static class ResponseException extends Exception {

        public final int status;

        public ResponseException(int status) {
            this.status = status;
        }

    }

    public HttpResponse<String> get(String path) throws ResponseException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .GET()
                .build();

        final HttpResponse<String> response;
        try {
            response = http_client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ResponseException(500);
        }

        if(response.statusCode() != 200) {
            throw new ResponseException(response.statusCode());
        }

        return response;
    }

}
