package tobyString.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HelloAPITest {

    @Test
    void helloApi(){
        TestRestTemplate rest = new TestRestTemplate();
        // RestTemplate : API 요청을 호출해 응답을 가져와 수행할 수 있음 (요청과 응답 객체를 갖고 있는 클래스)
        // => 이걸 Test 목적으로 응답값 그대로를 가져오는 클래스 : TestRestTemplate (테스트 용 -> 요청과 응답에 대한 정보가 더 자세히 나타나있음)

        // ResponseEntity : 웹 응답의 모든 요소를 담고 있는 객체 => String : Body 부분이 String임(->Json, Dto..)
        // => 따로 cast 안해줘도 String 으로 돌려줌
        ResponseEntity<String> res
                = rest.getForEntity("http://localhost:8081/app/hello?name={name}", String.class, "Spring") ;



        // 응답 검증 (상태코드, 컨텐츠 타입,응답 바디)
        // => 테스트 하고자 하는 어플리케이션 실행 후 검증 실행해야 함 안그럼 예외 발생 ( I/O error on GET request for "http://localhost:8081/hello": Connection refused)
        Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK) ;

      //  Assertions.assertThat(res.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.TEXT_PLAIN_VALUE) ;
        Assertions.assertThat(res.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith(MediaType.TEXT_PLAIN_VALUE) ;

        Assertions.assertThat(res.getBody()).isEqualTo("*HelloSpring*");
    } // helloApi

    @Test
    void failsHelloApi(){
        TestRestTemplate rest = new TestRestTemplate();

        ResponseEntity<String> res
                = rest.getForEntity("http://localhost:8081/app/hello?name=",String.class) ;

        Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR) ; // 500

    } // failsHelloApi

}
