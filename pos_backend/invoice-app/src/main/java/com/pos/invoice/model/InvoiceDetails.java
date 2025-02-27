package com.pos.invoice.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class InvoiceDetails {
    private Integer orderId;
    private Date orderDate;
    private String userEmail;
    private Double totalPrice;
    private List<InvoiceItem> items;
} 