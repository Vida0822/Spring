package tobyString.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;


@HellobootTest
// @Rollback(false)
public class JdbcTemplateTest {
    @Autowired
    JdbcTemplate jdbcTemplate ;

    @BeforeEach
    void init(){ // 내장형 DB를 실행할때는 애플리케이션을 시작할때 테이블을 생성하고 데이터를 초기화하는 작업 필요
        jdbcTemplate.execute("create table if not exists hello(name varchar(50) primary key, count int)");

    }

    @Test // 내 데이터를 넣고 조회해보는 쿼리
    void insertAndQuery(){
        jdbcTemplate.update("insert into hello values(?,?)","Toby",3 );
        jdbcTemplate.update("insert into hello values(?,?)","Spring", 1 );

        Long count = jdbcTemplate.queryForObject("Select count(*) from hello", Long.class) ;
        Assertions.assertThat(count).isEqualTo(2);
    }

    @Test // 내 데이터를 넣고 조회해보는 쿼리
    void insertAndQuery2(){
        jdbcTemplate.update("insert into hello values(?,?)","Toby",3 );
        jdbcTemplate.update("insert into hello values(?,?)","Spring", 1 );

        Long count = jdbcTemplate.queryForObject("Select count(*) from hello", Long.class) ;
        Assertions.assertThat(count).isEqualTo(2); // 만약 Rollback 되지 않으면 테스트 실패 (개수가 4개가 되니까)
    }
}

