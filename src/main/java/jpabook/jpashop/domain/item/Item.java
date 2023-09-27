package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
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



} // class
