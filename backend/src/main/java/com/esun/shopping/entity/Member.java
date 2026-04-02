package com.esun.shopping.entity;

import lombok.Data;

@Data
public class Member {
    private String memberId;
    private String memberName;
    private String email;
    private String passwordHash;
    private String phone;
    private String role;
    private String createdAt;
}
