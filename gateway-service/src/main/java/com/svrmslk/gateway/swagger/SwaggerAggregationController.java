package com.svrmslk.gateway.swagger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class SwaggerAggregationController {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    @Value("${services.customer-service.url}")
    private String customerServiceUrl;

    @Value("${services.tenant-service.url}")
    private String tenantServiceUrl;

    @Value("${services.notification-service.url}")
    private String notificationServiceUrl;

    private final WebClient webClient;

    public SwaggerAggregationController() {
        this.webClient = WebClient.builder().build();
    }

    @GetMapping("/v3/api-docs")
    public Mono<ResponseEntity<Map<String, Object>>> getAggregatedApiDocs() {
        List<Mono<Map<String, Object>>> apiDocRequests = new ArrayList<>();

        apiDocRequests.add(fetchApiDocs(authServiceUrl, "auth-service"));
        apiDocRequests.add(fetchApiDocs(customerServiceUrl, "customer-service"));
        apiDocRequests.add(fetchApiDocs(tenantServiceUrl, "tenant-service"));
        apiDocRequests.add(fetchApiDocs(notificationServiceUrl, "notification-service"));

        return Mono.zip(apiDocRequests, results -> {
            Map<String, Object> aggregatedDocs = new HashMap<>();
            aggregatedDocs.put("openapi", "3.0.1");

            Map<String, Object> info = new HashMap<>();
            info.put("title", "Aggregated API Documentation");
            info.put("version", "1.0.0");
            info.put("description", "Combined API documentation for all microservices");
            aggregatedDocs.put("info", info);

            Map<String, Object> allPaths = new HashMap<>();
            Map<String, Object> allComponents = new HashMap<>();

            for (Object result : results) {
                if (result instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> serviceDoc = (Map<String, Object>) result;

                    Object pathsObj = serviceDoc.get("paths");
                    if (pathsObj instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> paths = (Map<String, Object>) pathsObj;
                        allPaths.putAll(paths);
                    }

                    Object componentsObj = serviceDoc.get("components");
                    if (componentsObj instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> components = (Map<String, Object>) componentsObj;
                        allComponents.putAll(components);
                    }
                }
            }

            aggregatedDocs.put("paths", allPaths);
            aggregatedDocs.put("components", allComponents);

            return ResponseEntity.ok(aggregatedDocs);
        }).onErrorResume(e -> {
            log.error("Error aggregating API docs: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        });
    }

    private Mono<Map<String, Object>> fetchApiDocs(String serviceUrl, String serviceName) {
        return webClient.get()
                .uri(serviceUrl + "/v3/api-docs")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnError(e -> log.warn("Failed to fetch API docs from {}: {}", serviceName, e.getMessage()))
                .onErrorReturn(new HashMap<>());
    }
}