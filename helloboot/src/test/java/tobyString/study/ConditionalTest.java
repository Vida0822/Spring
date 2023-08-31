package tobyString.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConditionalTest {

    @Test
    void conditional(){
        //true
        ApplicationContextRunner contextRunner = new ApplicationContextRunner();
        contextRunner.withUserConfiguration(Config1.class).run(context -> {
            assertThat(context).hasSingleBean(MyBean.class).hasSingleBean(MyBean.class);
            assertThat(context).hasSingleBean(MyBean.class).hasSingleBean(Config1.class);
        }) ;

        // false
        new ApplicationContextRunner().withUserConfiguration(Config2.class)
                .run(context -> {
            assertThat(context).doesNotHaveBean(MyBean.class);
            assertThat(context).doesNotHaveBean(Config2.class);
        }) ;

   //      MyBean bean2 = ac.getBean(MyBean.class); // 예외 발생 => ok
        // ( 부정적 상황에서 메서드가 어떤 값을 던지는지 아는 것 중요 : null? 예외? =>  getBean => 예외)
    } // conditional


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Conditional(BooleanCondition.class)
    @interface BooleanConditional{
        boolean value();
    }

    @Configuration
//     @Conditional(TrueCondition.class)
    @BooleanConditional(true)  // private으로하면 해당 클래스(원본) 자체는 사용 불가능 (객체 생성해야함 - 복사해야함)
    static class Config1{
        @Bean
        MyBean myBean(){
            return new MyBean();
        } // myBean
    } // Config1

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Conditional(BooleanCondition.class)
    @interface FalseConditional{}

    @Configuration
    // @Conditional(FalseCondition.class)
    @BooleanConditional(false) // -al : annotaion , n : condition 구현한 클래스 이름
    static class Config2{
        @Bean
        MyBean myBean(){
            return new MyBean();
        }
    }

    static class MyBean { // static으로 등록하면 이 클래스와 상관없이 그냥 전역적 레벨로 존재 (이 클래스는 그냥 패키지 역할이라고 보면됨 )
    }

    private static class BooleanCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Map<String, Object> annoationAttributes = metadata.getAnnotationAttributes(BooleanConditional.class.getName()) ;
            // 이 condition이 사용되어있는 환경에서 이 annotaion에 붙어있는 attribute를 읽어오는거임
            Boolean value = (Boolean)annoationAttributes.get("value") ; // <value , true or false)
            return value;
        } //
    }
/*
    static class TrueCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return true;
        }
    } // TrueCondition

    static class FalseCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return false;
        }
    } // FalseCondition
*/
}
