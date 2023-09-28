package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 한 테이블 다 때려박음 (vs strategy : Joined, tablePerClass )
@DiscriminatorColumn(name = "dtype") // 섞여있는 레코드들 (자식 클래스들) 의 레코드를 구분해줄 컬럼
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id ;

    // 상속관계 매핑 필요 !! (jpa는 상속관계를 지원하지 않기 때문에)
    private String name ;
    private int price ;
    private int stockQuantity ;

    @ManyToMany(mappedBy = "items")
    // items라는 필드에 의해 만들어진 fk 의 변화가 여기에 반영됨
    private List<Category> categories = new ArrayList<>() ;

    // == 핵짐 비즈니스 로직 == //
    // 재고를 늘리고 줄이고
    // '도메인 주도 설계' : 핵심 비즈니스 로직을 엔티티에 직접 넣음
    //                  ; 엔티티 자체가 해결할 수 있는 비즈니스 로직은 엔티티 안에 메서드로 넣는게 좋음
    // --> 객체 지향을 잘 살릴 수 있음
    // : 데이터를 갖고 있는 객체 안에서 그 데이터를 다뤄주는게 응집도가 있음

    /*
        stock 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity ;
    }  // addStock

    /*
        stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity ;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock") ;
            // 예외 클래스 선언
        }
        this.stockQuantity = restStock ;
    }
    // 이런식으로 필드값을 변경할 일이 있으면 핵심 비즈니스 로직 메서드로 변경해야함
    // getter로 가져와 막 이것저것 서비스 단에서 해주고 setter로 바깥에서 넣어주는것보다....


} // class
