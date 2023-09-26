package domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    // column 이름 줘야함 for mapping ! 아님 진짜 'id'라는 컬럼명으로 매핑 조회? (아마 )
    private Long id ;

    private String name ;

    @Embedded // 내장 타입을 사용했다
    private Address address ;

    @OneToMany(mappedBy = "member") // 앞에 나오는게 현재 클래스, 뒤에 나오는게 이 칼럼과의 관계
    private List<Order> orders = new ArrayList<>() ;
    // 여기에 어떤 값을 넣는다고 해서 foreign 키 값이 변경되지 않음
    /*
    양방향 관계
    --> 연관관계의 주인을 정해줘야함
    (Member에서 값을 바꿀 수도 , Order 에서 값을 바꿀 수도 있으니까)
    ㄴ "Member와 Order의 필드 중 어디의 값이 변경되었을 때 FK 를 바꿔야 (반영해야)하지?
        (만약 두 값이 다르다면 ? )

       ==> 객체는 변경 포인트가 두개지만, 테이블에선 변경 포인트가 하나인 상황 (FK)
       --> 둘 중 어느 값이 값을 변경하는 주체가 될 수 있을지 '주인'을 정해줘야함 : '연관관계의 주인'
            ㄴ 연관관계의 주인 : FK 에 가까운곳
             (Order 테이블에 FK가 있음 --> Order 클래스의 Member 를 연관관계의 주인으로 )

    ==> Order (주인) : "난 주인이에요" --> 아무것도 안해도 됨
    ==> Member(주인 x) : "난 거울이에요"    @OneToMany(mappedBy = "member") : order 테이블에 있는 변경된 member_id값이 반영됨
     */


} // Member
