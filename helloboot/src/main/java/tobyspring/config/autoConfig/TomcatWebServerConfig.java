package tobyspring.config.autoConfig;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatWebServerConfig {
    @Bean // factory 메서드 실행 -> 얘네도 생성
    public ServletWebServerFactory serverFactory (){
        return new TomcatServletWebServerFactory(8081);
    }

}
