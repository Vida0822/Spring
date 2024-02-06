package com.example.querydsl;

import com.example.querydsl.entity.Hello;
import com.example.querydsl.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
@Transactional
@Commit
// test의 transaction은 기본적을 rollback해버림 (결과 보려면 commit ㅅ겯 gownjdigka)
class QuerydslApplicationTests {

    @Autowired
 //   @PersistenceContext   -- 일반 java 방법으로 context 주입 받음 (Spring 말고 딴거 쓸때 이거 사용)
    EntityManager em ;

    @Test
    void contextLoads() {
        Hello hello = new Hello(); // Hello
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em) ;
        QHello qHello = QHello.hello; //new QHello("h");

        Hello result = query
                .selectFrom(qHello)
                // querydsl로 넘겨줄땐 q파일로 써야함 (QHello로 조회)
                // context에서 hello 객체를 찾는다 --> result : 처음에 저장한 객체 hello 여야함
                .fetchOne();

        Assertions.assertThat(result).isEqualTo(hello) ;
        Assertions.assertThat(result.getId()).isEqualTo(hello.getId()) ;

    }
}
