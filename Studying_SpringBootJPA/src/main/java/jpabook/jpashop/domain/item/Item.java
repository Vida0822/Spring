package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
// @BatchSize(size = 100)
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id ;

    private String name ;
    private int price ;
    private int stockQuantity ;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>() ;
    // 얜 @JsonIgnore 없어도 됨 : category는 강제 초기화 안하니까 Proxy로 나옴  json - "categories": null,

    // == 핵짐 비즈니스 로직 == //
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
} // class
