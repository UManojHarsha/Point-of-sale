package com.pos.increff.model.form;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private Double price;
    
    @NotBlank(message = "Barcode cannot be empty")
    @Size(max = 255, message = "Barcode cannot exceed 255 characters")
    private String barcode;
    
    @NotBlank(message = "Client name cannot be empty")
    private String clientName;
}

