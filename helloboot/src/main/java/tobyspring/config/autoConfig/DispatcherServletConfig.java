package tobyspring.config.autoConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class DispatcherServletConfig {

    @Bean
    public DispatcherServlet dispatcherServlet(){
        return new DispatcherServlet() ; // 여기선 Spring Container (application Context)를 어떻게 넣어줌?
    }
}
