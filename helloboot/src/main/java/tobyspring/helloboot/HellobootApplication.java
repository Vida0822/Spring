package tobyspring.helloboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import tobyspring.config.MySpringBootApplication;
import org.springframework.boot.SpringApplication;

// @Configuration  // 이게 Bean 등록정보(스프링 컨테이너 구성정보)를 가진 클래스임을 알려줌 => "아 여기 BeanAnnotaion이 붙은 Factory 메서드가 있겠구나)
// @ComponentScan //  @Component 이 붙은 클래스를 찾아 빈객체로 생성+조립해라 !
@MySpringBootApplication  // 얘를 타고 모든 로직 외 동작들을 실행시킴
public class HellobootApplication {
/*	@Bean
	public ServletWebServerFactory serverFactory (){
		return new TomcatServletWebServerFactory(8081);
	}
	@Bean
	public DispatcherServlet dispatcherServlet(){
		return new DispatcherServlet() ; // 여기선 Spring Container (application Context)를 어떻게 넣어줌?
	}
*/

	public static void main(String[] args) {
		// MySpringApplication.run(HellobootApplication.class, args);
		SpringApplication.run(HellobootApplication.class, args); // refactoring
	} // main => 스프링 부트 최종코드 !
} // HellobootApplication
