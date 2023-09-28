package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/*
@Test의 목적
[이전 @Test 사용 안했을 때 기능 점검] (Service, Repository)
웹계층 짜고 (Controller) --> 비즈니스 로직코드 짜고 (Service, Repository)
-->  webApplication 실행해 브라우저 띄우고
--> 테스트 하려는 페이지 까지 손수 이동해 요청 보내고
--> 오류나면 그게 웹 계층 문제인지 (404), 서비스 계층 문제인지 확인하고 (500)
--> 다시 자바 코드 고치고 --> webApplication 재실행하고 --> 페이지 이동하고 --> ...

(그나마 웹 계층은 Controller에 syso 등으로 테스트 가능하지만 Service Repository는 웹계층을 거쳐 테스트되어야함 ... )

 ㄴ 단점
  1. Domain, Controller, Service, Repository 를 따 짜야 테스트 시작 가능 (단계별로 테스트가 아닌 기능이 다 개발되어야 테스트 가능... 제발 안틀렸길 하고 개발할 수 밖에)
   => 오류 발생시 어느쪽이 문젠지 파악 곤란 : 요청 url이 ? db 처리부분이? Service 쪽 자바 오류 ? Repository쪽 어노테이션 잘못 사용?
  2. 조금만 수정해도 webApplication 실행해 브라우저 띄워서 페이지 이동까지 또 다시가야함 (테스트 준비 시간 너무 오래걸림)
    : 이게 서비스가 작아서 다행이지 만약 20 페이지 넘어가야 조회되는 페이지라면...?

[Test 계층 점검]
웹계층 x, 비즈니스 로직 코딩 (Service, Repository로 db에 값을 crud 하는 부분만) 점검 --> 오류나면 해당 비즈니르 로직 부분만 바로 수정 --> 다시 테스트
--> 통과시 웹계층 코딩  --> webAPI 테스트 또는 브라우저로 테스트 --> 오류나면 웹 계층 부분만 수정

 ㄴ 장점
    1. 단계별 테스트 가능 : Repository (ok) -->  Service (ok) --> Controller(ok) --> 전체 API 테스트 (ok)
        => 문제 파악이 빨라지고 수정 범위가 줄어들음
    2. 테스트 시간 및 방법이 간단 : 그냥 실행만 시키면 해당 부분 점검 (브라우저 띄워서 일일히 페이지 이동 --> 웹 오류페이지 메세지에 의존 x)

=> 점검 순서
1. Repository Test :
  Repository의 메서드로 인해 실제 db값이 의도한대로 변경되는가  -> ok ?

2. Service
    1. Repository 메서드 호출이 잘 일어나는가 ?
    2. 추가한 기타 서비스 로직들이 의도한대로 잘 수행되는가 ?
    3. 특수상황에 대해 설정해놓은 예외가 잘 발생하는가?

3. Controller
    1. Service 메서드 호출이 잘 일어나는가?
    2. 요청이 제대로 매핑, 바인딩 되며 응답을 알맞은 뷰 페이지로 잘 보내는가 (리다이렉트, 포워딩 점검)
    3. 특수상황에 대해 설정해놓은 예외가 잘 발생하는가?

4. 전체 API, 브라우저 테스트
    1. 의도한대로 화면에 출력 되는가 ?
    2. 모든 계층이 자연스럽게 연결 되는가 ?
 */

@RunWith(SpringRunner.class) // jUnit 실행할 때 Spring 이랑 엮어서 같이 실행 (integration)
@SpringBootTest // db가 도는것까지 봐야하기 때문에 이 두개가 있어야 --> Application 실행
@Transactional // test에 한해선 rollback
public class MemberServiceTest {

    @Autowired MemberService memberService ;
    @Autowired MemberRepository memberRepository ;
    @Autowired EntityManager en ;

