package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
public class MemberRepository {

    // @PersistenceContext
    // @Autowired // 얘로 대체 가능 (Spring boot) : 원래는 위의 어노테이션으로만 주입 가능  --> 생성자 DI로 대체 가능
    // spring이 PersistenceContext애 갖고있는 entityManager 주입해줌
    // 원래는 직접 생성, 등록해서 꺼내서 써줘야함
    private final EntityManager en ;

    /* 컨테이너를 직접 주입
    @PersistenceUnit
    private EntityUnit en ;
    */

    @Autowired
    public MemberRepository(EntityManager en) {
        this.en = en;
    }

    public void save(Member member){
        en.persist(member);
        // entity 매니저의 메서드, 'persist(객체)' 를 호출
        // 영속성 컨텍스트에 Member 객체 (entity)를 넣어두면, transaction 시점에 db에 insert 쿼리 날라감
        // persistContext에 넣을때 <key, value> 형태로 값을 넣음 --> 이 key값에 id가 들어감
    } // save

    public Member findOne(Long id){
        return en.find(Member.class, id);
        // jpa' 단건조회 메서드 : (조회할 엔티티 --> 테이블 타입 , PK)
    } // findOne

    public List<Member> findAll(){
        return en.createQuery("select m from Member m", Member.class)
                // 'jpql' : sql은 table을 대상으로 쿼리를 한다면 jpql은 엔티티 객체를 대상으로 쿼리 (from 대상이 entity)
                .getResultList();
    } // findAll

    public List<Member> findByName(String name){
        return en.createQuery("select m from Member m where m.name=:name", Member.class)
                // jpql : sql은 table을 대상으로 쿼리를 한다면 jpql은 엔티티 객체를 대상으로 쿼리
                .setParameter("name", name)   // parameter binding으로 특정 이름의 회원만 찾음
                .getResultList();
    } // findByName



} // class
