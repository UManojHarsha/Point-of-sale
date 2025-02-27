package com.pos.invoice.controller;

import com.pos.commons.OrderStatus;
import com.pos.invoice.dto.InvoiceDto;
import com.pos.invoice.flow.InvoiceFlow;
import com.pos.commons.api.ApiException;
import com.pos.commons.OrdersData;
import com.pos.commons.OrderItemsData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Api
@RestController
@RequestMapping("/api/invoice")
//@Component("invoiceAppController")
public class InvoiceController {

    @Autowired
    @Qualifier("invoiceAppFlow")
    private InvoiceFlow flow;

    @Autowired
    private InvoiceDto dto;


    @ApiOperation(value = "Generate invoice for an order")
    @PostMapping("/{orderId}/generate")
    public ResponseEntity<String> generateInvoice(
            @PathVariable Integer orderId,
            @RequestBody InvoiceGenerationRequest request) {
        try {
            //TODO: Send the reuest form to dto layer
            String invoicePath = dto.generateInvoice(orderId, request.getOrder(), request.getOrderItems());
            return ResponseEntity.ok(invoicePath);
        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating invoice: " + e.getMessage());
        }
    }

    @ApiOperation(value = "Download invoice for an order")
    @GetMapping("/{orderId}/download")
    public ResponseEntity<?> downloadInvoice(@PathVariable Integer orderId, @RequestParam String invoicePath) {
        try {
            Path path = Paths.get(invoicePath);
            Resource resource = new UrlResource(path.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"invoice_" + orderId + ".pdf\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading invoice: " + e.getMessage());
        }
    }

    // Helper class to match the request from InvoiceClient
    private static class InvoiceGenerationRequest {
        private OrdersData order;
        private List<OrderItemsData> orderItems;

        // Getters and setters
        public OrdersData getOrder() {
            return order;
        }

        public void setOrder(OrdersData order) {
            this.order = order;
        }

        public List<OrderItemsData> getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(List<OrderItemsData> orderItems) {
            this.orderItems = orderItems;
        }
    }
} 