package domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

}
