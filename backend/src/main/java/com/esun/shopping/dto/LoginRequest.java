package com.esun.shopping.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email不能為空")
    @Email(message = "Email格式不正確")
    private String email;

    @NotBlank(message = "密碼不能為空")
    private String password;
}
