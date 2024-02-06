package com.example.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class QuerydslApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuerydslApplication.class, args);
    }

    @Bean // spring bean으로 등록
    JPAQueryFactory jpaQueryFactory(EntityManager em){
        return new JPAQueryFactory(em) ;
        // 조립조건 : EnityManager --> bean으로 생성시 스프링 컨테이너가 갖고있는 EntityManager Bean 주입해줌
    }
}
