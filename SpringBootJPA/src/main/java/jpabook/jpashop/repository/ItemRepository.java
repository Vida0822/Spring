package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager en ;

    public void save(Item item){
        /*
        if(item.getId() == null){
            en.persist(item) ;
        }else{
            en.merge(item) ;
        }
         */
        en.persist(item) ;
    } // save

    public Item fineOne(Long id){
        return en.find(Item.class, id) ;
    } // fineOnes

    public List<Item> findAll(){
        return en.createQuery("select i from Item i", Item.class)
                .getResultList() ;
    } // findAll




} // class
