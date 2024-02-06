package com.example.querydsl.search;

import com.example.querydsl.dto.MemberDto;
import com.example.querydsl.dto.QMemberDto;
import com.example.querydsl.dto.UserDto;
import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QMember;
import com.example.querydsl.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class querydslProjectionTest {

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
        em.flush(); // 영속성 context에 있는 애들 query로 db에 날려줌
        em.clear(); // 영속성 context 비움

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member: members) {
            System.out.println("member = " + member); // 원래 test에선 syso 쓰면 ㄴㄴ : assertthat으로 검증해야함
            System.out.println("member = " + member.getTeam());
        }
    } // beforeEach

    /*
    프로젝션과 결과 반환 
     */
    @Test
    public void simpleProjection(){

        // 프로젝션으로 조회되는 필드 타입이 1개 --> 그거에 매치되는 자료형으로 받으면 됨
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s= "+s);
        }
    } // simpleProjection

    // 튜플
    @Test
    public void tupleProjection(){
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for(Tuple tuple : result){
            String username = tuple.get(member.username) ;
            Integer age = tuple.get(member.age) ;
        }

    }
    /*
    querydsl 패키지(종속적)
     --> repository 계층에서 쓰는건 괜찮은데 servie , controller로 넘어가는건 좋지 않음
         : 그래야 하부 시스템 구조가 바뀌어도 위에는 바뀌지 않음
    ==> 바깥으로 나갈땐 dto로 변환해서 나가는게 나음 !
        ('성능 최적화')
     */

    @Test
    public void findDtoByJPQL(){
     //   em.createQuery("select m.username, m.age from Member m ", MemberDto.class) --> 타입 안 맞아서 오류
        List<MemberDto> results = em.createQuery("select new com.example.querydsl.dto.MemberDto(m.username, m.age) from Member m ", MemberDto.class)
                // 순수 jpa에서 dto를 조회할 땐 new 명령어(생성자 방식) 로 package이름을 다 적어줘야해서 지저분함
                .getResultList();

        for (MemberDto memberDto:  results ) {
            System.out.println("memberDto = " + memberDto);
        } // for
    } // findDtoByJPQL

    // setter 접근 방식
    @Test
    public void findDtoBySetter(){// queryDSL - 프로퍼티 접근 방식

        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result ){
            System.out.println("memberDto = " + memberDto);
        }
    } // findDtoBySetter


    // 필드 직접 접근
    @Test
    public void findDtoByField(){// queryDSL - 프로퍼티 접근 방식

        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result ){
            System.out.println("memberDto = " + memberDto);
        }
    } // findDtoByField

    // 생성자 접근
    @Test
    public void findDtoByConstructor(){

        List<UserDto> result = queryFactory
                // .select(Projections.constructor(MemberDto.class
                .select(Projections.constructor(UserDto.class
                        // 생성자 방식은 '타입'으로 인식하기 때문에 그냥 그대로 바꿔서 사용해도 됨
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for (UserDto memberDto : result ){
            System.out.println("memberDto = " + memberDto);
        }
    } // findDtoByConstructor

    // User - field, setter
    @Test
    public void findUserDto(){

        QMember memberSub =  new QMember("memberSub") ;

        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class
                     //   , member.username
                        // User : name, age --> 필드명이 달라서 오류
                        // ==> dto로 받아주려면 이름 일치시켜 줘야함
                        , member.username.as("name")

                        , ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub),"age")
                 ))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result ){
            System.out.println("memberDto = " + memberDto);
        }
    } // findDtoByField

    /*
     @QueryProjection
     */
    // : 결과로 받을 Dto 위에 직접 붙여줌으로써 ("이건 query결과로 받을 dto객체야") --> Q파일(Dto자체를 클래스로 만드는것)  만들어서 그 생성자 이용
    //  vs 위의 contructor 방식 :  필드를 잘못 넣어줘 생기는 똑같은 문제를 컴파일 오류로 못잡고 런타임때 알아챌 수 있음
    // ==> 여기선 바로 생성자로 만들기 때문에 개수나 타입 안맞으면 컴파일 오류 만듬
    @Test
    public void findDtoByQueryProjection(){
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                // 생성자를 그대로 가져가기 때문에 타입같은거 다 맞춰줌 !
                .from(member)
                .fetch();
    } // findDtoByQueryProjection


}
