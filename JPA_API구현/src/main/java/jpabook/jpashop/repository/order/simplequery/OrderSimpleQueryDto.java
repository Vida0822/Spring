package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    // 여기에 안만들면 Repository --> Controller 참조하는 이상한 연관관계 발생...

    private Long orderId;
    private String name ;
    private LocalDateTime orderDate ;
    private OrderStatus orderStatus ;
    private Address address ;

    //public OrderSimpleQueryDto(Order order){
    // Entity를 직접 받을 수 없음 ! jpa는 o를 식별자로 넣어버려서?? --> 직접 다 받아야함
    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address ){
        this.orderId = orderId ;
        this.name= name ; //LAZY 초기화 : 영속성 컨텍스트에 없으니 db에 쿼리 날림
        this.orderDate = orderDate ;
        this.orderStatus = orderStatus ;
        this.address = address ; // LAZY 초기화 : 영속성 컨텍스트에 없으니 db에 쿼리 날림
    }

}
