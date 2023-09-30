package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    // 회원가입 폼 (페이지) 전용 객체
    /* 왜 필요 ? 그냥 멤버 객체로 받으면 안됨 ?
        실제 도메인이 필요한 데이터 목록과, 화면의 복잡한 입력값들과 불일치 하는 경우 多
        --> 엔티티에 화면을 처리하는 기능이 점점 많아짐 : 도메인 객체를 써버리면 그 도메인 객체 되게 지저분하게 조건 걸어줘야함 (ex NotEmpty 범벅) - 유지보수 어렵
            (화면을 고치려했더니 핵심 비즈니르 로직이... 비즈니스 로직 고치려니 화면이...)

        --> 깔끔하게 화면과 맞춘 객체를 만들어주고 그 객체에서 필요한 정보 뽑아 도메인 객체 등록, save 하는게 better !
       ==>  Entity --> 최대한 순수하게 유지 !  => 폼 객체나 DTO로(getter, setter만 있는 데이터 전송을 위한 객체!) 사용 !

        ※ api를 만들땐 특히 절대 entity 를 웹으로 반환하면 안됨
           ㄴ entity에 로직 추가 --> api 스펙이 아예 변해버림 ... (불안전한 api)

     */


    @NotEmpty(message = "회원 이름은 필수 입니다")
    private String name ;
    // import 안되는 이유 : 의존추가가 안되어있으면 당연히 import도 안됨...
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
