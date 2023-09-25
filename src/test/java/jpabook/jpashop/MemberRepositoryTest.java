package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest // 이게 있어서 원래 application 실행 안시켜놔도 됨 ? ㅇㅇ !
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository ;

    @Test
    @Transactional // test에 있으면 text 끝난 후 자동으로 db 롤백 해버림 !
    @Rollback(value = false) // 그래서 rollback 해제해버림
    public void testMember() throws Exception{
        // intellij template 활용하면 편함

        Member member = new Member();
        member.setUsername("heemin");
        Long save = memberRepository.save(member);

        Member findMember = memberRepository.find(save);

        Assertions.assertThat(findMember.getID())
                .isEqualTo(member.getID()) ;
        Assertions.assertThat(findMember.getUsername())
                .isEqualTo(member.getUsername()) ;

        // 여기까지만 하면 에러 : transaction 없음
        // : jpa에서 동작하는 모든 db 작업은 transaction 에서 작동해야한다

        Assertions.assertThat(findMember)
                .isEqualTo(member) ;
        // true : member(등록한거) == findMember(조회한거) : 같은 영속성 컨텍스트 (Id(식별자)값이 같으면 같은 entity 로 식별) --> 1차 caches 에서 찾아서 준거

    }

}