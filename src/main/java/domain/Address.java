package domain;

import javax.persistence.Embeddable;

@Embeddable // 얜 내장 타입이다
public class Address {

    private String city ;
    private String street ;
    private String zipcode ;
}
