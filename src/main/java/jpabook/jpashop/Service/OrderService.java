package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // 이거 덕분에 생성자 안만들어도 됨...
public class OrderService {

    public final OrderRepository orderRepository ;
    public final MemberRepository memberRepository ;
    public final ItemRepository itemRepository ;

    /*
    주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId) ;
        Item item = itemRepository.fineOne(itemId) ;

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성 (단순화를 위해 한 상품만 고를 수 있게함 --> table 자체는 여러개 남길 수 있게 함 )
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count) ;

        /* 기껏 생성 메서드 만들어 놨는데 이거 가능하게 하면 안됨 ... 생성 방식은 의도한대로 하도록 통일해야함
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setCount(11) ;
        ==> 기본 생성자 procected로 만듬 : @NoArgsConstructor(access = AccessLevel.PROTECTED)

         */

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem) ;

        // 주문 저장
        orderRepository.save(order);
        // CascateType.ALL 로 , order가 persist 될때 delivery, orderItem 자동으로 persist
        // cascade 어디까지 ? lifs cycle 내에서 다른 객체에서 참조하지 않는 private order인 경우에 사용하면 good (Order가 OrderItem과 Delivery를 따로 관리함)
        // ==> 만약 Delevery, OrderItem 이 다른 객체들과 복잡하게 얽혀있다면 각각 repository에 persist만들어주고 사용
        return order.getId();

    } // order

    // 취소
    /*
    주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){

        // 주문 엔티티 로직
        Order order = orderRepository.findOne(orderId) ;

        // 주문 취소
        order.cancel();
        // 짧은 이유 --> 비즈니스 로직을 entity에 다 작성해두고 그냥 호출만 하는거기 때문
        /*
            의의 : 비즈니스 로직이 엔티티에 있다 ! !
            createOrder같은 복잡한 비즈니스 로직을 서비스단이 아니라 엔티티단으로 뺌
            --> '도메인 모델 패턴' : jpa 특성을 더 잘 활용할 수 있음 (entity의 변화 --> table 변화)
            ㄴ 서비스 계층 : 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다
            (vs 트랜잭션 스크립트 패턴 : 서비스 계층에서 대부분의 비즈니스 로직을 처리하는 것 (entity엔 getter만 )

            --> 유지보수 측면에서 검토하고 좋은 쪽 택하기 (문맥에 적합한걸 택하기에 프로젝트 하나에선 양립하기도 함 )
        */
        /*
         근데 원래 같으면 객체 상태는 바꿨다 쳐도 그 상태를 db에 반영하기 위해선 또 그 값을 얻어와 mybatis에 parameter로 넘기면서 비즈니스 로직 호출해서
         다시 해줘야함 ...근데 jpa 는 자바 엔티티의 변화를 영속화시키면서 db에 바로 반영하기 때문에
         따로 쿼리를 작성해줄 필요 없다 !! (엔티티 안의 데이터만 바꾸면 jpa 더티체킹으로 변경 내용을 감지해 그거에 맞춰 update 쿼리를 촥촥 날림)
         order의 status가 바뀜 (알아서 update 쿼리 날림) , item의 stock 변경 설정해줌 (알아서 update 쿼리 날림)
         */
    } // cancelOrder

    // 검색
    /* public List<Order> findOrders(OrderSearch orderSearch){
        return
    } */
}
