package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class MemberRepository {


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
    } // save

    public Member findOne(Long id){
        return en.find(Member.class, id);
    } // findOne

    public List<Member> findAll(){
        return en.createQuery("select m from Member m", Member.class)
                .getResultList();
    } // findAll

    public List<Member> findByName(String name){
        return en.createQuery("select m from Member m where m.name=:name", Member.class)
                // jpql : sql은 table을 대상으로 쿼리를 한다면 jpql은 엔티티 객체를 대상으로 쿼리
                .setParameter("name", name)   // parameter binding으로 특정 이름의 회원만 찾음
                .getResultList();
    } // findByName



} // class
