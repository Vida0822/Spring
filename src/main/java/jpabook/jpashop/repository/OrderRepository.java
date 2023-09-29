package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
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

     public List<Order> findAll(OrderSearch orderSearch){
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
         String jpql = " select o from Order o join o.member m " ;
         boolean isFirstCondition = true ; // 두번째 조건부턴 and가 되어야해 만듬

         // 주문상태검색
         if(orderSearch.getOrderStatus() != null){
             if(isFirstCondition){
                 jpql += " where " ;
                 isFirstCondition = false ;
             }else{
               jpql += " and ";
             }
             jpql += " o.status = :status " ;
         }

         if(StringUtils.hasText(orderSearch.getMemberName())){
             // hasText() : 이 메서드로 들어오는 인자값(String)이 null이 아니면 true를 반환 , null이 들어오면 (아무 조건도 입력되지 않으면 false)
             if (isFirstCondition){
                 jpql += " where ";
                 isFirstCondition = false ;
             }else{
                 jpql += " and ";
             }
             jpql += " m.name like :name " ;
         }
         TypedQuery<Order> query = en.createQuery(jpql, Order.class).setMaxResults(1000) ;
         // TypedQuery : Execute a SELECT query and return the query results as a typed List.
         //Returns: a list of the results<Order>
         if (orderSearch.getOrderStatus() != null){
             query = query.setParameter("status", orderSearch.getOrderStatus()) ;
         }
         if (StringUtils.hasText(orderSearch.getMemberName()) ){
             query = query.setParameter("name", orderSearch.getMemberName()) ;
         }
         return query.getResultList() ;

    } // findAll

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
} // class
