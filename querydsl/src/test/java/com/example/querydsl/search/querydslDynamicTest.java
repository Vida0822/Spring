package com.example.querydsl.search;

import com.example.querydsl.dto.MemberDto;
import com.example.querydsl.dto.QMemberDto;
import com.example.querydsl.dto.UserDto;
import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QMember;
import com.example.querydsl.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
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
public class querydslDynamicTest {

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
    Boolean Builder 사용
    : 값의 존재 여부에 따라 조건식 생성
     */
    @Test
    public void dynamicQuery_BooleanBuilder(){

        // 동적쿼리로 검색하려는 파라미터
        String usernameParam = "member1" ;
        Integer ageParam = 10 ;

        List<Member> result = searchMember1(usernameParam, ageParam) ;
        assertThat(result.size()).isEqualTo(1) ;
    }  // dynamicQuery_BooleanBuilder

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {

       // BooleanBuilder builder = new BooleanBuilder() ;
        // 조건식 builer (참거짓이 판별되는 조건식들이 쌓여있는 builder 칸)

        BooleanBuilder builder = new BooleanBuilder(member.username.eq(usernameCond)) ;
        // 필수인 조건식은 초기값으로 넣어줄 수 있다 (username 검색조건은 반드시 넘어온다)

        if(usernameCond != null){ // 검색조건이 넘어오면
            builder.and(member.username.eq(usernameCond)) ; // 조건식 생성 + and로 묶음
        }
        if(ageCond != null){ // 검색조건이 넘어오면
            builder.and(member.username.eq(usernameCond)) ; // 조건식 생성 + and 로 묶음
        }
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch() ;
    }

    /*
    where 다중 파라미터 사용 **
    : where문 안에서 바로 사용
     */
    @Test
    public void dynamicQuery_WhereParam(){

        // 동적쿼리로 검색하려는 파라미터
        String usernameParam = "member1" ;
        Integer ageParam = 10 ;

        List<Member> result = searchMember2(usernameParam, ageParam) ;
        assertThat(result.size()).isEqualTo(1) ;
    }  // dynamicQuery_BooleanBuilder

    // 어짜피 개발할 때 이거만 봄 !! 위의 booleanBuilder는 이거보고 저거보고 로직 다 읽어야함
    // 얘깥은 경우 아래 eq메서드들은 그렇겠구나 하고 신경안쓰지 로직 점검이 간편해짐
    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
                //.where(userNameEq(usernameCond), ageEq(ageCond))
                // ** 3. querydsl에선 where()에 null이 들어오면 기본적으로 무시가 된다!
                .where(allEq(usernameCond, ageCond))
                .fetch() ;
    }

    //private Predicate userNameEq(String usernameCond) {
    private BooleanExpression userNameEq(String usernameCond) {
        if(usernameCond == null){
            return null ; // 1. 검색조건이 null이면 null을 반환하는데
        }
        return member.username.eq(usernameCond) ; // 2. 검색조건이 null이 아니면 조건식을 반환
    }
    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null ;
    }

    // where 파라미터 방식을 쓰면 메서드를 이렇게 뺄 수 있는데,
    // 그러면 아래처럼 조건식의 조립이 가능 (다시는 안볼,... !)
    // --> mybatis나 다른 방식에선 상상도 못했던 것 !
    // 재활용, 가독성 up ! ex) 광고상태 isValid , 날짜 In --> isServicable()라는 상태(조건 판별식)를 자유롭게 만들수 있다
    private Predicate allEq(String usernameCond , Integer ageCond){
        return userNameEq(usernameCond).and(ageEq(ageCond)) ;
        // 물론 여기서 null 조건 등 고려해줘야함 !
    }
}
