package tobyString.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import tobyspring.helloboot.HelloRepository;

@HellobootTest
public class HelloRepositoryTest {

    @Autowired
    HelloRepository helloRepository ;

    @Autowired
    JdbcTemplate jdbcTemplate ;

/* DB 만드는 작업 여기 말고 real project application 실행시 DB 만들도록 변경
    @BeforeEach
    void init(){ // 내장형 DB를 실행할때는 애플리케이션을 시작할때 테이블을 생성하고 데이터를 초기화하는 작업 필요
        jdbcTemplate.execute("create table if not exists hello(name varchar(50) primary key, count int)");
    }
*/
    @Test
    void findHelloFaild(){
        Assertions.assertThat(helloRepository.findHello("Toby")).isNull();
         // 반환하는 값이 null인지 체크 체크 => 조회되는 결과가  없으면 null 이 아닌 예외를 던짐
    }

    @Test
    void increaseCount(){

        Assertions.assertThat(helloRepository.countOf("Toby")).isEqualTo(0);

        helloRepository.increateCount("Toby");
        Assertions.assertThat(helloRepository.countOf("Toby")).isEqualTo(1);
        helloRepository.increateCount("Toby");
        Assertions.assertThat(helloRepository.countOf("Toby")).isEqualTo(2);
    }
}
