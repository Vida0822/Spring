package tobyspring.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

import java.util.Map;

// 클래스 이름을 읽어와서 해당 클래스가 존재하는지 유무를 boolean 값으로 반환하면 그 boolean 값으로 해당 클래스 빈 객체 생성 여부 결정
public class MyOnClassCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String,Object> attrs =  metadata.getAnnotationAttributes(ConditionalMyOnClass.class.getName()) ;  // 이 애노테이션에 붙은 모든 attribute를 map 형태로 반환
        String value = (String)attrs.get("value") ;

        return ClassUtils.isPresent(value,context.getClassLoader()) ;

    }
}
