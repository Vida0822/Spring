package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of = "orderID") // Collectors에서 orderId를 기준으로 묶어줌
@AllArgsConstructor
public class OrderQueryDto {
    private Long orderID ;
    private String name ;
    private LocalDateTime orderDate ;
    private OrderStatus orderStatus ;
    private Address address ;
    private List<OrderItemQueryDto> orderItems ;

    public OrderQueryDto(Long orderID, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderID = orderID;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
