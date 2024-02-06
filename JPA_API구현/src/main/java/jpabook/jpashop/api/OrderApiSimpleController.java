package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiSimpleController {

    /*
    주문 정보 조회
    --> 회원 정보 (연관관계)
    --> 배송 정보 (연관관계) 같이 조회 (XToOne 관계)

    관건 : 지연로딩으로 설정 되어 있어 Order 을 조회하더라도 Member, Delivery의 정보는 조회되지 않는다
    => 그렇다면, 지연로딩을 유지한 상태에서 연관관계 객체의 정보를 함께 조회하려면 어떻게 해야하는가?
     */
    private final OrderRepository orderRepository;

    /*
    1.
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // Order에 Member와 Delivery 다 있음

    /*
    문제 발생 : 무한 루프...
    원인 : Order에서 Member를 조회하기 위해 들어가니 List<Order>
    --> Order 조회하기 위해 다시 Member
    --> 그 Member 찾기 위해 다시 Order (양방향 연관관계에서의 문제)
    : Model을 붙이는 @Controller에선 Model에 붙이는 객체의 참조를 개발자가 직접 선택
    but Rest Api에서 Json은 객체를 텍스트로 표현하는 형식이기 때문에 객체간 연결 표현을 위해 재귀적으로 표현 (어디선가 끊어줘야함 )

    해결 : 양방향이 걸리는데를 다 @JsonIgnore 해줘서 반대쪽으로 참조 x

    다시 문제 발생 : 500 Server Error
    원인 : 지연로딩 - Order 만 db에서 조회하고 Member는 조회 x --> Member에는 new ProxyMember() (ByteBuddyInterceptor)
            ==> 순수한 Member를 뽑아야하는데 다른 객체가 있으니 500번 에러
    해결 : Hibernate5Module 을 스프링 빈으로 등록해 이 프록시 빈을 json 객체로 생성
        (의존 추가 --> main에 빈 등록)
           & LazyLoading으로 적힌 애들 다 db에서 끌고 옴 --> 모든 지연로딩 억지 실행

    문제 : Api 스펙과 관계없는 다른 객체들(거의 전부)를 다 끌고옴 ex) OrderItem
    해결 : Hibernate5Module 옵션 사용하지 말고 Lazy 로딩 내가 직접
        ※ 그렇다고 EAGER 로 바꾸면 ㄴㄴ
    */

        for (Order order: all  ) {
            order.getMember().getName() ; // Lazy 강제 초기화
            order.getDelivery().getAddress() ; // Lazy 강제 초기화
        }
        return all ;
        /*
    문제 : getName()만 요청하긴 하지만 사실상 Member엔티티의 다른 정보들까지 다 들고 옴
     ==> 결론 : Entity를 외부로 노출하지 말자 ! 필요한 api 스펙만 dto로 변경해서 노출 ***
    */
    }

    /*
    2.
    엔티티를 DTO로 변환하는 일반적인 방법 (성능 문제 有 )
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        return orderRepository.findAllByString(new OrderSearch()) // ORDER 2개
                .stream().map(o -> new SimpleOrderDto(o)) // dto가 entity를 parameter로 받는건 왠만하면 ㄱㅊ
                .collect(toList());
    } // ordersV2


    /*
    문제 : 쿼리가 너무 많이 호출됨 (성능 문제 발생)
    원인 :
      테이블 3개 조회 ORDER, MEMBER, DELIVERY
      Order 조회 --> Sql 1번 --> 결과 row 수 2개
      Member 조회 - member0_.member_id=1 x 2 --> 각각 Lazy 로딩 초기화
      Delivery 조회 - delivery0_.delivery_id=1 x 2
      ==> 총 5개 쿼리 터짐 (6.65 s)
      N + 1 문제 : "N 개 결과 조회하니 1+N 개의 쿼리가 추가실행 됨) --> jpa 성능 문제의 90% !!
     */

    /*
    3.
     Entity --> dto : fetch 조인 사용 (자주 사용)
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findWithMemberDelivery() ;
        /*
        "select o from Order o "+
         "join fetch o.member m "+
        "join fetch o.delivery d "
        : proxy 가 아닌 진짜 객체값들을 다 채워서 반환
        (sql에선 그냥 조인인데 jpa에선 '패치 조인'으로 가져오는거임)
        ==> 쿼리가 한번 나감 !!(143 ms)   vs   n+1 ...(6.65 s)
        : Lazy로딩 자체가 일어나지 않고 전부 db에서 조회하는 거임 ! 그중 조건에 맞는 row 만 가져옴

         */
        List<SimpleOrderDto> collect = orders.stream().map(o -> new SimpleOrderDto(o)).collect(toList());
        return collect ;
        /*
        보통 같이 쓰는 객체가 정해져있기 때문에 fetch join하는 repository 메서드 선언해서 사용하는 경우 多
         */
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name ;
        private LocalDateTime orderDate ;
        private OrderStatus orderStatus ;
        private Address address ;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name= order.getMember().getName() ; // LAZY 초기화 : 영속성 컨텍스트에 없으니 db에 쿼리 날림
            orderDate = order.getOrderDate() ;
            orderStatus = order.getStatus() ;
            address = order.getDelivery().getAddress(); // LAZY 초기화 : 영속성 컨텍스트에 없으니 db에 쿼리 날림
        }
    } // SimpleOrderDto

    /*
    4.
    JPA에서 DTO 로 바로 조회
     */
    private final OrderSimpleQueryRepository simpleQueryRepository;

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return simpleQueryRepository.findOrderDtos() ;
    }

    /*
    V3 vs V4
    V3: fetchjoin : 외부를 건들이지 않고 간단하게 조회 가능
    vs
    V4 : 재사용성 x (로직 재활용 x ), but 성능 최적화 측면에선 조금 낫지만 생각보다 미비
        --> Repository 코드가 API 스펙에 맞춰서 짜져버림 (Repository의 용도는 Entity 를 직접 CRUD 하는게 보통)
            (계층이 깨져있는것, Repository가 화면에 의존, 사실상 박힌거임 --> API 스펙이 바뀌면 Repository를 바꿔줘야 함)
         but 생각보다 성능에 영향 주지 x
         --> 보통 from 절에서 성능 문제 발생 ( Index 잘못잡히고.... ) , select 필드 수가 큰 문제 x
         ==> 정말 select 필드가 많은 경우 V4 선택 !
         : 이때, 이러한 성능 최적화용 쿼리를 별도의 패키지로 뽑는다 ("조회전용으로 특정 화면에 맞춰 쓰는거구나 !" )
     */
    /*
    결론 : 쿼리 방식 선택 권장 순서
    1. 우선 엔티티를 DTO로 변환하는 방법 선택
    2. 필요하면 패치 조인으로 성능을 최적화한다 (대부분의 성능 이슈가 해결)
    3. 그랟 안되면 DTO로 직접 조회
    4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template를 사용해 SQL 직접 사용

     */
}
