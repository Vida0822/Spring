package tobyspring.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MyConfigurationPropertiesImportSelector.class) // @Enable- 애노테이션의 대부분의 목적: 이 안에 @Import을 다시 넣어 기능을 가진 Configuration 클래스나 Selector을 가져오는 목적
public @interface EnableMyConfigurationProperties {
    Class<?> value();
}
