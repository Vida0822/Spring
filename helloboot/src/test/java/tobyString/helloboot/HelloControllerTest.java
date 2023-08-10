package tobyString.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelloControllerTest {

    @Test
    void helloController(){
        // 생성자에 객체 어떻게 주입?
        HelloController helloController = new HelloController(name -> name);

        String ret = helloController.hello("Test") ;
        Assertions.assertThat(ret).isEqualTo("Test") ;
    }

    // 들어오는 요청 (API 동작) 이 제대로 된다는 보장이 없음 ex) Parameter 없어서 exeption
    // => 각 실패에 대한 Test 도 마련해놓아야한다!
    @Test
    void failsHelloController(){ // Exeption이 발생하면 test 성공
        HelloController helloController = new HelloController(name -> name);
        // 이것도 DI ! (스프링 대신 테스트 코드가 assembler 역할!)

        Assertions.assertThatThrownBy(()->{
           helloController.hello(null);
        }).isInstanceOf(IllegalArgumentException.class) ;

        Assertions.assertThatThrownBy(()->{
            helloController.hello("");
        }).isInstanceOf(IllegalArgumentException.class) ;

    } // assertThatThrownBy





}
