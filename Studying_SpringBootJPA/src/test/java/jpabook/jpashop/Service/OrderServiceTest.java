package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired  EntityManager en ;
    @Autowired  OrderService orderService ;
    @Autowired  OrderRepository orderRepository ;

    @Test
    public void 상품주문() throws Exception{
        //given
        // 엔티티 조회
        Member member = createMember(); // extract method (회원 가입)
        Book book = createBook();

        // when (회원이 상품을 주문할 때)
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(),book.getId(), orderCount) ;

        // then
        Order getOrder = orderRepository.findOne(orderId) ;

        // 각각의 비즈니스 로직 점검
        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야한다", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount , getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야한다.", 8 , book.getStockQuantity());
    } // 상품주문

    @Test(expected = NotEnoughStockException.class)
    // 예외 테스트 **  : 재고 수량 초과 주문
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member = createMember();
        Item item = createBook(); // "jpa" , 10000, 10
        int orderCount = 11; // 재고보다 주문 수량이 많은 상황

        // when
        orderService.order(member.getId(), item.getId(), orderCount) ;  // 예외 터져야함

        // then
        fail("잭 수량 부족 에러가 발행해야한다");
    }


    @Test
    public void 주문취소() throws Exception{
        //given
        Member member = createMember() ;
        Book item = createBook();
        int orderCount = 2 ;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount) ;

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder =  orderRepository.findOne(orderId) ;

        assertEquals("주문 취소시 상태는 CANCEL이다", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야한다.", 10, item.getStockQuantity());
    } // 주문 취소

    @Test
    public void 주문검색() throws Exception{
        //given

        // when

        // then
    }

 

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        en.persist(member);
        return member;
    }



    private Book createBook() {
        Book book = new Book();
        book.setName("jpa");
        book.setPrice(10000);
        book.setStockQuantity(10);
        en.persist(book);
        return book;
    }



}