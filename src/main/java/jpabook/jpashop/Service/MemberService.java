package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
// service에서 jpa의 모든 변경사항, 로직은 트랜잭션 안에서 (단위로) 처리되어야함 (lazy loading 등 적용)
// 클래스 레벨에 쓰면, public 메서드들은 다 transaction에 들어감 => 커밋 시점에 데이터다 db에 반영
// javax or sping' ? spring 사용 권장 (사용 가능한 option 많아짐)
// @AllArgsConstructor
@RequiredArgsConstructor
// final이 있는 필드만 가지고 생성자 --> 중간에 필요한 다른 일반 필드는 그냥 두고 주입이 필요한 애들만 final을 붙여주기 때문에 깔끔
public class MemberService {

     // @Autowired // field injection (단점 多)
    private final MemberRepository memberRepository ; // final 넣어주면 값이 들어오는지 안들어오는지 컴파일 시점에서 체크 할 수 있음 (생성자 주석처리하면 컴파일 에러 발생 )
    // 단점 多
    // 한번 주입되면 바꿀 수 없음 : private으로 되어있는 필드이기 때문에 접근할 수 없음
    // ==> 자동 주입은 상관없지만, 내가 쓰려는걸 직접 주입하기는 어려움
    // ==> test 케이스 작성 곤란

    /*
    @Autowired // setter DI --> 직접 주입하기가 수월 (test good)
    private void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository = memberRepository ;

        // 단점
        // 사실상 로딩시점 이후 조립을 바꿀 일이 없음
        // --> 모든 생성이 끝나고 setter DI가 호출되기 때문에 컨테이너 구성 딘계(빈 등록 시점)에 여기 담긴 빈을 사용해야하는 경우 사용 못함
    }
    */

/*    // @Autowired // 생성자 DI --> 생성자가 딱 1개인 경우 @Autowired 없어도 자동으로 주입해줌
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;

        // 빈 생성 시점에 초기화 (setter DI 단점 해결)
        // 의존 관계 명확 --> 주입해주지 않으면 빈이 생성되지 않음을 명시적으로 보여줌

    }*/

    // 중복 회원 조회
    // 이 메서드가 필요한 이유 : Member의 pk인 id는 가입한 회원들에 대해 부여되는 index의 개념일 뿐 실제 계정명이 아니다
    // 계정명(user' id) == 'name'  ==> pk인 id를 제외하더라도 name(id)에 대해 중복검사 해줘야 함
    public void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName((member.getName())) ;
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다. ") ;
        } // if
        // 이 경우 multi thread 문제 방어 못함 : 돌아가는 여러개의 was에서 동시에 save 호출하면 동시에 값 넣어지게 되어 중복회원 여부 판단 잘못됨
        // db에서 member에 name 을 unique 제약조건으로 잡는 등 방안 필요
    }  // validateDuplicateMember


    /*
    회원가입
     */
    @Transactional // readOnly 하면 데이터 변경 x --> 메서드 레벨 설정한게 우선순위가 높음
    public Long join(Member member){
        validateDuplicateMember(member) ;  // 중복 회원 검증 --> 예외
        memberRepository.save(member);
        return member.getId() ;
        // persist() (EntityManager)
        // : persistContext에 넣을때 <key, value> 형태로 값을 넣음 --> 이 key값에 id가 들어감
        // --> 영속성 컨텍스트로 넣는 시점엔 자동으로 key(id) 값이 생성되있는게 보장됨
    } // join

    /*
    회원 조회
     */
    // 전체 조회
   //  @Transactional(readOnly = true) // '읽기 전용 트랜젝션' : 리소스를 제한해 성능을 최적화
    public List<Member> findMembers(){
        return memberRepository.findAll() ;
    }

   //  @Transactional(readOnly = true)
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId) ;
    }


} // MemberService
