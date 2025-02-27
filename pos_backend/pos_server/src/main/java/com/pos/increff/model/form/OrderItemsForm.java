package com.pos.increff.model.form;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemsForm {
    @NotNull(message = "Product ID cannot be null")
    @Min(value = 1, message = "Invalid product ID")
    private int productId;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private Double price;
}
