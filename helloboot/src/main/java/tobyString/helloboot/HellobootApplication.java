package tobyString.helloboot;

import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
public class HellobootApplication {

	public static void main(String[] args) {
		TomcatServletWebServerFactory serverFactory = new TomcatServletWebServerFactory(8081);
		// onStartup
		WebServer webServer = serverFactory.getWebServer(servletContext -> {

			// application context -> Spring container
			GenericApplicationContext applicationContext = new GenericApplicationContext();

			// object를 직접 만들어서 넣어줬던 servlet context와 다르게 이것도 가능하지만!
			// 스프링은 일반적으로 어떤 클래스로 bean 객체를 만들건지 설정 정보를 등록해줌
			applicationContext.registerBean(HelloController.class);

			// 자기한테 등록된 설정정보를 반영해 spring container 구성 (빈 객체 생성)
			applicationContext.refresh();

			servletContext.addServlet("frontController"
					, new HttpServlet() {
				@Override
				protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
					/* 공통기능 코딩~ : 인증, 보안, 다국어처리 .. */

					if (req.getRequestURI().equals("/hello")&&req.getMethod().equals(HttpMethod.GET.name())) {

						String name = req.getParameter("name");

						HelloController helloController = applicationContext.getBean(HelloController.class) ;
						// 스프링 컨테이너 안에 HelloController 클래스 타입의 빈 객체가 하나뿐이라면 자료형만 지정해주는것만으로도 해당 빈 객체 호출
						// servlet container는 helloController에 helloController 를 생성하던 뭐하던 신경쓰지 않고 그냥 스프링 컨테이너에서 갖다 씀
						String ret = helloController.hello(name); // HelloController안의 hello()를 실행해 문자열을 리턴받은거임

						resp.setContentType(MediaType.TEXT_PLAIN_VALUE);
						resp.getWriter().println(ret); // 응답 body 에 작성
					} else {
						resp.setStatus(HttpStatus.NOT_FOUND.value()); // 404
					}

				}
			}).addMapping("/*"); // 이때부터 frontController 역할을 맡게 됨
		}) ;

		webServer.start();
	} // main
} // HellobootApplication
