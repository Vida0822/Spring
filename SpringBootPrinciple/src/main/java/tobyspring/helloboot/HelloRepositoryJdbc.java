package tobyspring.helloboot;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class HelloRepositoryJdbc implements HelloRepository {

    private final JdbcTemplate jdbcTemplate;

    public HelloRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Hello findHello(String name) { // queryForObject : 쿼리를 수행한 조회 결과를 객체로 반환하는 메서드
        try {
            return jdbcTemplate.queryForObject("select * from hello where name='" + name + "'",
                    (rs, rowNum) -> new Hello(rs.getString("name"), rs.getInt("count"))
            );
        }catch (EmptyResultDataAccessException e){
            return  null;
        }

    }

    @Override
    public void increateCount(String name) {
        Hello hello=findHello(name);
        // 검색한 이름으로 조회가 안되면 생성 + 1 up
        if(hello==null) jdbcTemplate.update("insert into hello values (?,?)", name, 1) ;
        else jdbcTemplate.update("update hello set count =? where name=?", hello.getCount()+1, name);

    }
}
