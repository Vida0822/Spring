package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수 입니다")
    private String name ;
    /*이거 해주니 이름 입력 안하면 화면에 빨간 테두리 + 안내 메세지 자동 출력
        Controller에서 에러 --> 그 result (error) 도 함께 MemberForm에 담겨(field가 됨!) html로 다시 넘어감 (Spring boot 기능)

         <input type="text" th:field="*{name}" class="form-control" placeholder="이름을 입력하세요"
                   th:class="${#fields.hasErrors('name')}? 'form-control fieldError' : 'form-control'">
            <p th:if="${#fields.hasErrors('name')}" th:errors="*{name}">Incorrect date</p>

        필드 중 name이라는 에러가 있으면, css+ 그 에러필드의 메세지를 참조해 출력
        (나머지 값들에 뭔갈 입력했으면 그 값들이 Form에 담기고, 그 Form 객체가 에러와 함께 그대로 다시 전달되니 화면에선 그 입력했던 값들이 다시 나타남 )

     */


    private String city ;
    private String street ;
    private String zipcode ;

}
