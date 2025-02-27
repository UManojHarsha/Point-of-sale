package com.pos.increff.model.form;

import com.pos.commons.OrderStatus;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.DecimalMin;

@Getter
@Setter
public class OrdersForm {
    @NotNull(message = "User ID cannot be null")
    @Min(value = 1, message = "Invalid user ID")
    private int userId;
    
    @NotNull(message = "Order date cannot be null")
    private Date orderDate;
    
    @NotNull(message = "Total price cannot be null")
    @DecimalMin(value = "0.0", message = "Total price cannot be negative")
    private double totalPrice;

    @NotNull(message = "Status cannot be null")
    private OrderStatus status;
}
