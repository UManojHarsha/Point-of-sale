package com.pos.commons.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

public abstract class AbstractAppClient {
    
    //private static final Logger logger = LoggerFactory.getLogger(AbstractAppClient.class);
    protected final String baseUrl;
    protected final RestTemplate restTemplate;

    protected AbstractAppClient(String baseUrl, RestTemplate restTemplate) {
        // Remove trailing slash from baseUrl if present
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        System.out.println("Initialized client with base URL: " + this.baseUrl);
        this.restTemplate = restTemplate;
    }

    protected <T, R> R makeRequest(
            HttpMethod method,
            String path,
            HttpHeaders headers,
            MultiValueMap<String, String> queryParams,
            T body,
            Class<R> responseType,
            Map<String, String> pathVariables) throws AppClientException {
        try {
            // Build URL with query parameters and path variables
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
            
            // Add path, ensuring it starts with a slash
            String cleanPath = path.startsWith("/") ? path : "/" + path;
            builder.path(cleanPath);
            
            if (queryParams != null) {
                builder.queryParams(queryParams);
            }

            String url = builder.buildAndExpand(pathVariables).toUriString();

            System.out.println("Base URL: " + baseUrl);
            System.out.println("Path: " + cleanPath);
            System.out.println("Final URL: " + url);
            System.out.println("Request method: " + method);
            System.out.println("Request headers: " + headers);
            System.out.println("Request body: " + body);
            System.out.println("Path variables: " + pathVariables);
            
            // Create request entity
            HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);

            // Make the request
            ResponseEntity<R> response = restTemplate.exchange(
                url,
                method,
                requestEntity,
                responseType
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AppClientException("Request failed with status: " + response.getStatusCode());
            }

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new AppClientException("Client error: " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            throw new AppClientException("Server error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            System.err.println("Error making request: " + e.getMessage());
            e.printStackTrace();
            throw new AppClientException("Error making request to " + path + ": " + e.getMessage(), e);
        }
    }

    // Overload for requests without path variables
    protected <T, R> R makeRequest(
            HttpMethod method,
            String path,
            HttpHeaders headers,
            MultiValueMap<String, String> queryParams,
            T body,
            Class<R> responseType) throws AppClientException {
        return makeRequest(method, path, headers, queryParams, body, responseType, null);
    }
} 