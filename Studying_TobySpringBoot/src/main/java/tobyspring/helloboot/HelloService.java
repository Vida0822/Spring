package tobyspring.helloboot;

public interface HelloService {
    String sayHello(String name) ;  // sayHello


    default int countOf(String name){
        return 0; // 구현을 안하면 0을 반환
    }
}
