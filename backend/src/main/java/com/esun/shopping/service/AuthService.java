package com.esun.shopping.service;

import com.esun.shopping.common.JwtUtil;
import com.esun.shopping.dto.AuthResponse;
import com.esun.shopping.dto.LoginRequest;
import com.esun.shopping.dto.RegisterRequest;
import com.esun.shopping.entity.Member;
import com.esun.shopping.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(MemberRepository memberRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (memberRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("此Email已被註冊");
        }

        String memberId = generateMemberId();
        String passwordHash = passwordEncoder.encode(request.getPassword());

        memberRepository.insertMember(
            memberId,
            request.getMemberName(),
            request.getEmail(),
            passwordHash,
            request.getPhone()
        );

        String token = jwtUtil.generateToken(memberId, "MEMBER");
        return new AuthResponse(token, memberId, request.getMemberName(), "MEMBER");
    }

    public AuthResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail());
        if (member == null || !passwordEncoder.matches(request.getPassword(), member.getPasswordHash())) {
            throw new IllegalArgumentException("Email或密碼錯誤");
        }

        String token = jwtUtil.generateToken(member.getMemberId(), member.getRole());
        return new AuthResponse(token, member.getMemberId(), member.getMemberName(), member.getRole());
    }

    private String generateMemberId() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = new Random().nextInt(9000) + 1000;
        return "M" + date + random;
    }
}
