package com.example.querydsl.dto;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class MemberSearchCondition {
    // 검색 조건이 될 수 있는 애들을 정의해놓음 --> 여기 담아서 뽑아 쓸거임 (각각의 값으로 넘기고 받는게 아니라)

    // 회원명, 팀명, 나이 (ageGoe, ageLoe)
    private String username ;
    private String teamName ;
    private Integer ageGoe ; // 특정나이이상
    private Integer ageLoe ; // 특정나이이하
}
