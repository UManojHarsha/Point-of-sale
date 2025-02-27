package com.pos.increff.model.form;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ReportForm {
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String productName;
    
    @Size(max = 255, message = "Client name cannot exceed 255 characters")
    private String clientName;
    
    @Size(max = 255, message = "Barcode cannot exceed 255 characters")
    private String barcode;
    
    private Date fromDate;
    
    private Date toDate;
} 