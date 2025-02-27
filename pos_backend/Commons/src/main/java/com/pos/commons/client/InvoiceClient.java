package com.pos.commons.client;
import com.pos.commons.OrderItemsData;
import com.pos.commons.OrdersData;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.pos.commons.api.ApiException;
import com.pos.commons.InvoiceGenerationRequest;


public class InvoiceClient extends AbstractAppClient {

    private static final String GENERATE_INVOICE_URL = "/api/invoice/{orderId}/generate";
    private static final String DOWNLOAD_INVOICE_URL = "/api/invoice/{orderId}/download";

    public InvoiceClient(String baseUrl, RestTemplate restTemplate) {
        super(baseUrl, restTemplate);
    }

    //TODO : Clean and split up the function
    public String generateInvoice(Integer orderId, InvoiceGenerationRequest requestBody) throws AppClientException {
        if (orderId == null || orderId < 0) {
            throw new AppClientException("Invalid order ID: " + orderId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create path variables map
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("orderId", orderId.toString());

        // System.out.println("Making request to: " + baseUrl + GENERATE_INVOICE_URL.replace("{orderId}", orderId.toString()));
        // System.out.println("Order ID: " + orderId);
        // System.out.println("Request body: " + requestBody);

        try {
            // Make the request and get the invoice path
            return makeRequest(
                HttpMethod.POST,
                GENERATE_INVOICE_URL,
                headers,
                null, // No query params needed
                requestBody,
                String.class,
                pathVars
            );
        } catch (AppClientException e) {
            throw new AppClientException("Failed to generate invoice: " + e.getMessage(), e);
        }
    }


    //TODO : Clean up this function
    public byte[] downloadInvoice(Integer orderId, String invoicePath) throws AppClientException {
        if (orderId == null || orderId < 0) {
            throw new AppClientException("Invalid order ID: " + orderId);
        }
        if (invoicePath == null || invoicePath.trim().isEmpty()) {
            throw new AppClientException("Invoice path cannot be empty");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("orderId", orderId.toString());

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("invoicePath", invoicePath);

        try {
            // Make the request
            return makeRequest(
                HttpMethod.GET,
                DOWNLOAD_INVOICE_URL,
                headers,
                queryParams,
                null,
                byte[].class,
                pathVars
            );
        } catch (AppClientException e) {
            throw new AppClientException("Failed to download invoice: " + e.getMessage(), e);
        }
    }
} 