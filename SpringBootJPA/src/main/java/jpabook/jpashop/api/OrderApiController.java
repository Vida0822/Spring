package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    /*
    XToMany 데이터들, 즉 Order에 대한 상품List까지 가져올 때 어떻게 성능 최적화를 할 수 있는가?
     ※ 1:다 조회의 문제   : 주문 하나만 조회해도 주문상품 때문에 rows 가 5개가 뻥튀기 되서
     */
    private final OrderRepository orderRepository ;

    /*
    1. 엔티티 직접 노출
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName() ; // 강제 초기화 - Member (where orderID = ?)
            order.getDelivery().getAddress(); //  강제 초기화 - Delivery

            List<OrderItem> orderItems = order.getOrderItems() ; // OrderItems
            orderItems.stream().forEach(o->o.getItem().getName()); // Item
        }
        /* Order의 필드 중 영속성 컨텍스트에 있는 애들로 채울 수 있는애들론 필드 채움
            --> 강제 초기화 함으로써 Member, Delivery, OrderItems 값들 불러옴 (재료 준비)
         ex) member,delivery  --> Lazy loading이므로 원래같으면 proxy로 값 채워지지만
            getName, getAddress로 강제 초기화하면서 그 테이블에서 관련된 데이터들도 싹 긁어오니까 (where order id = ?)
            저 값들도 다 채울 수 있음 !
            => 문제 : 관련된 데이터들을 읽어오기 위해 쿼리가 효율적으로 나가는가?
         */
        return all ; // 그래서 List<Order>반환시 각 Order의 member, delivery, List<OrderItem>값도 같이 반환될 수 있는 거임
    } // ordersV1

    /*
    "orderItems": [
            {
                "id": 6,
                "item": {
                    "id": 2,
                    "name": "JPA1 Book",
                    "price": 10000,
                    "stockQuantity": 99,
                    "categories": null,
                    "author": null,
                    "isbn": null
                },
                "orderPrice": 10000,
                "count": 1,
                "totalPrice": 10000
            },
     */


    /*
    2. 엔티티를 dto로 변환
     */
