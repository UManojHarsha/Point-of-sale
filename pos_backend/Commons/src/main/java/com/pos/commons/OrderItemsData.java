package com.pos.commons;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter 
public class OrderItemsData {
    private Integer id;
    private Integer orderId;
    private String productName;
    private Integer quantity;
    private Double price;
}