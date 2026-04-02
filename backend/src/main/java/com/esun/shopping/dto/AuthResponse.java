package com.esun.shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String memberId;
    private String memberName;
    private String role;
}
