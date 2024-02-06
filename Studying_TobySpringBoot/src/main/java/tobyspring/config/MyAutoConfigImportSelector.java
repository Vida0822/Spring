package tobyspring.config;

import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyAutoConfigImportSelector implements DeferredImportSelector {

    private final ClassLoader classloader;

    public MyAutoConfigImportSelector(ClassLoader classloader){
        this.classloader=classloader;  // 이렇게 하면 스프링이 class load할때 사용하는 빈 넣어줌
    }
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        /*
        return new String[]{
                // 사용할 configuration 클래스를 Stiring 으로 반환
                // => Spring 이 이걸 읽어 빈 생성 + 사용
                // 소스코드로 직접
                "tobyspring.config.autoConfig.DispatcherServletConfig",
                "tobyspring.config.autoConfig.TomcatWebServerConfig"
        };
        */
        List<String> autoConfigs = new ArrayList<>();

        // 외부 설정파일로 읽기: txt(구성정보 후보들)로 빼서 자바의 파일 읽기 기능
        // 애노테이션 클래스 정보
        /* classLoader : 애플리케이션에서 resource(파일 등)을 읽어올 땐 classLoader 사용
                    => 스프링이 빈을 생성할 떄 사용할 구성정보 파일을 읽어올때 사용하도록 같이 넣어줘야함
                    => 자동구성정보로 쓸 것이기 때문에 규격화된 방식으로 일어와야함
                    => 작성되어있는 클래스파일을 모두 사용하는게 아니라 후보들을 넣어놓고 스마트하게 고름 ~
         */
         ImportCandidates.load(MyAutoConfiguration.class,classloader).forEach(autoConfigs::add);
        // META-INF/spring/full-qualified-annotation-name.imports on the classpath


        // return StreamSupport.stream(candidates.spliterator(),false).toArray(String[]::new) ;
        // return autoConfigs.toArray(String[]::new);
        // Arrays.copyOf(autoConfigs.toArray(), autoConfigs.size(), String[].class) ;
        return autoConfigs.toArray(new String[0]); // 빈 String array를 넣어줌 => 컬렉션의 사이즈보다 작은 array가 반환되면 새로운 배열 생성
    }

}
