package com.example.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    // 다른 Member의 다른 필드는 받지 않고 딱 얘네 두개만 다루는 dto
    private String username;
    private int age;

    @QueryProjection // 이 dto도 Q 파일을 만들어줌
    public MemberDto(String username, int age ){
        this.username = username ;
        this.age = age;
    }
    // 문제 : 이 dto 자체가 querydsl(jpql 대체용 jpa; repository 계층 소속)과의
    //         연관성(의존성)이 생김
    // dto는 여러 layer에 걸쳐서 돌아다니는데 repository의 querydsl과 관련되어있으면 (순수하지 x)
    // + querydsl 안쓰기로해서 의존성 빼면 저거 다 오류...
    // Q파일을 만든다는건 사실상 dto로 테이블을 만든다는 의미 (영속성 객체처럼 다룸 )



}
