package tobyspring.helloboot;

import org.springframework.stereotype.Service;

// @Component
@Service
public class SimpleHelloService implements HelloService {

    private final HelloRepository helloRepository;

    public SimpleHelloService(HelloRepository helloRepository) {
        this.helloRepository = helloRepository;
    }


    @Override
    public String sayHello(String name){
        this.helloRepository.increateCount(name);

        return "Hello"+name;
    } // sayHello

    @Override
    public int countOf(String name) {
        return helloRepository.countOf(name);
    }


}
