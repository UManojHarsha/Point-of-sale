package com.pos.invoice.model;

import lombok.Data;

@Data
public class InvoiceItem {
    private String productName;
    private Integer quantity;
    private Double price;
    private Double total;
} 