    @Test
    // @Rollback(false)
    public void 회원가입() throws Exception{

        // given : 이런게 주어졌을 때, 건너 받았을 때
        Member member = new Member();
        member.setName("kim");

        // when : 이런 행동을 하면
        Long saveId =  memberService.join(member) ;

        // then : 이런 결과가 나와야한다  ==> 이걸 검증
        en.flush();
        Assert.assertEquals(member, memberRepository.findOne(saveId));
        // @Transactional로 같은 영속성 컨텍스트에서 관리하기 때문
        // ㄴ  영속성 컨텍스트 값에선 PK값이 똑같으면 객체를 새로 생성하지 않고 기존에 있는 객체를 사용하기 때문에
        // 같은 pk값으로 조회한 member 객체는 같은 객체를 가리킨다


    /* 특이한 점
    Insert 쿼리는 나가지 않는다
     : 영속성 컨텍스트에 저장된 member 객체는 트랜잭션이 commit 될 때 flush 형태로 날라가기 때문에
    => 근데 Spring Test는 기본적으로 롤백을 해버림 'Rollback for Transactional Test '
        --> insert는 commit 될때 테스트 가능한데 롤백되면 테스트 불가능

    => 해결
    1. Rollback 속성 false 주기
    or
    2. en.flush() 추가 : rollback 되기 전 영속성 컨텍스트에 있는 변화사항을 db에 반영하면서 (다 비움, 터트림) --> insert 되었던거 돌아옴

    call next value for hibernate_sequence;
    Hibernate:
    insert into member (city, street, zipcode, name, member_id) values (?, ?, ?, ?, ?)
    insert into member (city, street, zipcode, name, member_id) values (NULL, NULL, NULL, 'kim', 1);
*/
    } // 회원가입()

    //@Test
    @Test(expected = IllegalStateException.class)
    // 기대되는 결과 : IllegalStateException --> 이 예외가 테스트 도중 터져서 밖으로 나가면 테스트 성공으로 간주 !
    public void 중복_회원_예외() throws Exception{

        // given
        Member member1 = new Member() ;
        member1.setName("kim");

        Member member2 = new Member() ;
        member2.setName("kim");

        // when
        memberService.join(member1) ;
        //  memberService.join(member2) ; --> 이거 주석시 : java.lang.AssertionError: 예외가 발생 해야 한다.
      /*  try{
            memberService.join(member2) ;
        }catch (IllegalStateException e ){
            return ;
        }  */// 여기서 exception 걸리면 밖으로 나감 --> test 실패 !
        memberService.join(member2) ;

        // then
        Assert.fail("예외가 발생 해야 한다.");
        // 만약 예외가 발생해 밖으로 나가지 않고 아래코딩, 즉 이코딩까지 실행되면 test가 실패한 것으로 간주 !
        // 이거 없으면 그냥 test success 뜸 !
        // fail() : 오면 안될곳 까지 코드가 오면 테스트 실패야 !
    } //  중복_회원_예외()

    /*
    테스트 할 때 우리가 설치해둔 h2 db 사용함
    => 그럼 test 할 때 db가 따로 설치돼 있어야 하는게 전제되며,
        Rollback이 된다고 하지만 서비스의 db를 같이 사용함 (test 용 db는 따로 있는게 나음)
        ex) 중복체크를 하러 데이터 하나를 임의로 넣어주려하는데 테스트로 인한 중복이 아닌 실제 중복이 발생할 수 있음
        + 애초에 운영 설정과 테스트 설정은 따로 하는게 나음

    => 테스트용 db를 살짝 띄우는 방법 존재
        : java memory용 db 사용 (inMemory)
        : test/resource/application.yml 추가
        --> test 로직들은 우선적으로 여기 있는 설정정보 사용: 우선권을 가짐  (또 다른 Spring boot project)
        --> jdbc:h2:mem:test 추가 - inMemory db 동작 : h2 db 내리고 돌려도 돌아감 ~ !
           (근데 이런 설정 없어도 됨 ㅋ : spring boot 는 이런거 없으면 자동으로 inMemory 방식으로 돌려버림 )

     */
} // class