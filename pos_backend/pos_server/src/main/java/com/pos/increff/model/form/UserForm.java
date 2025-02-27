package com.pos.increff.model.form;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;
@Getter
@Setter 
public class UserForm {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 1, message = "Password must be at least 8 characters")
    private String password;
}

