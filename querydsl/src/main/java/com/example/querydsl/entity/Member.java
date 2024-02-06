package com.example.querydsl.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id", "username", "age"})
// ※ team 넣으면 안됨 : 그럼 team 타고 들어가 읽으려하면 또 멤버있고 그 멤버 타고들어가면 또 팀있고.... (무한루프)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id ;

    private String username ;

    private int age ;

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계 주인 (java 입장)
    @JoinColumn(name = "team_id") // 참조키 -- 외래키 생성 (참조되는 키 : 아무것도 안함)
    private Team team ;

    public Member(String username){
        this(username, 0, null) ;
    }
    public Member(String username , int age ){
        this(username, age, null) ;
    }
    public Member(String username, int age , Team team){
        this.username = username ;
        this.age = age;
        if(team != null){
            setTeam(team) ; // 연관관계 메서드 호출
        }
    }

    // 연관관계 메서드
    private void setTeam(Team team) {
        this.team = team ;
        team.getMembers().add(this) ;
    } // changeTeam
} // class
