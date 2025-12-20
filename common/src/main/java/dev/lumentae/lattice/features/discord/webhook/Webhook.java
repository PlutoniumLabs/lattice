package dev.lumentae.lattice.features.discord.webhook;

import dev.lumentae.lattice.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Webhook {
    private final String endpoint;
    private final HttpClient client = HttpClient.newHttpClient();

    public Webhook(String endpoint) {
        this.endpoint = endpoint;
    }

    public void send(WebhookMessage message) {
        if (message == null) return;

        if (endpoint.isEmpty()) {
            System.out.println("Webhook endpoint is empty.");
            return;
        }

        String jsonPayload = String.format(
                "{\"content\": \"%s\", \"username\": \"%s\", \"avatar_url\": \"%s\"}",
                message.content(), message.username(), message.avatarUrl()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 429) {
                String retryAfter = response.headers().firstValue("Retry-After").orElse("0");
                Constants.LOG.warn("Rate limited. Retrying in {}s...", retryAfter);
                try {
                    Thread.sleep(Long.parseLong(retryAfter) * 1000);
                } catch (InterruptedException ignored) {
                }
                send(message);
            } else if (response.statusCode() != 204) {
                Constants.LOG.error("Failed to send message: {}", message.content());
                Constants.LOG.error("Response ({}): {}", response.statusCode(), response.body());
            }
        } catch (InterruptedException | IOException ignored) {
        }
    }
}