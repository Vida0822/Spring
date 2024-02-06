package com.example.querydsl.controller;

import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDto;
import com.example.querydsl.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    // 서비스는 x
    private final MemberJpaRepository memberJpaRepository ;

    @GetMapping("/vl/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition){ // 주고받을땐 dto로 !!

        return memberJpaRepository.searchByBuilder(condition) ; // 바로 repository 호출
    }
}
