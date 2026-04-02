package com.esun.shopping.service;

import com.esun.shopping.dto.MemberDto;
import com.esun.shopping.entity.Member;
import com.esun.shopping.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberDto getProfile(String memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("找不到會員");
        }
        MemberDto dto = new MemberDto();
        dto.setMemberId(member.getMemberId());
        dto.setMemberName(member.getMemberName());
        dto.setEmail(member.getEmail());
        dto.setPhone(member.getPhone());
        dto.setCreatedAt(member.getCreatedAt());
        return dto;
    }
}
