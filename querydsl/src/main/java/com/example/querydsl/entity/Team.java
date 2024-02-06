package com.example.querydsl.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 외 생성자 있으면 이거 따로 추가해줘야함 (jpa가 인식)
// 학습용이라 setter있는거. 원래 객체(data) cud 다 별도의 로직 메서드로 해줘야함
@ToString(of={"id", "name"})
public class Team {

    @Id @GeneratedValue
    private Long id ;

    private String name ;

    @OneToMany(mappedBy = "team") // 연관관계 거울 (vs 주인 : 그냥 @ManyToOne만 사용)
    // 참조되는 키 --> 아무것도 x
    private List<Member> members = new ArrayList<>(); // arrayList 생성(new)해야함

    public Team(String name){
        this.name = name ;
    }

}
