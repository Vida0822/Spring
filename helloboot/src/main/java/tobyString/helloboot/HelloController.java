package tobyString.helloboot;

import java.util.Objects;

public class HelloController {

    // SimpleHelloService simpleHelloService = new SimpleHelloService();

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    public String hello(String name){
        return helloService.sayHello(Objects.requireNonNull(name));
        // null 이면 예외를 던지고 아니면 값을 그대로 넘김 (null이 아닌경우에만 사용되도록)
    }
}
