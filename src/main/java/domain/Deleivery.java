package domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Deleivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address ;

   // @Enumerated(EnumType.ORDINAL)  순서대로 1, 2, 3 --> 중간에 다른 값 생기면 망함 ㅋㅋ... (READY ,xxx ,CAMP) => 절대 쓰면 안되고 꼭 String 으로 써야함
   @Enumerated(EnumType.STRING)
   private DeleveryStatus status ; // READY , CAMP
}
