package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id ;

    // @NotEmpty --> presentation 계층 (웹 계층, 화면 계층)의 로직이 entity에까지 들어가있음
    private String name ;

    @Embedded
    private Address address ;

    // @JsonIgnore  --> presentation 계층 (웹 계층, 화면 계층)의 로직이 entity에까지 들어가있음
    @OneToMany(mappedBy = "member") // 앞에 나오는게 현재 클래스, 뒤에 나오는게 이 칼럼과의 관계
    @JsonIgnore
    private List<Order> orders = new ArrayList<>() ;



} // Member
