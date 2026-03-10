package mssu.in.api_gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayHealthController {

    private final WebClient webClient;

    public GatewayHealthController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "API Gateway");
        info.put("status", "UP");
        info.put("timestamp", LocalDateTime.now().toString());
        info.put("endpoints", Map.of(
                "auth", "/api/auth/**",
                "users", "/api/users/**",
                "tickets", "/api/tickets/**",
                "customer", "/api/customer/**",
                "messages", "/api/messages/**",
                "admin", "/api/admin/**",
                "executive", "/api/executive/**"
        ));
        return ResponseEntity.ok(info);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "API Gateway"
        ));
    }

    @GetMapping("/services/status")
    public Mono<Map<String, Object>> servicesStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", LocalDateTime.now().toString());

        return Mono.zip(
                checkService("http://localhost:9101/actuator/health", "auth-user-service"),
                checkService("http://localhost:9102/actuator/health", "ticket-service"),
                checkService("http://localhost:9103/actuator/health", "customer-service"),
                checkService("http://localhost:9104/actuator/health", "admin-service"),
                checkService("http://localhost:9105/actuator/health", "executive-service")
        ).map(tuple -> {
            Map<String, Object> services = new HashMap<>();
            services.put("auth-user-service", tuple.getT1());
            services.put("ticket-service", tuple.getT2());
            services.put("customer-service", tuple.getT3());
            services.put("admin-service", tuple.getT4());
            services.put("executive-service", tuple.getT5());
            status.put("services", services);
            return status;
        });
    }

    private Mono<Map<String, Object>> checkService(String url, String serviceName) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "UP");
                    result.put("name", serviceName);
                    return result;
                })
                .onErrorResume(e -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "DOWN");
                    result.put("name", serviceName);
                    result.put("error", e.getMessage());
                    return Mono.just(result);
                });
    }
}
