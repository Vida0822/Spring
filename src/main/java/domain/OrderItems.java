package domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItems {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id ;

    @ManyToOne
    @JoinColumn("item_id")
    private Item item ; // many가 OrderItems , one이 Item

    @ManyToOne
    @JoinColumn(name = "order_id") // 조인할 컬럼 !
    private Order order ;

    private int orderPrice ; // 주문 '당시의' 가격
    private int count ;  // 주문 수량

} // OrderItems
