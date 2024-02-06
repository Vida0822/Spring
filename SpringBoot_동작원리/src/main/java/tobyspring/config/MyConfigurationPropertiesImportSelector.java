package tobyspring.config;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class MyConfigurationPropertiesImportSelector implements DeferredImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        MultiValueMap<String, Object> attr = importingClassMetadata.getAllAnnotationAttributes(EnableMyConfigurationProperties.class.getName());
        // EnableMyConfigurationProperties 이 애노테이션에 붙은 모든 attribute를 가져옴
        Class propertyClass = (Class) attr.getFirst("value");

        return new String[]{propertyClass.getName()};
        // Import로 직접 ServerProperties 클래스를 가져오는 대신 @EnableMyConfigurationProperties의 element 값으로 프로퍼티값을 대신 읽어오도록 만듬
    }
}
