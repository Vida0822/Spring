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

    @ManyToMany
    @JoinTable(name = "category_item"
            ,joinColumns = @JoinColumn(name = "catecory_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>() ;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent ;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>() ;

    public void addChildCategory(Category child){ // 자식 카테고리 하나 더 추가
        this.child.add(child) ; // 부모의 자식 카테고리 리스트에 하나 추가
        child.setParent(this); // 해당 자식 카테고리의 부모 카테고리 필드엔 이 부모카테고리 정보 추가
    }




} // class
