package tobyString.helloboot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import tobyspring.helloboot.HellobootApplication;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@HellobootTest
public class DataSourceTest {
    // Spring Container Test : Spring Container을 구성하고, 실제로 그 bean을 가져와서 테스트?
    @Autowired
    DataSource dataSource ; // 해당 스프링빈이 있으면 가져옴
        
    @Test
    void connect() throws SQLException {
         Connection connection = dataSource.getConnection();
         connection.close();
    }
}
