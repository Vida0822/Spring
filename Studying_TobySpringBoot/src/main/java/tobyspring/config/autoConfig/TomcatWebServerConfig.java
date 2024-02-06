package tobyspring.config.autoConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import tobyspring.config.ConditionalMyOnClass;
import tobyspring.config.EnableMyConfigurationProperties;
import tobyspring.config.MyAutoConfiguration;

@MyAutoConfiguration
// @Conditional(TomcatWebServerConfig.TomcatCondition.class)
@ConditionalMyOnClass("org.apache.catalina.startup.Tomcat")
// @Import(ServerProperties.class)
@EnableMyConfigurationProperties(ServerProperties.class)
public class TomcatWebServerConfig {
/*
    @Value("${contextPath:}") // placeholder => 에러 ! 문자열 그대로 추가됨 : 이 치환 기능은 스프링의 기본기능이 아니기 때문에 후처리 기능으로 추가해줘야함 =>
    String contextPath;

    @Value("${port:8081}")
    int port; // 만약 이 값을 지정하지 않고(properties에 안쓰면)  띄우면 에러 => default값을 지정해줘야함 ':8081'
*/
    @Bean("tomcatWebServerFactory") // factory 메서드 실행 -> 얘네도 생성
    @ConditionalOnMissingBean// 이 타입의 bean과 같은 타입의 빈이 있는지 체크하고, 없는 경우에만 자동구성정보의 톰캣 빈 생성
    // DeferredImportSelector 를 구현한 이유 : 유저 구성정보를 자동구성정보보다 먼저 등록되도록
    public ServletWebServerFactory servletWebServerFactory( /*Environment env */ ServerProperties properties){
        TomcatServletWebServerFactory factory =  new TomcatServletWebServerFactory(8081);
        // tomcat 서버를 생성하고 띄우는데 여러가지 복잡한 작업을 대신해주는 클래스 !

        // factory.setContextPath("/app");
        // 이걸 정해주면 모든 서블릿의 Mapping앞에 contextPath 추가 => 그냥 /hello 면 에러 ! /app/hello 로 요청 보내줘야함
        // 이걸 코드로 박아넣지 않고 Enviomnet를 통해 Property 값 지정
        // factory.setContextPath(env.getProperty("contextPath"));
        factory.setContextPath(/*this.contextPath*/ properties.getContextPath());
        factory.setPort(/*port*/ properties.getPort());

        return factory ;

    }

    // 클래스 파일만 생성해선 주입 안됨 <-> 빈으로 등록해야함 : 클래스 위에 또는 @Bean


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
