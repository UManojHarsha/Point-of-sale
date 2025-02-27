package com.pos.increff.model.form;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

@Getter
@Setter
public class InventoryForm {
    @NotNull(message = "Product barcode cannot be null")
    private int productId;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be negative")
    private int totalQuantity;
}
