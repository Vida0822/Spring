package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
// 얜 내장 타입이다(값 타입 클래스)
// getter만 만듬 :변경이 되면 안됨 ! (setter 제공 하면 안됨)
// -->변경하려면 해당 객체로 생성해서 복제해 새롭게 만듬
public class Address {

    private String city;
    private String street;
    private String zipcode;

    /* 에러 !  Class 'Address' should have [public, protected] no-arg constructor
    public Address(String zipcode) {
        this.zipcode = zipcode;
    }
    얘만 해주면 기본생성자 x --> jpa가 생성할 때 reflection이나 proxy 같은 기술을 써야할 때가 많은데
    이런건 기본 생성자가 있어야 가능
     */
    protected Address() {
        // public 은 사람들이 너무 접근을 많이하니 protected까지 jpa는 허용
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
    // 사람들은 아 기본생성자는 protected니까 굳이 쓰지 말아야겠고 얘는 public이 열려있으니까 얘로 생성해서 쓰고
    // 한번 생성자로 값 초기화 하면 변경할 수 없도록 setter을 없애줘야겠다
} // class
