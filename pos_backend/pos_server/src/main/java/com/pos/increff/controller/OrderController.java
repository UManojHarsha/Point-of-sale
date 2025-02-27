package com.pos.increff.controller;

import com.pos.increff.api.ApiException;
import com.pos.increff.dto.OrdersDto;
import com.pos.commons.OrderItemsData;
import com.pos.commons.OrdersData;
import com.pos.commons.OrderStatus;
import com.pos.commons.client.InvoiceClient;
import com.pos.commons.client.AppClientException;
import com.pos.increff.model.form.CombinedOrdersForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import com.pos.commons.InvoiceGenerationRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrdersDto dto;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String INVOICE_SERVICE_URL = "http://localhost:9002";

    private InvoiceClient getInvoiceClient() {
        System.out.println("Creating InvoiceClient with URL: " + INVOICE_SERVICE_URL);
        return new InvoiceClient(INVOICE_SERVICE_URL, restTemplate);
    }
    

    @ApiOperation(value = "Creates a new order")
    @PostMapping(path = "/add")
    public ResponseEntity<?> add(@RequestBody CombinedOrdersForm form) throws ApiException {
        dto.add(form);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Gets list of all orders")
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) throws ApiException {
        return ResponseEntity.ok(dto.getAll(page, pageSize));
    }

    @ApiOperation(value = "Gets list of all order items")
    @RequestMapping(path = "/order-items/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<List<OrderItemsData>> getAllItems(@PathVariable int orderId) throws ApiException {
        return ResponseEntity.ok(dto.getAllItems(orderId));
   
    }

    @ApiOperation(value = "Generate invoice for an order")
    @PostMapping("/{orderId}/generateInvoice")
    public ResponseEntity<?> generateInvoice(@PathVariable Integer orderId) throws ApiException {
        String invoicePath = dto.generateInvoice(orderId);
        return ResponseEntity.ok("Invoice generated successfully");
    }

    @ApiOperation(value = "Download invoice for an order")
    @GetMapping("/{orderId}/downloadInvoice")
    public ResponseEntity<?> downloadInvoice(@PathVariable Integer orderId) throws ApiException {
        byte[] pdfBytes = dto.downloadInvoice(orderId);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"invoice_" + orderId + ".pdf\"")
                .body(resource);
    }
}