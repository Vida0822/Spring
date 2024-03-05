package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager en ;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return en.createQuery(
                        //"select o from Order o "+ - jpa는 Entity나 Embeddable 객체만 클래스로 반환 가능 (Dto는 안됨)
                        //  => new 사용해서 반환해줘야함
                        "select new jpabook.jpashop.repository.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                                // 이렇게 하면 내가 원하는 칼럼만 select + 결과 dto에 담아줌 !
                                "from Order o "+
                                " join o.member m " +
                                " join o.delivery d ", OrderSimpleQueryDto.class)
                .getResultList() ;

    }
}
