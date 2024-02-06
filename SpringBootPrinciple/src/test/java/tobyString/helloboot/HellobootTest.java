package tobyString.helloboot;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import tobyspring.helloboot.HellobootApplication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HellobootApplication.class)
@TestPropertySource("classpath:/application.properties") // 각각의 Test로 인한 DB 변화가 일어나지 않도록 해줌
// properties Environment 저장은 boot만의 기능 => test를 실행하는 동안 이걸 프로퍼티 파일로 사용하도록
@Transactional
public @interface HellobootTest {

}
