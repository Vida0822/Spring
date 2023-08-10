package tobyString.helloboot;

import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration  // 이게 Bean 등록정보(스프링 컨테이너 구성정보)를 가진 클래스임을 알려줌 => "아 여기 BeanAnnotaion이 붙은 Factory 메서드가 있겠구나)
@ComponentScan //  @Component 이 붙은 클래스를 찾아 빈객체로 생성+조립해라 !
public class HellobootApplication {
	@Bean
	public ServletWebServerFactory serverFactory (){
		return new TomcatServletWebServerFactory(8081);
	}
	@Bean
	public DispatcherServlet dispatcherServlet(){
		return new DispatcherServlet() ; // 여기선 Spring Container (application Context)를 어떻게 넣어줌?
	}

	public static void main(String[] args) {
		// MySpringApplication.run(HellobootApplication.class, args);
		SpringApplication.run(HellobootApplication.class, args);
	} // main => 스프링 부트 최종코드 !

} // HellobootApplication
