package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Entity --> entity는 아님 그냥 domain ( = joinRequest )
public class OrderSearch {
    // 검색 : 동적 쿼리 날려줘야함 (넘어오는 parameter값에 따라 Where로 조회하는 검색조건이 그때그때 달라짐)
    // (not 쿼리를 다 다르게 쓰는게 아니라 쿼리 자체가 달라지는 거임)

    private String memberName ; // 회원 이름 
    private OrderStatus orderStatus ; //주문 상태 

   
}
