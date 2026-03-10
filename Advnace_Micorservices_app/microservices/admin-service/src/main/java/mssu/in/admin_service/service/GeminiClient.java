package mssu.in.admin_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Client for OpenRouter API.
 */
@Service
public class GeminiClient {

    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${ai.api.key}") String apiKey,
            @Value("${ai.model}") String model) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    /**
     * Sends a plain text prompt via OpenRouter and returns the content.
     */
    public String generateText(String prompt) {
        try {
            String url = "https://openrouter.ai/api/v1/chat/completions";

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "http://localhost:9104"); // Optional
            headers.set("X-Title", "TicketHub Admin Service"); // Optional

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            String raw = restTemplate.postForObject(url, request, String.class);
            if (raw == null) {
                logger.warn("OpenRouter response was null");
                return null;
            }

            JsonNode root = objectMapper.readTree(raw);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                logger.warn("No choices in OpenRouter response: {}", raw);
                return null;
            }

            JsonNode messageNode = choices.get(0).path("message").path("content");
            return messageNode.isMissingNode() ? null : messageNode.asText();

        } catch (Exception ex) {
            logger.error("Error calling OpenRouter API", ex);
            return null;
        }
    }
}
