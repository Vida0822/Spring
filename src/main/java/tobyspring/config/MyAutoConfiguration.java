package tobyspring.config;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Configuration(proxyBeanMethods = false) // proxyBeanMethods = false로 바꾼 configuration이 적용된다
public @interface MyAutoConfiguration {
    // 이 애노테이션 이름으로 된 설정파일을 만들어 거기에 config 으로 쓸 클래스 목록을 작성해줄거임
}
