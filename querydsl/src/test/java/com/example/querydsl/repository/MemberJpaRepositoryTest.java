package com.example.querydsl.repository;

import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDto;
import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.Team;
import com.example.querydsl.repository.MemberJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em ;

    @Autowired
    MemberJpaRepository memberJpaRepository ;



    @Test
    public void jpqlTest(){
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get() ;
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJpaRepository.findAll() ;
        assertThat(result1).containsExactly(member) ;

        List<Member> result2 = memberJpaRepository.findByUserName("member1") ;
        assertThat(result2).containsExactly(member) ;
    }
    @Test
    public void queryDSLTest(){
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get() ;
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJpaRepository.findAll_Querydsl() ;
        assertThat(result1).containsExactly(member) ;

        List<Member> result2 = memberJpaRepository.findByUsername_Querydsl("member1") ;
        assertThat(result2).containsExactly(member) ;
    }

    @Test
    public void searchTest(){

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


        MemberSearchCondition condition = new MemberSearchCondition() ;
        // 1. 검색조건으로 username 조회는 안나옴 !
        condition.setAgeGoe(35) ;
        condition.setAgeLoe(40); ;
        condition.setTeamName("teamB");
        // ※ 조건이 다 빠진경우
        // --> error : where 조건에 아무 거름조건이 없으니 '모든 데이터'(수천만개라면?) 를 끌고옴
        // ==> 검색 조건으로 기본적으로 하나정돈 세팅하거나 limit를 두는게 좋음

       // List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition); // 2. 그래도 무사히 실행 (null이면 조건문 자체가 생성 안되니까)
        List<MemberTeamDto> result = memberJpaRepository.searchByWhereParam(condition); // 2. 그래도 무사히 실행 (null이면 조건문 자체가 생성 안되니까)
        assertThat(result).extracting("username").containsExactly("member4") ; // 검색결과로 나온 값이 "member4"여야함
    }

}