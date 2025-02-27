package com.pos.increff.model.data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportData {
    String productName ;
    String clientName;
    String barcode;
    int quantity;
    double price;
}
