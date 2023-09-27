package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "catecory_id")
    private Long id ;

    private String name ;

    // 엔티티 클래스의 필드가 다대다 관계임을 나타냄
    // 클래스 구조에선 서로 리스트를 가짐으로써 다대다 관계까 성립함
    // 하지만 클래스에선 그렇게 할 수 없으므로 1:다 다:1 관계로 풀어내야함
    // => 이를 위한 중간(연결) 테이블 필요 : 테이블은 category_item으로 지정
    // (but 실무에선 ㄴㄴ : 다른 필드 추가 불가능)
    @ManyToMany
    @JoinTable(name = "category_item"
            ,joinColumns = @JoinColumn(name = "catecory_id"), // 이 엔티티와 Join table이 연결되는 칼럼은 category_id이다
            inverseJoinColumns = @JoinColumn(name = "item_id")) // 다른 엔티티의 table에 연결되는 칼럼은 "item_id"이다.
    private List<Item> items = new ArrayList<>() ;



    // 카테고리 구조 : 개체에서 쭉 내려감 (부모 타입이 내 타입) - 테이블에선 자기 참조 !!!
    // --> 각 클래스에 연결 정보 담겨있음 : 객체가 생성되었을 때 부모 카테고리와 자식 카테고리 정보가 같이 기입됨
    @ManyToOne(fetch = LAZY) // 부모 카테고리는 하나만 가질 수 있음 (나 -- Many, 부모 -- One )
    @JoinColumn(name = "parent_id") // Category 테이블에서 parent_id라는 칼럼 이름으로  조인에 참여함(연결됨)
    private Category parent ;

    @OneToMany(mappedBy = "parent") // parent 라는 필드('부모 카테고리')의 값 변경에 의해 값이 변경됨 
    // 다른 엔티티를 매핑하는 것처럼 생각해야함 (그냥 이름만 같은 복제품이라고 생각)
    private List<Category> child = new ArrayList<>() ; // 자식은 여러 카테고리를 가질 수 있음

    public void addChildCategory(Category child){ // 자식 카테고리 하나 더 추가
        this.child.add(child) ; // 부모의 자식 카테고리 리스트에 하나 추가
        child.setParent(this); // 해당 자식 카테고리의 부모 카테고리 필드엔 이 부모카테고리 정보 추가
    }




} // class
