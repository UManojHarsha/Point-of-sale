package com.pos.commons;

import java.util.Date;

import lombok.Data;

@Data
public class OrdersData {
    private Integer id;
    private String userEmail;
    private Double totalPrice;
    private Date updatedDate;
    private String invoicePath;
    private OrderStatus status;
}