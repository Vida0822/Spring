package tobyspring.helloboot;

public interface HelloRepository {
    // 이걸 구현해 db에 접근하는 서비스는 mybatis도 될 수 있고, jpa가 될수도 있겠지?
    Hello findHello(String name); // 이름으로 찾아 해당 Hello객체 (이름, count)를 반환하는 메서드

    void increateCount(String name) ; // 이름을 찾으면 해당 count 생성

    default int countOf(String name){
        // default 메서드 : interface에 추가할 수 있는 일반 메서드 (Comparator.java 참고)
        // 공통적인 로직을 갖고 있는 메서드는 내용까지 구현해서 상속받은 클래스가 만들어야할 메서드들 줄어들음
        Hello hello = findHello(name);
        return hello==null? 0: hello.getCount();
    }
}
