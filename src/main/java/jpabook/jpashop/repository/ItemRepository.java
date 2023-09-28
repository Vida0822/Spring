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
        if(item.getId() == null){
            en.persist(item) ; // 완전 새로 생성한 객체
        }else{ // db 에 등록된걸 기져오는거 ("db한번 들어간거구나" ) -- > 영속성 context는 commit해서 db에 넣을 때 '비워지는게' 아님 : 그냥 일괄처리 방식으로 일정 부분씩 저장하는거
            en.merge(item) ; // update 비슷하게... !
        }
    } // save

    public Item fineOne(Long id){
        return en.find(Item.class, id) ;
    } // fineOnes

    public List<Item> findAll(){
        return en.createQuery("select i from Item i", Item.class)
                .getResultList() ;
        // jpql --> jpa에 등록되어있지 않은 쿼리 메서드를 날려야할땐 쿼리를 직접 작성해줘야함
    } // findAll




} // class
