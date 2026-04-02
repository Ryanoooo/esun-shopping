package com.esun.shopping.controller;

import com.esun.shopping.common.ApiResponse;
import com.esun.shopping.dto.MemberDto;
import com.esun.shopping.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberDto>> getProfile(
            @AuthenticationPrincipal String memberId) {
        MemberDto dto = memberService.getProfile(memberId);
        return ResponseEntity.ok(ApiResponse.success("查詢成功", dto));
    }
}
