package com.example.querydsl.repository;

import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDto;
import com.example.querydsl.dto.QMemberDto;
import com.example.querydsl.dto.QMemberTeamDto;
import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QTeam;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.querydsl.entity.QMember.member;
import static com.example.querydsl.entity.QTeam.*;
import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
// 1. 여기에 @Component있음
// --> MemberJpaRepository는 자동으로 생성, 빈 등록됨
public class MemberJpaRepository { // entity를 조회하기 위한 data(db)dp에 접근하는 객체

    private final EntityManager em ;
    private final JPAQueryFactory queryFactory ;
    // queryDSL은 jpql을 대체하기 위한것
    // Repository에 JPAQueryFactory 선언해줘서 jpql대신 'queryDSL' 형식으로 바꿔줌 !!
    /*
    EntityManager : Spring 과 함께쓰면 트랜잭션 단위로 다 따로 분리되어서 동작함 (proxy를 주입해서 사용하기 때문에)
    ==> 동시성 문제가 발생하지 않는다
     ㄴ JpaQueryFactory는 전적으로 EntityManager에 의존하기에 얘도 동시성 문제 x (여러군데에서 접근해도 ㄱㅊ)
     */
    
// 2. 이 빈을 생성하기 위한 생성자 이거뿐 (Entity Manager, JPAQueryFactory)
// 빈 생성위해 @Autowired없어도 이 생성자로 자동 생성!
// 그 과정속 필요한 빈을 자기가 갖고있는 빈이면 주입 (em)
 /*
    public MemberJpaRepository(EntityManager em ){ // , JPAQueryFactory queryFactory) { --> 얜 자동구성으로 갖고있는게 아니라 주입 x
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em); // 내부적 생성
    }
 */
/*
public MemberJpaRepository(EntityManager em , JPAQueryFactory jpaQueryFactory ){
    // 자동 구성정보로 등록되어있지 않으니 특정 파일에 bean으로 따로 등록해서 (조립조건 : EntityManager로)
    // ==> 주입받음
    this.em = em;
    this.queryFactory = jpaQueryFactory;
}
=> 이 방식으로 바뀌면 @RequiredArgsConstructor로 대체가능 (자동으로 기본형식 생성자 (this = 매개변수) 로 만들어주니까 ! )
    (but test 코드상에선 좀 불편하겠찌? ㅋㅋ  )
*/

    public void save(Member member){
        em.persist(member);
    }

    public Optional<Member> findById(Long id){
        Member findMember = em.find(Member.class, id) ;
        return Optional.ofNullable(findMember) ; // null이 될 수 있는 findMember 반환
    }

    /*
   - jpql 버전 -
    */
    public List<Member> findAll(){
        return em.createQuery("select m from Member m" , Member.class)
                .getResultList() ;
    }

    public List<Member> findByUserName(String username){
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getResultList() ;
    }

    /*
    query dsl 버전
     */
    public List<Member> findAll_Querydsl(){
        return queryFactory
                .selectFrom(member) // QMember = Member '테이블' --> alias : member (default)
                .fetch();
    } // findAll_Querydsl

    public List<Member> findByUsername_Querydsl(String username){
        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username)) // member(alias).username = username
                .fetch() ;
    } // findByUsername_Querydsl


    /*
    동적 쿼리 - boolean
     */
    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
        /*
        [good 설계] 
        db조회로 반환 받을 dto : MemberTeamDto
        검색 조건으로 넣을 dto : MemberSearchCondition
         */
        
        BooleanBuilder builder = new BooleanBuilder();
        // if!(null, "") --> 원래 null로 들어오는게 우리 셀곈데 보통 ""로도 많이 들어옴 ! (둘다 '검색어 없음'으로 인식해야함)
        // => StringUtils의 hasText 사용 (		return (str != null && !str.isEmpty() && containsText(str)); )

        // user 이름으로 검색
        if(hasText(condition.getUsername())){ // username 검색어가 null이나 ""가 아니면
            builder.and(member.username.eq(condition.getUsername())) ; // 조건문 생성
        }

        // 팀 이름으로 검색
        if(hasText(condition.getTeamName())){ // teamname 검색어가 null이나 ""가 아니면
            builder.and(team.name.eq(condition.getTeamName())) ; // 조건문생성
        }

        // 특정 나이 이상 검색
        if(condition.getAgeGoe() != null){
            builder.and(member.age.goe(condition.getAgeGoe())) ; // goe : >=
        }

        // 특정 나이 이하 검색
        if(condition.getAgeLoe() != null){
            builder.and(member.age.loe(condition.getAgeLoe())) ; // goe : >=
        }

        return queryFactory
                .select(new QMemberTeamDto(
                        // 결과 dto로 반환 : builder로 동적쿼리 편하게 만느는 동시에 성능 최적화
                        // 그 방법 중 Q 타입으로 반환 (사실상 dto의 entity화...)
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder) //**
                .fetch();
    } // searchByBuilder

    public List<MemberTeamDto> searchByWhereParam(MemberSearchCondition condition){

        return queryFactory
                .select(new QMemberTeamDto( // builder로 동적쿼리 편하게 만느는 동시에 성능 최적화
              //  .selectFrom(member( // select projection이 달라져도 (이 case는 dto --> entity ) 아래 코드 안바꿔도됨 (재활용 good)
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernaemEq(condition.getUsername()), // 메서드 <-- 검색조건 中 멤버 name
                        teamNameEq(condition.getTeamName()), // 메서드 <-- 검색조건 中 팀 name
                     //  ageGoe(condition.getAgeGoe()),
                     //  ageLoe(condition.getAgeLoe())
                        ageBetween(condition.getAgeLoe(), condition.getAgeGoe())
                       )
                .fetch();
    }

   // private Predicate usernaemEq(String username) {
    private BooleanExpression usernaemEq(String username) { // 나중에 composition 생각할거면 이게 나음
       // return isEmpty(username)? null : member.username.eq(username) ; // 부정
        return hasText(username)? member.username.eq(username) : null ; // 긍정
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null ;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null ;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.goe(ageLoe) : null ;
    }

    // 함수 composision , 조립하기 너무 good ! ==> 여러가지 검색조건 수정, 추가되더라도 조건 판별식 하나더 만들고
    // 합성된 조립에 그냥 하나 더 넣어주기만 하면됨
    private BooleanExpression ageBetween(int ageLoe, int ageGoe){
        return ageGoe(ageLoe).and(ageGoe(ageGoe)) ;
        // member.age.goe(ageGoe).and member.age.goe(ageLoe)
        // null or 조건식(ageLoe 보다 작고) ^ null or 조건식 (ageGoe보다 큰)
    }
} // class
