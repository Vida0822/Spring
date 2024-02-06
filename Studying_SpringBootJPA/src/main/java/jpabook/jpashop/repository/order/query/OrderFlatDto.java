package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderFlatDto {
    // sql 조인의 결과를 그대로 가져올 수 있도록 구조 맞추기
    // 진짜 sql 조회 한줄을 그대로 담는 것 !

    // OrderQueryDto -->
    private Long orderID ;
    private String name ;
    private LocalDateTime orderDate ;
    private OrderStatus orderStatus ;
    private Address address ;

    // OrderItemQueryDto (원래 별개 객체의 컬렉션으로 붙여주었던 것) -->
    private String itemName ;
    private int orderPrice ;
    private int count ;

}
