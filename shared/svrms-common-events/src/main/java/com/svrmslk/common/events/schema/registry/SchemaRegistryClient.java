package com.svrmslk.common.events.schema.registry;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Client for interacting with an external schema registry (e.g., Confluent Schema Registry).
 * Provides caching and fallback mechanisms for production resilience.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class SchemaRegistryClient {

    private static final Logger log = LoggerFactory.getLogger(SchemaRegistryClient.class);

    private final String registryUrl;
    private final RestTemplate restTemplate;
    private final java.util.Map<String, JsonNode> schemaCache;

    public SchemaRegistryClient(String registryUrl, RestTemplate restTemplate) {
        this.registryUrl = registryUrl;
        this.restTemplate = restTemplate;
        this.schemaCache = new java.util.concurrent.ConcurrentHashMap<>();

        log.info("SchemaRegistryClient initialized: url={}", registryUrl);
    }

    /**
     * Fetches a schema from the registry.
     *
     * @param eventType the event type
     * @param version the schema version (optional)
     * @return the schema if found
     */
    public Optional<JsonNode> getSchema(String eventType, String version) {
        String cacheKey = eventType + ":" + (version != null ? version : "latest");

        // Check cache first
        JsonNode cached = schemaCache.get(cacheKey);
        if (cached != null) {
            log.debug("Schema found in cache: eventType={}, version={}", eventType, version);
            return Optional.of(cached);
        }

        try {
            String url = buildSchemaUrl(eventType, version);
            log.debug("Fetching schema from registry: url={}", url);

            JsonNode schema = restTemplate.getForObject(url, JsonNode.class);

            if (schema != null) {
                schemaCache.put(cacheKey, schema);
                log.info("Schema fetched and cached: eventType={}, version={}", eventType, version);
                return Optional.of(schema);
            }

        } catch (Exception ex) {
            log.error("Failed to fetch schema from registry: eventType={}, version={}",
                    eventType, version, ex);
        }

        return Optional.empty();
    }

    /**
     * Registers a new schema in the registry.
     *
     * @param eventType the event type
     * @param schema the schema to register
     * @return the schema ID if successful
     */
    public Optional<Integer> registerSchema(String eventType, JsonNode schema) {
        try {
            String url = registryUrl + "/subjects/" + eventType + "/versions";
            log.debug("Registering schema in registry: url={}, eventType={}", url, eventType);

            SchemaRegistrationRequest request = new SchemaRegistrationRequest(schema.toString());
            SchemaRegistrationResponse response = restTemplate.postForObject(
                    url, request, SchemaRegistrationResponse.class);

            if (response != null && response.id != null) {
                log.info("Schema registered successfully: eventType={}, schemaId={}",
                        eventType, response.id);
                return Optional.of(response.id);
            }

        } catch (Exception ex) {
            log.error("Failed to register schema in registry: eventType={}", eventType, ex);
        }

        return Optional.empty();
    }

    /**
     * Clears the schema cache.
     */
    public void clearCache() {
        schemaCache.clear();
        log.info("Schema cache cleared");
    }

    /**
     * Builds the schema URL for fetching.
     */
    private String buildSchemaUrl(String eventType, String version) {
        if (version != null) {
            return registryUrl + "/subjects/" + eventType + "/versions/" + version;
        } else {
            return registryUrl + "/subjects/" + eventType + "/versions/latest";
        }
    }

    /**
     * Request payload for schema registration.
     */
    private record SchemaRegistrationRequest(String schema) {}

    /**
     * Response payload from schema registration.
     */
    private record SchemaRegistrationResponse(Integer id) {}
}