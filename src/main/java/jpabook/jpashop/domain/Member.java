package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id ;

    private String name ;

    @Embedded
    private Address address ;

    @OneToMany(mappedBy = "member") // 앞에 나오는게 현재 클래스, 뒤에 나오는게 이 칼럼과의 관계
    private List<Order> orders = new ArrayList<>() ;



} // Member
