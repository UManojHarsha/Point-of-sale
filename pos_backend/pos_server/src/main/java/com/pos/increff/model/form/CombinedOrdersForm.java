package com.pos.increff.model.form;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class CombinedOrdersForm {
    @NotBlank(message = "User email cannot be empty")
    @Email(message = "Invalid email format")
    private String userEmail;
    
    @NotNull(message = "Total price cannot be null")
    @Min(value = 0, message = "Total price cannot be negative")
    private Double totalPrice;
    
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemsForm> orderItems;
}
