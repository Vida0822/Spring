package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager en ;

    public void save(Order order){
        en.persist(order);
    }

    public Order findOne(Long id){
        return en.find(Order.class, id) ;
    }

     public List<Order> findAllByString(OrderSearch orderSearch){
        // jpql
        /* return en.createQuery("select o from Order o join o.member m"+
                    " where o.status = :status " +
                    " and m.name like :name "
                 // join o.member m : 객체니까 참조스타일로 조인
                 // " " 안에 쿼리 넣을 떈 앞뒤 공백 필수
                 , Order.class)
                 .setParameter("status", orderSearch.getOrderStatus())
                 .setParameter("name", orderSearch.getMemberName())
                 .setMaxResults(1000) // 최대 1000건
                 .getResultList();

         근데 값이 없으면 해당 값 조건으로 조회하는 쿼리문 없어야함
         --> 만약 값이 없으면 다가져오고, 있으면 문장 생성해서 검색해
         ("null 이면 상태 체크 하지말고 다가져와!")
         --> '동적쿼리' (mybatis는 잘 되어있음)
          */

         // 동적쿼리 1. java 분기 사용  (권장 x)
         /*
            문자로 더하기해서 끼워넣기... -> 개 힘듬 개오바 (실수(오타) 多, 가독성 ↓)
          */

             String jpql = "select o from Order o join o.member m";
             boolean isFirstCondition = true;

             //주문 상태 검색
             if (orderSearch.getOrderStatus() != null) {
                 if (isFirstCondition) {
                     jpql += " where";
                     isFirstCondition = false;
                 } else {
                     jpql += " and";
                 }
                 jpql += " o.status = :status";
             }

             //회원 이름 검색
             if (StringUtils.hasText(orderSearch.getMemberName())) {
                 if (isFirstCondition) {
                     jpql += " where";
                     isFirstCondition = false;
                 } else {
                     jpql += " and";
                 }
                 jpql += " m.name like :name";
             }

             TypedQuery<Order> query = en.createQuery(jpql, Order.class)
                     .setMaxResults(1000);

             if (orderSearch.getOrderStatus() != null) {
                 query = query.setParameter("status", orderSearch.getOrderStatus());
             }
             if (StringUtils.hasText(orderSearch.getMemberName())) {
                 query = query.setParameter("name", orderSearch.getMemberName());
             }

             return query.getResultList();
         }

    // 동적쿼리 2. Jpa Criteria (권장 x)
    /*
    jpa가 제공하는 표준 동적쿼리 작성 방법
    : jpql을 자바코드로 작성할 수 있음

    */
    public List<Order> findAllCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = en.getCriteriaBuilder() ;
        CriteriaQuery<Order> cq = cb.createQuery(Order.class) ; // 응답 타입
        Root<Order> o = cq.from(Order.class) ;
        Join<Object, Object> j = o.join("member", JoinType.INNER) ;

        List<Predicate> criteria = new ArrayList<>( ) ;

        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status) ;
        }
             // :
             // :
        // 단점 : 유지보수성이 너무 낮고 실제 쿼리랑 너무 다름 (알아보기 불가능)
        // --> 도대체 무슨 jpql이 만들어지는거야?

        return null  ;
    } // findAllCriteria


    // 동적쿼리 3. Query DSL  **(해결책)
    /*
     정말 편해서 동적쿼리 말고도 정적쿼리 조금만 복잡해져도 이거 쓰면 됨 !
     */
    public List<Order> findAllBYDSL(OrderSearch orderSearch) {


        return null ;



    } // findAll

    public List<Order> findWithMemberDelivery() {
        return en.createQuery(
                "select o from Order o "+
                        "join fetch o.member m "+
                        "join fetch o.delivery d ", Order.class
                // 한번에 조인해서 select 절로 다 가져오는거임
        ).getResultList() ;
    }


    public List<Order> findAllWithItem() {
        return en.createQuery(
                "select distinct o from Order o "+
                       // " join fetch o.member o " + - QueryException: could not resolve property: delivery of: jpabook.jpashop.domain.Member
                        " join fetch o.member m " +
                        " join fetch o.delivery d " +
                        " join fetch o.orderItems oi "+
                        " join fetch oi.item i ", Order.class)
               // .setFirstResult(1)
               // .setMaxResults(100)
                .getResultList() ;

    }
} // class
