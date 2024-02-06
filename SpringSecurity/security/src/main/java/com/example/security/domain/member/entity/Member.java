package com.example.security.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identity - 숫자 자동 증가 : 레코드가 초가될 때마다 해당 칼럼의 숫자를 자동으로 증가시키는 제약조건
    /*
    CREATE TABLE MEMBER(
        ID NUMBER IDENTITY(1,1) NOT NULL  -- 초기값, 증가값 --
    )
    */
    private Long memberID ;

    private String name ;

    private String nickname ;

    private String birthDay ;

    @Column(name = "EMAIL")
    private String email ;

    @Column(name = "PASSWORD")
    private String password ;

    @Enumerated(EnumType.STRING)
    private Authority authority ;


}
