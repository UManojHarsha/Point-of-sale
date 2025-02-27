package com.pos.increff.model.data;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientData {
    private int id;
    private String name;
    private Date createdDate;
    private Date updatedDate;
    private String email ;
    private String contactNo ;
} 