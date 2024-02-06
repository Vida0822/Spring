package com.example.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberTeamDto {  // 검색 결과로 받을 dto
    // 검색 요구사항 : member와 팀정보 중 여러가지 섞어서 가져올거임
    // => 그 가져올것만 정확하게 필드로 정의해놓은 dto 사용
    // => "쿼리 사용(조회) 결과로 dto를 받겠다"


    private Long memberID ;
    private String username ;
    private int age ;
    private Long teamId ;
    private String teamName ;

    @QueryProjection // 이건 querydls 조회결과로 dto를 받으려할건데 사용할 객체야
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName){
        this.memberID = memberId ;
        this.username = username ;
        this.age = age ;
        this.teamId = teamId ;
        this.teamName = teamName ;
    }






}
