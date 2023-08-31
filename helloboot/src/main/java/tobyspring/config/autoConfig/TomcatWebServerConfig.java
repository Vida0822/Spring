package tobyspring.config.autoConfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;
import tobyspring.config.ConditionalMyOnClass;
import tobyspring.config.MyAutoConfiguration;

@MyAutoConfiguration
// @Conditional(TomcatWebServerConfig.TomcatCondition.class)
@ConditionalMyOnClass("org.apache.catalina.startup.Tomcat")
public class TomcatWebServerConfig {

    @Bean("tomcatWebServerFactory") // factory 메서드 실행 -> 얘네도 생성
    @ConditionalOnMissingBean// 이 타입의 bean과 같은 타입의 빈이 있는지 체크하고, 없는 경우에만 자동구성정보의 톰캣 빈 생성
    // DeferredImportSelector 를 구현한 이유 : 유저 구성정보를 자동구성정보보다 먼저 등록되도록
    public ServletWebServerFactory servletWebServerFactory(){
        return new TomcatServletWebServerFactory(8081);
        // tomcat 서버를 생성하고 띄우는데 여러가지 복잡한 작업을 대신해주는 클래스 !
    }
/*
    static class TomcatCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
           //  return false;
            return ClassUtils.isPresent("org.apache.catalina.startup.Tomcat", context.getClassLoader()) ;
        } // matches
    } // TomcatCondition

    */

}
