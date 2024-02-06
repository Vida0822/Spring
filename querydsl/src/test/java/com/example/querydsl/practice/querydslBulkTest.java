package com.example.querydsl.practice;

import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class querydslBulkTest {

    @Autowired
    EntityManager em ;

    JPAQueryFactory queryFactory  ;
    // 얘는 field level로 올려도 괜찮 (동시성 문제도 알아서 해결해준다)
    // multi thread 환경에서도 트랜잭션 소속 여부 따져서 동시성 문제 없게끔 운영해줌


    @BeforeEach // 각 테스트들 실행 전 아래 작업 먼저 해줌
    public void testEntity(){
        queryFactory = new JPAQueryFactory(em) ;
        Team teamA = new Team("teamA") ;
        Team teamB = new Team("teamB") ;
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA) ;
        Member member2 = new Member("member2", 20, teamA) ;
        Member member3 = new Member("member3", 30, teamB) ;
        Member member4 = new Member("member4", 40, teamB) ;
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush(); // 영속성 context(1차 캐시)에 있는 애들 query로 db에 날려줌
        em.clear(); // 영속성 context 비움

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member: members) {
            System.out.println("member = " + member); // 원래 test에선 syso 쓰면 ㄴㄴ : assertthat으로 검증해야함
            System.out.println("member = " + member.getTeam());
        }
    } // beforeEach

    /*
    기본 jpa의 변경감지 --> 영속성 컨텍스트의 데이터를 직접 수정, 삭제
    ㄴ 레코드 하나씩 조회해서 건건히 변경
    ==> 조건에 맞는 대량의 데이터를 한번에 수정, 삭제할때는 좋지 않음
    => 그렇게 한번에 연산처리 해주는걸 'bulk 연산'이라고 함
     */

    /*
    수정
     */
    @Test
    public void bulkUpdate(){
        queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute() ;

     /*
        but !! bult 연산은 영속성 컨텍스트를 무시하고 db에 바로 팍 나감
        ==> db와 영속성 컨텍스트의 상태가 달라져버림
        ( 영속성 컨텍스트 : member1, member2, member3, member4
            db : 비회원, 비회원, member3, member4

        * 그럼 db에 반영된 값을 영속성 context에 다시 가져와야겠네?
        ->  근데 db에서 읽어온 값이 있어도 영속성 컨텍스트에 있는 값을 유지함 (항상 우선권)
        - db의 값은 '준영속 상태'로 남음 <-> db의 값은 무시됨

        * dirty checking : 영속성 컨텍스트에 있는 값이 수정, 삭제되면 그 변경사항을 db에 반영함
        반대로, 영속성 컨텍스트는 변화하지 않았는데 그안에 있는 객체들과 같은 id의 객체들을 생성, 수정해도 영속성 컨텍스트는 영향을 받지 않음
         ㄴ 그렇게 영속성컨텍스트의 영향을 받지 않는 객체들을 '준영속 상태'에 있다고 함
         */

        // 해결: 영속성 context를 초기화해서 조회한 결과로 다시 채움 !!
        em.flush();
        em.clear(); // 영속성 context 날리고 (아무값도 없음)

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch() ; // 새로 조회해온 객체들은 영속성 context에 없는 값들이기 때문에 새롭게 초기화됨

        for (Member member1: result) {
            System.out.println("member1 = "+ member1);
        }
    } // bulkUpdate


    /*
    bulkAdd
     */
    @Test
    public void bulkAdd(){
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
//                .set(member.age, member.age.multiply(2))
                .execute();
    }

    /*
    삭제
     */
    @Test
    public void bulkDelete(){
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute()  ;
    } // bulkDelete
}
