package tobyString.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tobyspring.helloboot.HelloRepository;
import tobyspring.helloboot.HelloService;

import java.util.stream.IntStream;

@HellobootTest
public class HelloServiceDBCountTest {

    @Autowired
    HelloService helloService;
    @Autowired
    HelloRepository helloRepository;

    @Test
    void sayHelloIncreaseCount()
    {
        IntStream.rangeClosed(1,10).forEach(count->{
            helloService.sayHello("Toby") ;
            Assertions.assertThat(helloRepository.countOf("Toby")).isEqualTo(count) ;
        });
    }
}
