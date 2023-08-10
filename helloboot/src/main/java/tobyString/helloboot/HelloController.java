package tobyString.helloboot;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

// @RequestMapping("/hello") // 이 안에 매핑 정보가 담겨져있는 메서드가 있다 (/hello 요청을 처리하는 메서드가 있다)
// @MyComponent
@RestController
public class HelloController { // implements ApplicationContextAware {

    // SimpleHelloService simpleHelloService = new SimpleHelloService();
    private final HelloService helloService;
   //  private ApplicationContext applicationContext ; // 자기 자신이지만 Bean 처럼 관리
    // 생성자를 통해 HelloController가 초기화될 때 final 이면 그 필드도 초기화 되어야하는데
    // ApplicationContextAware 는 생성된 후 메서드 호출(setter)을 통해 주입(조립)되기 때문에
   //private final ApplicationContext applicationContext ;

/*    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 스프링 컨테이너가 초기화 되는 시점에 이거 실행됨
        System.out.println(applicationContext);
        this.applicationContext=applicationContext;
    }
*/
    //이거 있는채로 실행하면 Bean 생성 오류 => 왜?
    public HelloController(HelloService helloService) {  // 주입
    this.helloService = helloService;
    }
   /*
    public HelloController(HelloService helloService, ApplicationContext applicationContext) {  // 주입
        this.helloService = helloService;
        this.applicationContext=applicationContext;

        System.out.println(applicationContext);
    }
*/
  //  @GetMapping("/hello")
    @GetMapping("/hello")
   // @ResponseBody
    // 이거 추가해줘야함 : return 된 String 값을 그대로 응답의 Body에 추가해주는 애노테이션 => @RestController 로 대체
    public String hello(String name){

        if(name == null || name.trim().length() == 0)throw new IllegalArgumentException();

        return helloService.sayHello(name); // 이렇게만 하면 에러! Controller에서 반환하는 String return 값을 스프링은 기본적으로 view 페이지 이름으로 인식
        // null 이면 예외를 던지고 아니면 값을 그대로 넘김 (null이 아닌경우에만 사용되도록)
    } // hello
}
