package tobyspring.helloboot;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tobyspring.config.autoConfig.TomcatWebServerConfig;

@Configuration(proxyBeanMethods = false) // 얜 Component Scan에 의해 추가
public class WebServerConfiguration {

    @Bean ServletWebServerFactory custormerWebServerFactory(){
        TomcatServletWebServerFactory serverFactory = new TomcatServletWebServerFactory() ;
        serverFactory.setPort(9090); // 톰캣 클래스 제공 이유 : 이전 이것저것 편리한 정보를 지정할 수 있음
        return serverFactory ;
    } // custormerWebServerFactory
}
