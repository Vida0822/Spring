package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id ;

    // @ManyToOne // 다대일 중 다쪽이 주문 --> Orders(many) To Member(one)
    @ManyToOne(fetch = FetchType.LAZY) // default인 eager에서 lazy 타입으로 바꿔줘야함
    @JoinColumn(name = "member_id")  // 'FK(참조하는 컬럼)' 생성
    // @JoinColumn : join할 컬럼, 즉 FK를 생성하며 이름이 member_id 가 됨 --> 연관관계 거울 쪽에서 조인 당하는(참조되는 컬럼) "mapped By" 표시
    // fk 가 Order 테이블에 생김 (not Member 테이블) --> 연관관계 주인쪽에 JoinColumn 적어줌 !!
    private Member member ;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // '참조 당하는 컬럼'
    // mappedBy ('참조된다') : 연관관계 거울 --> OrderItems의 필드 order가 참조하는 필드가 이 필드임
    // @JoinColumn Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn: jpabook.jpashop.domain.Order.orderItems
    // 연관관계 거울쪽엔 JoinColmn 적어주지 않음 : mapped By(참조당한다)와 JoinColumn(참조한다)는 같이 적어줄 수 없음
    private List<OrderItem> orderItems = new ArrayList<>() ;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // foreign key 어디에 둘까?  --> access를 많이 하는데에 둠 : order를 보면서 delivery를 봄 (delevery를 찾을 때 order을 볼일 보다)
    // --> 그래서 orders에 delivery_Id를 Order 테이블의 fk로 둠 (not Delivery 테이블)
    @JoinColumn(name = "delivery_id") // fk가 delevery_id 가 Order 테이블에 생김
    private Delivery delivery ; // 원래 Delivery도 persist해줘야하는데 cascade = CascadeType.ALL 인해 Order만 persist해줘도 자동으로 됨


//  private Date date ; --> 그냥 Date 타입으로 하면 annotaion으로 매핑 시켜줘야함
    private LocalDateTime orderDate ; // 근데 localDate 타입으로 하면 hibernate가 자동으로 매핑 (java 8)
    private OrderStatus status ; // 주문 상태  - Enum 타입

    // 연관관계 편의 메서드 **
    // 원래 양방향 관계에서 서로의 객체 변화사항을 반영하려면 (테이블에선 연결시켰으니 정보 변경이 알아서 반영 되겠지만 객체간 값 세팅은 알아서 반영되지 않음)
    /* 원래 member의 주문 필드를 얻어와 주문 정보를 setting(Member)한 후 주문 클래스에도 멤버 정보를 입력해야함
        Member member = new Member() ;
        Order order = new Order();

        member.getOrders().add(order) ;
        order.setMember(member) ;
    }
    => 이러한 코드를 '비즈니스 로직'에 넣어야함
     ㄴ 근데 까먹을 수 있자나?
     연관관계가 있는 클래스 자체에서 이 두관계를 명시적으로 묶어주는 메서드를 만들어
     서로의 값 변화를 서로에게 바로 반영할 수 있도록 함 ('원자적으로 한 코드로 해결')
         */
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this) ;
        // setter 메서드에 Member의 order 필드도 수정하는 로직 추가 : '자신'을 설정정보로 제공하면서
    }

    // 주문했을 때, 주문 테이블에도 주문한 아이템들이 들어가야하고,
    // 주문한 아이템 테이블에도 주문정보가 들어가야한다. (양방향 , 1대 다)
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem) ;
        orderItem.setOrder(this);
    }
    // 연관관계 메서드 위치 --> 핵심적으로 컨트롤 하는쪽

    public void setDelivery(Delivery delivery){
        this.delivery = delivery ;
        delivery.setOrder(this);
    }



} // class
