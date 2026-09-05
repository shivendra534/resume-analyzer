package com.shivendra.resume_analyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @SuppressWarnings("unchecked")
    public List<Double> getEmbedding(String text) {
        try {
            String jsonBody = objectMapper.writeValueAsString(Map.of("inputs", text));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("EMBEDDING STATUS: " + response.statusCode());
            System.out.println("EMBEDDING RAW RESPONSE: " + response.body());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Embedding API returned status " + response.statusCode() + ": " + response.body());
            }

            return objectMapper.readValue(response.body(), List.class);

        } catch (Exception e) {
            System.out.println("EMBEDDING CALL FAILED: " + e.getMessage());
            throw new RuntimeException("Embedding API call failed: " + e.getMessage());
        }
    }

    public double cosineSimilarity(List<Double> vecA, List<Double> vecB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vecA.size(); i++) {
            dotProduct += vecA.get(i) * vecB.get(i);
            normA += Math.pow(vecA.get(i), 2);
            normB += Math.pow(vecB.get(i), 2);
        }

        if (normA == 0 || normB == 0) return 0.0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}