package tobyspring.helloboot;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class HelloDecorator implements HelloService{
    // HelloService를 의존함과 동시에 다른 클래스(HelloService)를 의존

    private final HelloService helloService ;
    
    public HelloDecorator(HelloService helloService){
        this.helloService = helloService;
    }
    // HelloService 구현 클래스는 2개 ! 어떻게 SimpleHelloService 지정하지?

    @Override
    public String sayHello(String name) {
        return "*"+helloService.sayHello(name)+"*" ; // 꾸며주는 기능 추가
    }


}
