package com.bot.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestClient {

    private final HttpClient http_client = HttpClient.newHttpClient();
    private final String host;

    public RestClient(String host) {
        this.host = host;
    }

    public HttpResponse<String> get(String path) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + host + path))
                .GET()
                .build();

        return http_client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
