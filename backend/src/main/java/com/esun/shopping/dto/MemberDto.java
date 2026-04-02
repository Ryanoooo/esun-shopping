package com.esun.shopping.dto;

import lombok.Data;

@Data
public class MemberDto {
    private String memberId;
    private String memberName;
    private String email;
    private String phone;
    private String createdAt;
}
