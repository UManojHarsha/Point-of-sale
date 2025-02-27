package com.pos.increff.model.data;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class DaySalesData {
    private Integer id;
    private Date date;
    private int orderCount;
    private int productCount;
    private double revenue;
} 