package tobyString.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class ConfigurationTest {

    @Test
    void configuration() {
        // @Configuration 특징 구현 샘플

        // Bean 객체 두개가 생성되면서 Common Object 가 두개가 생성되어야함
        // <-> 그 두개의 Common은 다른거겠지 ? isSameAs 로 비교하면 다르다고 나옴 (주소값까지 똑같아야함)
      /* isSameAs는 주소값까지 같아야 같다고 나옴 Ex
        //  Assertions.assertThat(new Common()).isSameAs(new Common()) ; // failed

        Common common = new Common();
        Assertions.assertThat(common).isSameAs(common) ; // success
      */
        /*
        MyConfig myConfig = new MyConfig(); // 얘는 @Configurarion을 실행시킨게 아니라 그냥 객체를 생성하면서
        Bean1 bean1 = myConfig.bean1(); // 객체 생성하는 과정에 Bean1, Bean2 생성하고 빈으로 등록하는 거임
        Bean2 bean2 = myConfig.bean2();

        Assertions.assertThat(bean1.common).isSameAs(bean2.common);// failed
        */

        // But, @Configuration 실행, 즉 구성정보로 사용하면 동작 방식이 달라진다 !
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(MyConfig.class); // 설정정보 파일로 등록
        ac.refresh();

        Bean1 bean1 = ac.getBean(Bean1.class);
        Bean2 bean2 = ac.getBean(Bean2.class);

        Assertions.assertThat(bean1.common).isSameAs(bean2.common); // success
        // success : 구성정보로 사용했을 땐 두개의 Bean 애들이 생성될때 주입되는 Common 이 같은애다 !

        /*
        기본적으로 proxyBeanMethod 가 true 면 MyConfig가 직접 등록되는게 아니라 프록시 빈을 앞에 두고, 그게 빈으로 등록됨
        => 즉 이 proxyBeanMehod의 진행방식 때문에 이런 일이 일어나는 것
        => 직접 proxyBeanMethod를 구현해서 알아보자 : MyConfigProxy

        => 일반적인 자바 코드 동작방식과는 다름 !
        : 그래서, Spring 은 이 proxybeanMethod의 동작을 막아 자바처럼 동작하도록 할 수 있음
            : @Configuration(proxyBeanMethods = false)
           => 이 방식을 유용하게 활용한 코드가 아니라면, 시간이 더 들고 무거운 proxy 패턴을 사용할 이유가 없기 때문 (성능 최적화 )
         */

    }

    @Test
    void proxyCommonMethod(){
        MyConfigProxy myConfigProxy = new MyConfigProxy();

        Bean1 bean1 = myConfigProxy.bean1();
        Bean2 bean2 = myConfigProxy.bean2();

        Assertions.assertThat(bean1.common).isSameAs(bean2.common); // success

    }

    static class MyConfigProxy extends MyConfig{
        private Common common ;

        @Override
        Common common() {

            if (this.common == null) this.common = super.common();

            return this.common;

            // 이 필드의 Common이 없으면 슈퍼클래스걸 바당옴
            // target Object에 대한 접근 방식을 제한하는 프록시를 만든거
        } // common
    } // MyConfigProxy



    @Configuration(proxyBeanMethods = false)
    static class MyConfig {
        @Bean
        Common common() {
            return new Common();
        }

        @Bean
        Bean1 bean1() {
            return new Bean1(common());
        }

        @Bean
        Bean2 bean2() {
            return new Bean2(common());
        }

    }

    // Bean 1 <--의존-- Common
    // Bean 2 <--의존-- Common
    // 싱글톤을 해야하는데 각 @bean으로 빈 등록하면 유지할 수 없음

    static class Bean1 {
        private final Common common;

        Bean1(Common common) {
            this.common = common;
        }

    } // Bean1

    static class Bean2 {
        private final Common common;

        Bean2(Common common) {
            this.common = common;
        }

    } // Bean2


    private static class Common {
    }
}
