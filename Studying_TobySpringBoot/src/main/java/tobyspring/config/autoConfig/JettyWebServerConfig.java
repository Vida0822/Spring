package tobyspring.config.autoConfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;
import tobyspring.config.ConditionalMyOnClass;
import tobyspring.config.MyAutoConfiguration;

@MyAutoConfiguration
// @Conditional(JettyWebServerConfig.JettyCondition.class)
@ConditionalMyOnClass("org.eclipse.jetty.server.Server")
public class JettyWebServerConfig {
    @Bean("jettyWebServerFactory")
    @ConditionalOnMissingBean
    public ServletWebServerFactory servletWebServerFactory(){ // 얜 수정 안해도 됨 (interface형 참조변수의 이유)
        return new JettyServletWebServerFactory(8081);
    }
/*
    static class JettyCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // ConditionContext : 현재 스프링 컨테이너 환경에 대한 정보를 얻을 수 잇는 클래스
            // + AnnotatedTypeMetadata : Meta 애노테이션으로 사용하고 있는 애노테이션들에 대한 metadata 반환
            // return false; // bean으로 등록할건지, 무시할건지 boolean 으로 반환
          //  return true;
            return ClassUtils.isPresent("org.eclipse.jetty.server.Server" , context.getClassLoader()) ;
        } // matches
    } // JettyCondition

*/
} // JettyWebServerConfig


