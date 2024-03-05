package tobyspring.config.autoConfig;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tobyspring.config.ConditionalMyOnClass;
import tobyspring.config.EnableMyConfigurationProperties;
import tobyspring.config.MyAutoConfiguration;

import javax.sql.DataSource;
import java.sql.Driver;

@MyAutoConfiguration
@ConditionalMyOnClass("org.springframework.jdbc.core.JdbcOperations") // 이 interface 존재하면 bean 생성
@EnableMyConfigurationProperties(MyDatsSourceProperties.class) // MyDatsSourceProperties 이 클래스가 등록이 되어야 프로퍼티 파일이 빈으로 등록되도록 함
@EnableTransactionManagement
public class DataSourceConfig {

    @Bean
    @ConditionalMyOnClass("com.zaxxer.hikari.HikariDataSource")
    @ConditionalOnMissingBean
    DataSource hikariDataSource(MyDatsSourceProperties properties){
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName(properties.getDriverClassName());
        // jdbc 연결위해선 username, url , 비밀번호 등 다양한 정보 필요 => 바로 넣는게 아니라 Property 소스로부터 연결
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());

        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean // Hikari 없으면 이거 씀
    DataSource dataSource(MyDatsSourceProperties myDatsSourceProperties) throws ClassNotFoundException {
        // 엄청 간단 : pool이 없고 매 연결마다 Connection 만듬 (속도 엄청 저하)
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
       
        dataSource.setDriverClass((Class<? extends Driver>) Class.forName(myDatsSourceProperties.getDriverClassName()));
        // jdbc 연결위해선 username, url , 비밀번호 등 다양한 정보 필요 => 바로 넣는게 아니라 Property 소스로부터 연결
        dataSource.setUrl(myDatsSourceProperties.getUrl());
        dataSource.setUsername(myDatsSourceProperties.getUsername());
        dataSource.setPassword(myDatsSourceProperties.getPassword());

        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnSingleCandidate(DataSource.class) // DataSource로 등록되어있는 클래스가 딱 한개만 존재할때 jdbcTempaete 빈 생성
    JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnSingleCandidate(DataSource.class) // DataSource로 등록되어있는 클래스가 딱 한개만 존재할때 jdbcTempaete 빈 생성
    JdbcTransactionManager jdbcTransactionManager(DataSource dataSource){
        return new JdbcTransactionManager(dataSource);
        // JdbcTransactionManager : 직접 사용하지 않고 선험적으로? 존재만 해도 트랜잭션 범위를 정해줌 (직접 변수로 받을 필요 주로 없음)
    }

}
