package com.example.querydsl.controller;

import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local")
// application.yml - local
// main이 실행되면 local 프로파일로 실행 됨
// --> @Profile("local") 여기로 들어와서 동작하고
// --> PostControuctor 실행 : db용 데이터 최기화
@Component // 빈으로 등록
@RequiredArgsConstructor
public class initMember {


    // 초기화 데이터 집어넎는 클래스
    private final InitMemberService initMemberService ;

    @PostConstruct // 빈 생성주기 관련
    // @Transactional
    // 그럼 그냥 Postcontruct에 아래 애들 넣어주면 안되나요?
    // Spring life cycle상 저 두 어노테이션은 구분 : Contruct로 하는 부분 vs transaction 하는 부ㅜㅂㄴ

    public void init(){
        initMemberService.init();
    }

    @Component
    // 이거 안해주면 bean으로 인식 안되서 위의 필드에 주입 안됨
    // 비록 외부 클래스에 @Component가 적용되어 있지만 얘는 내부클래스라도 '클래스' 기 때문에 따로 Bean 등록 해줘야함
    static class InitMemberService{

        @PersistenceContext
        private EntityManager em ;

        @Transactional
        public void init(){
            Team teamA = new Team("teamA") ;
            Team teamB = new Team("teamB") ;
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0 ; 1 < 100 ; i++){
                Team selectedTeam = i % 2 == 0 ? teamA : teamB ;
                em.persist(new Member("member"+i , i , selectedTeam));
            } // for
        }
    }





}
