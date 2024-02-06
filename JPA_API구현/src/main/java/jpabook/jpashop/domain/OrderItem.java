package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "item_id")
    // @JsonIgnore 필요 x : Item에선 다시 Order를 참조하는게 없으므로 양방향이 아님
    private Item item ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore // 양방향 관계에선 이거 걸어줘야함 !
    private Order order ;

    private int orderPrice ; // 주문 '당시의' 가격
    private int count ;  // 주문 수량

    /*
    protected OrderItem(){
    }
    */

    // ** 생성 메서드 **//
    // 실제 이걸 호출하는건 서비스 계층이 되겠지?
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    } // createOrderItem

    // ** 비즈니스 로직 **//
    /*
    주문 취소
     */
    public void cancel() {
        getItem().addStock(count);
    }

    // ** 조회 로직 ** //
    /*
    주문 상품 전체 가격조회
     */
    public int getTotalPrice() {
        return getOrderPrice()*getCount();
    }
} // OrderItems
