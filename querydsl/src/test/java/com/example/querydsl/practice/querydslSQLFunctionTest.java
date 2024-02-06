package com.example.querydsl.practice;

import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.Team;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class querydslSQLFunctionTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;
    // 얘는 field level로 올려도 괜찮 (동시성 문제도 알아서 해결해준다)
    // multi thread 환경에서도 트랜잭션 소속 여부 따져서 동시성 문제 없게끔 운영해줌


    @BeforeEach // 각 테스트들 실행 전 아래 작업 먼저 해줌
    public void testEntity() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush(); // 영속성 context(1차 캐시)에 있는 애들 query로 db에 날려줌
        em.clear(); // 영속성 context 비움

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member); // 원래 test에선 syso 쓰면 ㄴㄴ : assertthat으로 검증해야함
            System.out.println("member = " + member.getTeam());
        }
    } // beforeEach

    @Test
    public void sqlFunction(){
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace' , {0}, {1}, {2})", member.username, "member", "M"))
                            // H2Dialect에 등록되어있어야함
                .from(member)
                .fetch();

        for (String s : result){
            System.out.println("s= "+s);
        }
    } // sqlFunction

    @Test
    public void sqlFunction2(){

        List<String> result = queryFactory
                .select(member.username)
                .from(member)
               /* .where(member.username.eq(
                        Expressions.stringTemplate("function('lower', {0})", member.username))    */
                .where(member.username.eq(member.username.lower()))
                .fetch();


        for (String s : result){
            System.out.println("s= "+ s);
        }


    }// sqlFunction2
}