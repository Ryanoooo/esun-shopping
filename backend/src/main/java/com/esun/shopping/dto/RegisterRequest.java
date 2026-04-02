package com.esun.shopping.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "會員姓名不能為空")
    @Size(max = 50, message = "姓名最多50個字元")
    private String memberName;

    @NotBlank(message = "Email不能為空")
    @Email(message = "Email格式不正確")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, max = 100, message = "密碼長度需在6~100字元")
    private String password;

    @Size(max = 20, message = "電話最多20個字元")
    private String phone;
}
