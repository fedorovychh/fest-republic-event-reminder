package io.service.events.common;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Component
public class WebClient {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public Optional<String> parseResource(String resource) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(resource))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
        return Optional.of(response.body());
    }
}
