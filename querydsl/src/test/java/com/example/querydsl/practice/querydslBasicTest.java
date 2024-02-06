package com.example.querydsl.practice;

import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QMember;
import com.example.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static com.example.querydsl.entity.QMember.member;
import static com.example.querydsl.entity.QTeam.team;
import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class querydslBasicTest {

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

    @Test
    public void startJPQL(){

        // member1 을 찾아라
        String qlString = "select m from Member m where m.username = :username" ;
        // intellij 가 좋은거라 빨갛게 표시해주긴하지만 실행은됨 (경고느낌) => querydsl은 아예 실행이 안됨 (compile 안됨)

        QMember m1 = new QMember("m1") ;
        Member findMember = em.createQuery(qlString, Member.class) // 조회할 클래스 (객체)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1") ;

    } // startJPQL

    @Test
    public void startQuerydsl(){
        //package com.querydsl.jpa.impl;
        //QMember m =  QMember.member ;
        // new QMember("m") ;
        // --> m : table 알리아스 주는거 (select m from m where ~~ )
        //         ㄴ 같은 테이블 조인하는 등 앨리어스 반드시 다르게 줘야하는 경우만 사용

        // 권장 : import com.example.querydsl.entity.QMember;
        // QMember안에 있는 member 그냥 쓸 수 있음
        // **queryDSL : jpql의 builder 역할을 하는거임
        // (한눈에 보기 쉽고 직관적으로 만드는 디자인 패턴의 역할)
        Member findMember = queryFactory // member = MEMBER 테이블
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) // jpa가 preparestatement의 parameter binding 해줌 (db입장에서도 성능상 유리..왜?)
                .fetchOne() ;

        assertThat(findMember.getUsername()).isEqualTo("member1") ;
    } // startQuerydsl

    @Test
    public void search(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(10))
                      //  .and(member.age.eq(10))) --> chain으로 and 하는 것
                .fetchOne(); // 단건 조회 ! (결과 두 이상이면 NotUnique~ )

        assertThat(findMember.getUsername())
                .isEqualTo("member1") ;
    }

    /*
    결과 조회
     */
    @Test
    public void resultFetch(){
        List<Member> fetchList = queryFactory
                .selectFrom(member)
                .fetch();

        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();

        Member fetchedFirst = queryFactory
                .selectFrom(member)
                // .limit(1)
                .fetchFirst();

        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        List<Member> content = results.getResults(); // select *
        results.getTotal(); // select count(memberId)
        results.getLimit()  ;
        results.getOffset() ;
        // 페이징으로 사용 가능 but 복잡한 쿼리일 수록 이렇게 한번에 하면 안되고 페이징용 조회 쿼리 따로 날려야함

        long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }
    /*
    정렬
    1. 회원 나이 내림차순(desc)
    2. 회원 이름 오름차순 (asc)
    단 2에서 회원이름이 없으면 마지막에 출력 (nulls last)
     */
    @Test
    public void sort(){
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0) ; // member5 예상 --> 값이 같으면 pk의 index 순서대로 정렬된다 (원래 그렇듯이)
        Member member6 = result.get(1) ; // member6 예상
        Member memberNull = result.get(2) ; // null 예상

        assertThat(member5.getUsername()).isEqualTo("member5") ;
        assertThat(member6.getUsername()).isEqualTo("member6") ;
        assertThat(memberNull.getUsername()).isNull() ;
    } // sort

    @Test
    public void paging(){
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                // binding for my sql (oracle : rownum으로 바인딩)
                //.fetch();
               .fetchResults(); // 따로 쓰는게 낫다 (content 쿼리는 복잡한데 count 쿼리 단순한경우 where의 복잡한 조건들을 붙일 필요 x)

        assertThat(queryResults.getTotal()).isEqualTo(4) ;
        assertThat(queryResults.getLimit()).isEqualTo(2) ;
        assertThat(queryResults.getOffset()).isEqualTo(1) ;
        assertThat(queryResults.getResults().size()).isEqualTo(2) ;

//        assertThat(results.size()).isEqualTo(2) ;
    }

    /*
    집계 함수
     */
    @Test
    public void aggregation(){
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(), // SUM(age)
                        member.age.avg(), // AVG(age)
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        // quesryDSL Tuple : 여러개의 타입(member 단일 타입) 이 있을 때 가져오는 레코드 단위
        // 위에 적었던거 그대로 꺼내서 쓰면 됨
        // 근데 실무에선 이 방법보단 dto로 뽑아노는 방법을 많이 사용
        assertThat(tuple.get(member.count())).isEqualTo(4) ;
        assertThat(tuple.get(member.age.sum())).isEqualTo(100) ;
        assertThat(tuple.get(member.age.avg())).isEqualTo(25) ;
        assertThat(tuple.get(member.age.max())).isEqualTo(40) ;
        assertThat(tuple.get(member.age.min())).isEqualTo(10) ;
    } // aggregation

    // group by 사용 
    @Test
    public void group() throws Exception{

        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg()) // 각 팀별 member들의 나이 평균
                .from(member)
                .join(member.team, team) // joinColumn
                .groupBy(team.name) // teamA와 teamB로 그루핑
                //.having
                .fetch();

        Tuple teamA = result.get(0) ;
        Tuple teamB = result.get(1) ;

        assertThat(teamA.get(team.name)).isEqualTo("teamA") ;
        assertThat(teamA.get(member.age.avg())).isEqualTo(15) ;

        assertThat(teamB.get(team.name)).isEqualTo("teamB") ;
        assertThat(teamB.get(member.age.avg())).isEqualTo(35) ;
    }

    /*
    조인
     */
    @Test
    public void join(){

        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                // QMember.team & QTeam 조인 : 자동으로 연관관계 맺어진(정확히는 joinColumn으로) 필드로 조인
                //.innerJoin(member.team, team)
                //.leftJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2") ;
    } // join

    /*
    세타 조인
     */
    //  where절 필터링 - 세타조인 (위에서 한건 'equi join)
    // : 카티션 프로덕트(cross join 또는 쉼표로)하고 where 에서 솎아내는 방식의 where
    // 연관관계 없어도 조인가능
    @Test
    public void theta_join(){
        // 회원 이름이 팀 이름과 같은 회원 조회
      em.persist(new Member("teamA"));
      em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA" , "teamB") ;
    } // theta_join

    /*
    on절 기능 : join 대상 필터링(외부조인을 해야할때는 필수)
     */
    @Test
    public void join_on_filtering(){
//    예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
//        -- jpql : select m, t from Member m left join m.team t on t.name = 'teamA'
        List<Tuple> results = queryFactory
                .select(member, team)
                .from(member)
                // equi join(teamId로)과 더불어 팀 name(연관관계 x) 이 'teamA'인지도 추가적으로 필터링
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                //  .where(team.name.eq("teamA")) -- inner join이면 on절로 걸러내나 where이나 결과 똑같 .. ! (익숙한 where 절을 쓰자)
                // 근데 외부조인 필요하면 무조건 on 절 필터링
                .fetch();

        for (Tuple tuple: results) {
            System.out.println("tuple = " + tuple);
        } // for
    }

    // 2. 연관관계 없는 속성으로 조인
    @Test
    public void join_on_no_relation(){
        // 회원 이름이 팀 이름과 같은 회원 조회
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> results = queryFactory
                .select(member , team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name)) // 연관관계 x : member' name 과 team' name
                // .leftJoin(member.team, team)
                // 이걸 빼주면 id 매칭이 없어지고(PK-FK 연결 x)
                // 그냥 이름 매칭(내가 원하는 조건으로 조인)만 들어감
                .fetch();
        
        for (Tuple tuple: results) {
            System.out.println("tuple = " + tuple);
        } // fors
    } // join_on_no_relation

    /*
    패치 조인
     */
    @PersistenceUnit
    EntityManagerFactory emf ;

    @Test
    public void fetchJoinNo(){
        em.flush();
        em.clear();
        // fetchJoin 테스트는 영속성 컨테스트 비우고 하는게 better
        // 영속성 컨텍스트에 team도 member도 없음

        // LazyMode로 로딩기 때문에 이렇게 member 관련 정보만 조회하면  member 정보만 생성함
        // ; team을 참조해야하지만 그렇게 참조하는거 다 만들려하면 너무 오래 걸리기 때문에
        // 그냥 Member에 할당된 영속성 컨텍스트만 뒤짐 !
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                // 만약 이 member와 관련된 모든 team 객체들 한 쿼리로 끌어오고 싶으면 이거 사용
                // ==> team 필드에 얘의 팀 '객체'도 넣어져있음 (활용편 2편참고)
                .where(member.username.eq("member1"))
                .fetchOne();


        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());// 이 Team이 loading된 entity인지 아님 초기화 안된 entity인지 알려주는 함수
        assertThat(loaded).as("패치 조인 미적용").isFalse() ;
    }

    /*
    서브쿼리 조회
    : JPAExpressions 를 사용한다
     */
    @Test
    public void subQuery(){

        QMember memberSub = new QMember("memberSub") ;
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max()) // 40d
                                .from(memberSub) // 같은 table 을 서브쿼리로 쓸 땐 alias 다르게 줘야함
                )).fetch();
        assertThat(result).extracting("age")
                .containsExactly(40) ;
    } // subQuery

    @Test
    public void subQueryGoe(){ // greater or equal

        QMember memberSub = new QMember("memberSub") ;

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg()) // 40d
                                .from(memberSub) // 같은 table 을 서브쿼리로 쓸 땐 alias 다르게 줘야함
                )).fetch();
        assertThat(result).extracting("age")
                .containsExactly(30, 40) ;
    } // subQuery

    @Test
    public void subQueryIn(){ // greater or equal

        QMember memberSub = new QMember("memberSub") ; // 같은 table 을 서브쿼리로 쓸 땐 alias 다르게 줘야함
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age) // 40d
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                )).fetch();
        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40) ;
    } // subQuery

    @Test
    public void selectQueryIn(){ // greater or equal

        QMember memberSub = new QMember("memberSub") ;
        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple: result){
            System.out.print("tuple = " + tuple ) ;
        }
    } // selectQueryIn

    // ※ jpa jpql의 한계점 : from절의 서브쿼리(인라인뷰) 안됨
    // (jpql의 디자인 패턴 개념인 queryDSL 도 당연히 안됨 : queryDSL을 jpql로 변환시키는거니까...)

    // 현대화된 개발 패턴
    // 1. db에선 그냥 정말 필요한 데이터만 가져오는 역할을 하고 각종 비즈니스 로직, 뷰 로직은 어플리케이션 코드로 구현하는게 좋음
    // 2. 단일 쿼리에 대한 미신 : 실시간 트래픽이 중요한 상황이면 쿼리 한번 한번이 아까우니 어쩔수 없다지만
    //                      속도가 그렇게 중요하지 않으면 억지로 복잡한 쿼리 하나 날리는 것보다 2개 날리는게 나음
    /*
    case 문 
     */
    @Test
    public void basicCase(){ // when절의 판별식이 '값'만 들어갈때
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s: result) {
            System.out.println("s= " + s);
        } // for
    } // basicCase

    @Test
    public void complexCase() { // when 절의 판별식이 '조건식'이 들어갈 때
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        // 근데 이런 작업들은 ... db에서가 아니라 비즈니스 로직으로 application에서 하는게 훨씬 ㅏㄴ음
                        // ==> db에선 그냥 10, 23 숫자만 갖고오고 0~20살이다 ~ 이런건 어플리케이션에서 해야함
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println(" s = " + s);
        } // for
    } // complexCase

    /*
    상수/ 문자 더하기
     */
    @Test
    public void constant(){
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple: result  ) {
            System.out.println("tuple = " + tuple);
        }
    } // constant

    @Test
    public void concat(){

        // {username}_{age}
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                // stringValue ** : enum 처리할때도 자주 사용
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result ) {
            System.out.println("s= " + s);
        }
    } // concat


} // class
