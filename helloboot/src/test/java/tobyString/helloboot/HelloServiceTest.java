package tobyString.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import tobyspring.helloboot.Hello;
import tobyspring.helloboot.HelloDecorator;
import tobyspring.helloboot.HelloRepository;
import tobyspring.helloboot.SimpleHelloService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Test
@interface UnitTest{
}


@UnitTest
// 에러 : Target에 Method만 있고 애노테이션이 없기 때문
// <-> Annotaion 위에 붙일 수 없음 !
// <-> Meta Annotaion으로 사용할 수 없음
@interface FastUnitTest{

}


public class HelloServiceTest {

    // @UnitTest
    @Test // Test 메서드임을 인식하고 이 메서드 실행
    void simpleHelloService(){
        SimpleHelloService helloService = new SimpleHelloService(helloRepository) ;

        String ret = helloService.sayHello("Test") ;

        Assertions.assertThat(ret) .isEqualTo("HelloTest");

    }

    private static HelloRepository helloRepository =
            new HelloRepository() { // 의존 오브젝트, 협력 오브젝트
            @Override
            public Hello findHello(String name) {
                return null;
            }

            @Override
            public void increateCount(String name) {

            }
        };

    @Test
    void helloDecorator(){
        HelloDecorator decorator = new HelloDecorator(name -> name); // => 인터페이스에 메서드가 하나 추가되서 람다식으로 안됨

        String ret = decorator.sayHello("Test") ;
        Assertions.assertThat(ret).isEqualTo("*Test*");
    }
}
