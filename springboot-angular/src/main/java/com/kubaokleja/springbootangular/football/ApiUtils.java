package com.kubaokleja.springbootangular.football;

import com.kubaokleja.springbootangular.exception.FootballApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
class ApiUtils {

    private static final String X_AUTH_TOKEN = "X-Auth-Token";

    @Value("${football.api.token}")
    private String apiToken;

    HttpResponse<String> getResponse(String uri) throws FootballApiException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header(X_AUTH_TOKEN, apiToken)
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FootballApiException("External API does not respond.");
        }
    }
}
