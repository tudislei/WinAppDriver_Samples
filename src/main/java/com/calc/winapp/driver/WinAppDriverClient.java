package com.calc.winapp.driver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class WinAppDriverClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    public WinAppDriverClient(String host, int port) {
        this.baseUrl = "http://" + host + ":" + port;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    // ─── Raw HTTP ─────────────────────────────────────────────

    public JsonObject post(String path, Map<String, Object> body) throws Exception {
        String json = gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String bodyStr = response.body();

        if (bodyStr == null || bodyStr.isBlank()) {
            throw new RuntimeException("Empty response from WinAppDriver at " + baseUrl + path);
        }

        return JsonParser.parseString(bodyStr).getAsJsonObject();
    }

    public JsonObject get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}