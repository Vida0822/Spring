package domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id ;

    @ManyToOne // 다대일 중 다쪽이 주문 --> Orders(many) To Member(one)
    @JoinColumn(name = "member_id")
    // join할 컬럼 : member_id (foreign key 이름이 member_id 가 됨)
    // fk 가 Order 테이블에 생김 (not Member 테이블)
    private Member member ;

    @OneToMany(mappedBy = "order") // 연관관계 거울 (OrderItems의 필드 order에 의해 mapping 이 됨 )
    @JoinColumn
    private List<OrderItems> orderItems = new ArrayList<>() ;

    @OneToOne
    // foreign key 어디에 둘까?  --> access를 많이 하는데에 둠 : order를 보면서 delivery를 봄 (delevery를 찾을 때 order을 볼일 보다)
    // --> 그래서 orders에 delivery_Id를 Order 테이블의 fk로 둠 (not Delivery 테이블)
    @JoinColumn(name = "delevery_id") // fk가 Order 테이블에 생김
    private Deleivery deleivery ;

//  private Date date ; --> 그냥 Date 타입으로 하면 annotaion으로 매핑 시켜줘야함
    private LocalDateTime orderDate ; // 근데 localDate 타입으로 하면 hibernate가 자동으로 매핑 (java 8)
    private OrderStatus status ; // 주문 상태  - Enum 타입




} // class
