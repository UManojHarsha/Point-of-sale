package com.pos.increff.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductData {
    private Integer id;
    private String name;
    private Double price ;
    private String barcode;
    private String clientName ;
    private Integer clientId ;
    private String clientEmail ;
}