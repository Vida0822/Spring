package jpabook.jpashop.Service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager en ;

    @Test
    public void updateTest() throws Exception{


        Book book = en.find(Book.class , 1L) ;

        // TX
        book.setName("aaa");

        // TX commit
        /* jpa가 setName로 인한 Book(pk=1L) 의 변화사항을 알아서 update 해버림
              ㄴ ('dirty checking' ; 변경 감지 )
         ㄴ order cancel()에서 해줌 !  entity 값만 바꾸니 알아서 entity도 바꾸고 알아서 db 업데이트 해줌 (commit 시점에)
        */
        /*
        ※ 준영속 엔티티인 경우

         */


    }


} // ItemUpdateTest
