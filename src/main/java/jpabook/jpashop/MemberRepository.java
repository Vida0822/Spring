package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository // entity를 찾아주는거 ? (dao 랑 비슷한거)
public class MemberRepository {

    @PersistenceContext
    // entity 매니저 --> spring boot가 갖고 있는 애 주입해줌 (설정 파일 반영한 매니저 만들어 갖고 있음--> 우린 걍 쓰면 됨)
    private EntityManager en ;

    public Long save(Member member) {
        en.persist(member);
        return member.getID();
        // Tip: 왜 Member을 그대로 반환 안하고 id 만 ?
        // command랑 query를 분리해라 저장을 하고 나면 command 성 리턴값은 만들지 않음 (아이디 정도 있으면 다시 조회 가능)
    }
    public Member find(Long id){
        return en.find(Member.class, id);
    }
}