//    @GetMapping("/api/v1/orders")
    @GetMapping("/api/v2/orders")
    /*
    jpabook.jpashop.api.OrderApiController#ordersV1() to {GET [/api/v1/orders]}
    : There is already 'orderApiController' bean method
    jpabook.jpashop.api.OrderApiController#ordersV2() mapped.
     */
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch()) ;
        return orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
    }
    @Data
    // @Getter
    static class OrderDto {

        private Long orderID ;
        private String name ;
        private LocalDateTime orderDate ;
        private OrderStatus status ;
        private Address address ;
        //        private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        /*
            {
        "orderID": 4,
        "name": "userA",
        "orderDate": "2023-12-26T22:55:58.726906",
        "status": "ORDER",
        "address": {
            "city": "서울",
            "street": "1",
            "zipcode": "1111"
        },
        "orderItems": [
            {
                "itemName": "JPA1 Book",
                "orderPrice": 10000,
                "count": 1
            }
        ]
        ㄴ 필요한 데이터만 가져올 수 있음 : 위의 Entity 자체 반환은 화면에 불필요한 데이터 ex) item 상세정보 까지 다..
            => 운영 헬 !
         */
        public OrderDto(Order order) {
            orderID = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            status = order.getStatus();
            address = order.getDelivery().getAddress();

            // orderItems = order.getOrderItems(); // null 나옴 (강제 초기화 안해줬으니까)
            // 그렇다면 위에처럼 Stream ? 외부에 Entity가 노출되면 안됨 (심지어 감싸는것도 안됨)
            //   : 위 데이터들처럼 표시할 데이터만 반환 <-> OrderItem 마저 Dto로 반환
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem)) // DTO' 생성자 Parameter론 Entity 넣어주는게 편함
                    .collect(toList());
        }
    } // OrderDto

    @Data
    static class OrderItemDto {

        private String itemName ;
        private int orderPrice ;
        private int count ;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName() ;
            orderPrice = orderItem.getOrderPrice() ;
            count = orderItem.getCount() ;
        }
    } // OrderItemDto

    /*
    끔찍한 쿼리 횟수 ...
   ( Order 1건 --> Member 1건 + Delivery 1건 + Order Item 2건 + Item 2 건 ) x Order 갯수
     */
    /*
    3. Collection일 때의 fetch join (주의할 점 더 有)
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem() ;
        /*
        for (Order order : orders) {
            System.out.println("order ref = " + order + "order Id " + order.getId());
        }
        
         */

        return orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        /*
         ORDER & ORDERITEM 조인 시 Order 데이터가 중복해서 나타남 (OrderItem 행 갯수 기준으로 rows 출력되므로)
         => 근데, dto에서 출력할 땐 OrderItem 하나만 출력하는게 아니라 Order의 OrderItem들을 같이 출력함
         <-> 정확히 동일한 데이터가 OrderItem 행갯수만큼 중복해서 나오는 것 !
        => repository 쿼리에 'Distinct'에 넣어줌 !
            Jpa'disticnt : 적용된 테이블(Order)의 @Id 값이 똑같으면 중복을 제거하고 반환 (반환되는 Entity 객체가 'Order'니까 가능한 것)
            vs 일반 db distinct : 행 전체가 아예 똑같아야 중복 제거 (Order + Member + OrderItem ... 가 전체 동일해야됨)
        */
        /*
        ※ 일대다 fetch 조인의 치명적 단점 :
        1. 페이징이 안된다...
        : jpa--> 일단 데이터를 다 불러오고, 메모리에서 페이징 처리
        <-> 데이터를 다 불러와 어플리케이션에 올려놓고 거기서 걸러내는 거임... Memory 한계 당근 벗어나겠지...
            (sql 로 offset 이런게 되는게 아니라)
        ex) 50만개 데이타
        => '다' 기준으로 뻥튀기가 되버리기 때문에 페이징의 기준 자체가 애매해짐 (Order의 첫번째 ~ 3번째를 하고싶은데...)
        --> jpa가 어쩔 수 없이 메모리에서 해주는 것

        2. 컬렉션 둘 이상에 패치 조인을 사용하면 안된다
        (뭘 기준으로 데이터를 끌고올지 모를 수 있고 정합성이 떨어짐)
         */
    } // ordersV3

    /*
    3.1. 페이징 한계 개선
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset
            ,@RequestParam(value = "limit", defaultValue = "100") int limit )
    {
        List<Order> orders = orderRepository.findWithMemberDelivery(offset, limit) ;
        // member 랑 delvery 한방 쿼리로 가져옴 --> XToOne 관계에선 fetch join 많이해도 페이징 가능
        // 페이징할 기준인 Order 선에서 걸러져서 들어오고 --> 여기서 Order가 중복되지 않게 rows 2개만 넘어오고

        return orders.stream()
                .map(o -> new OrderDto(o)) // 강제 지연로딩
                .collect(toList());
        // 그 걸러진 Order 각각에 대해 컬렉션 지연로딩 (페이징된 애들 요소 각각에 컬렉션 붙여주는거니 페이징 깨지지 않음)
        // --> 결과 2개를 Stream 돌리면서 각각 OrderItems 컬렉션 붙여주기 (물론 얘는 조회된 행수만큼 조회 쿼리가 나가지만 이건 아래에서 개선)
        /*
        문제발생 : OrderItems에서 n+1 문제 발생 (Member, Delivery는 ㄱㅊ)
        => application.yml : hibernate.default_batch_fetch_size: 100 (global)
            or @BatchSize - XToMany : 그 컬렉션을 필드로 갖는 클래스에서 그 위에 ex) List<OrderItem> , XToOne : 해당 클래스 자체에 ex) Item
            <-> In 쿼리의 파라미터로 100개까지 받겠다
            --> 'IN' 쿼리로 조회되는 OrderItem 별로(2번), 그 Item별로(2번) 각각 조회했던것과 달리 3번만 쿼리 나감 
            <-> 1:n:m 을 1:1:1로 변환!

        * batchsize 사이즈 설정이 중요 !
            ㄴ db 중 IN 쿼리에서 1000개를 넘어가면 오류나는 경우 多 (maximum 1000)
        ==> 순간 부하(CPU, 리소스 사용) vs 시간   고려해서 100 ~ 1000 개 사이
            ㄴ but 메모리는 어떻게 불러오든 다 불러오기때문에 사용량이 같음 (outofmemory 위험은 같음)
        */
    } // ordersV3_page

    /*
    4. Jpa로 컬렉션을 DTO로 바로 조회 (엔티티 x)
     */
    private final OrderQueryRepository orderQueryRepository ;

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDto() ; // repository에서 바로 dto를 반환
    }  // ordersV4

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findOrderQueryDto_opt() ; // repository에서 바로 dto를 반환
    }  // ordersV4

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> flats = orderQueryRepository.findOrderQueryDto_flat();
        // OrderFlatDto를 OrderQueryDto 스펙에 어떻게 맞추는가 --> 직접 중복을 거른다 !

        return flats.stream()
                .collect(
                        groupingBy(o -> new OrderQueryDto(o.getOrderID(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress())
                                // 1. 그룹 기준 : 각 주문 정보들을 OrderQueryDto로 매핑 -> 그루핑 기준(Key)는 OrderQueryDto지만 실질적으로 OrderId 기준
                                , mapping(o-> new OrderItemQueryDto(o.getOrderID(), o.getItemName(), o.getOrderPrice(), o.getCount()) , toList())
                                // 2. 각 그룹에 매핑 : OrderQueryDto에서 각 주문별 아이템 정보들을 뽑아 OrderItemQueryDto로 변환 --> List<OrderItemQueryDto>생성
                        ) // groupingBy
                ).entrySet().stream() // <OrderQueryDto order, List<OrderItemQueryDto> > --> 이 엔트리 자체가 스트림의 요소 
                .map(e -> new OrderQueryDto(e.getKey().getOrderID(),e.getKey().getName()
                        , e.getKey().getOrderDate(), e.getKey().getOrderStatus()
                        , e.getKey().getAddress(), e.getValue())) // <key와, value>를 하나의 OrderQueryDto로 합쳐줌 (요소 변환)
                .collect(toList()); // 리스트로 변환해 반환 

        /*
        단점 : 쿼리는 한번이지만 중복데이터 추가 --> 애플리케이션에서 추가 작업이 크다 (분해해 웹 계층 dto로 만드는 과정)
         */


    }  // ordersV6
} // OrderApiController