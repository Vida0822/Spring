package tobyspring.helloboot;

import org.springframework.jdbc.core.JdbcTemplate;
import tobyspring.config.MySpringBootApplication;
import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;

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

	/*
	@Bean
	ApplicationRunner applicationRunner(Environment env){
		// 스프링 안에 있는 환경정보를 추상해놓은 Object인 Environment를 주입받음(실행될때 자동으로 )
		return args ->{
			String name = env.getProperty("my.name");
			System.out.println("my.name:" + name);
			// my.name:null --> my.name:ApplicationProperties --> my.name:EnvironmentVariable (System Environment ) --> my.name:SystemProperty
		} ;
	}
	*/

	private final JdbcTemplate jdbcTemplate;

	public HellobootApplication(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@PostConstruct
	void init(){
			jdbcTemplate.execute("create table if not exists hello(name varchar(50) primary key, count int)");
	}


	public static void main(String[] args) {
		// MySpringApplication.run(HellobootApplication.class, args);
		SpringApplication.run(HellobootApplication.class, args); // refactoring
	} // main => 스프링 부트 최종코드 !
} // HellobootApplication
