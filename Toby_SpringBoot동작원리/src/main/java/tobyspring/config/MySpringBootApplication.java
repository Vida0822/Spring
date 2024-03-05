package tobyspring.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
// 원래 디폴트는 클래스 : 클래스 파일 까지는 살아있지만 runtime으로 메모리 로딩할 때는 사라짐
// => runtime(실행) 시에도 살아있게 하려면 Runtime Policy 적용해줘야함
@Target(ElementType.TYPE) // TYPE : Class + Interface + Inner?
@Configuration  // 이게 Bean 등록정보(스프링 컨테이너 구성정보)를 가진 클래스임을 알려줌 => "아 여기 BeanAnnotaion이 붙은 Factory 메서드가 있겠구나)
@ComponentScan
// @Import({TomcatWebServerConfig.class,DispatcherServletConfig.class}) // 다른 패키지의 Config 빈들을 등록하기 위함 (설정 정보로 불러옴)
@EnableMyAutoConfig
public @interface MySpringBootApplication {
}
