package tobyspring.config.autoConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import tobyspring.config.MyAutoConfiguration;
import tobyspring.config.MyConfigurationProperties;

import java.util.Map;

@MyAutoConfiguration
public class PropertyPostProcessorConfig {

    @Bean
    BeanPostProcessor propertyPostProcessor(Environment env){
        return new BeanPostProcessor() { // 익명 클래스
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                // Bean Object 초기화가 끝난 다음에, 이 빈 오브젝트 프로세서를 실행해줘 !
                // <-> 이 후처리기는 모든 빈이 생성될때마다 각각 실행됨 !

                MyConfigurationProperties annotation = AnnotationUtils.findAnnotation(bean.getClass(),MyConfigurationProperties.class);
                // 클래스에 붙어있는 애노테이션을 찾아줌 ; MyConfigurationProperties가 붙은 애노테이션이 있으면 찾아서 반환해줘라
                if(annotation == null) return bean ;

                // postProcessor에서 prefix를 뭐로했는지 알아서 binding을 할 때 그 prefix 를 추가해줘야함 !
                Map<String,Object> attrs= AnnotationUtils.getAnnotationAttributes(annotation);
                String prefix = (String) attrs.get("prefix"); // ctrl+alt+V : refactoring - Introduce variabe


                return Binder.get(env).bindOrCreate(prefix, bean.getClass()); // bind를 시작했는데 없으면 Create해서 return
                    // => prefix 붙여서 검사
                // 로직: MyConfigurationProperties 애노테이션이 붙어있는 경우엔 그 Property 값을 바인딩한다



            }
        } ;
    }
}
