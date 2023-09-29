package jpabook.jpashop.domain;

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
    private Item item ; // many가 OrderItems , one이 Item

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // 조인할 컬럼 !
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
    주문 취소시 주문 아이템 테이블에서도 다시 주문된 아이템 목록 삭제
     */
    public void cancel() {
        getItem().addStock(count); //  getItem() - 객체 생략 = 이 객체의 Item
        // Item 클래스의 addStock 메서드 호출 ! 편리 --> 바로 Item 클래스 안에 있음
    }

    // ** 조회 로직 ** //
    /*
    주문 상품 전체 가격조회
     */
    public int getTotalPrice() {
        return getOrderPrice()*getCount();
    }
} // OrderItems
