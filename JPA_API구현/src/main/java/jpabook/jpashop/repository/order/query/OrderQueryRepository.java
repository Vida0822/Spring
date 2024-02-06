package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository { // 화면 로직이 섞여있는 db 조회, 비즈니스 로직
    // 장점 : select 되는 데이터 ↓
    private final EntityManager em;

    /*
    1. 1+n
     */
    public List<OrderQueryDto> findOrderQueryDto() {
        List<OrderQueryDto> result = getOrders(); // 컬렉션 값 안채워져 있음 --> 하나씩 넣어줘야함

        result.forEach(o->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderID()) ;
            // 각 Order 별로 Item 찾아냄 (where oi.order.id = :orderId) x Order 갯수
            //     --> 1:n 문제 발생
            o.setOrderItems(orderItems);
        } );
        // n+1 문제 발생
        return result ;
    } // findOrderQueryDto

    private List<OrderItemQueryDto> findOrderItems(Long orderID) {

        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count )" +
                                "from OrderItem oi "+
                                "join oi.item i "+
                                "where oi.order.id = :orderId " , OrderItemQueryDto.class)
                .setParameter("orderId", orderID)
                .getResultList() ;
    } // findOrderItems

    private List<OrderQueryDto> getOrders() {
        return em.createQuery(
                        // Entity 같은 경우 이미 있는 영속성 객체를 가져오는 것이기 때문에 new 를 할필요가 없지만 DTO 는 새로 생성해야함
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate,o.status, d.address) " +
                                // jpql 에선 컬렉션을 넣을 수 없음 (flat 하게 한줄밖에 못넣음) (queryDSL은 가능)
                                "from Order o " +
                                "join o.member m " +
                                "join o.delivery d ", OrderQueryDto.class)
                .getResultList();
    } // getOrders

    /*
    2.1+1
     */
    public List<OrderQueryDto> findOrderQueryDto_opt() {
        List<OrderQueryDto> orders = getOrders();

        List<Long> orderIds = orders.stream()
                .map(o -> o.getOrderID())
                .collect(Collectors.toList()); // Order Id들 뽑힘

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count )" +
                                "from OrderItem oi " +
                                "join oi.item i " +
                                "where oi.order.id in :orderIds ", OrderItemQueryDto.class) // 뽑힌 Order Id들 한번에 들어감
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        orders.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderID()))); // memory에 올려둔 Map에서 OrderId 기준으로 최적화

        return orders ;
    } // findOrderQueryDto_opt

    /*
    3. 한방 쿼리 - 1번
     */
    public List<OrderFlatDto> findOrderQueryDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate , o.status, d.address, i.name, oi.orderPrice, oi.count)"+
                        "from Order o "+
                        "join o.member m "+
                        "join o.delivery d "+
                        "join o.orderItems oi "+
                        "join oi.item i ", OrderFlatDto.class
        ).getResultList();
        /*
        주문쪽 데이터가 중복으로 들어갈 수 밖에 없음 ! => 페이징 불가
         */
    }



}
