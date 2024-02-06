package jpabook.jpashop.api;

import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/*
Rest API 스타일 컨트롤러
 */
//@Controller @ResponseBody
@RestController
/*
    @Controller의 역할은 Model 객체를 만들어 데이터를 담고 View를 찾는 것이지만,
    @RestController는 단순히 객체만을 반환하고 객체 데이터는 JSON 또는 XML 형식으로 HTTP 응답 바디에 담아서 전송
    (리턴되는 객체를 json 형태로 직렬화해서 반환)
     : 백엔드와 프론트엔드의 역할 구분이 심화 되면서 백엔드에선 페이지를 신경쓸 필요 없이 프론트엔드가 요청하는 데이터만 return !
     => 그래서 view 페이지 지정은 필요가 없는 것 !
    (but, 정 필요하다면 반환 객체를 ModelAndView를 사용한다)
*/
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService ;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberv1(@RequestBody @Valid Member member){
        // @RequestBody : 요청 body에 담기는 데이터 (주로 json)를 데이터를 Member 객체로 바꿔줌
        // 근데 이름 같은거 안 넣어도 잘 들어감 --> 제약조건 넣어줘야함 ex) Member에 @NotEmpty

        Long id = memberService.join(member) ;
        return new CreateMemberResponse(id) ;
    }
    /* 문제점 : 엔티티가 수정되면 api 스펙이 바뀜 .... ex) name --> username
                (엔티티를 비롯한 서버 코드는 바뀔 확률이 높은데...그럴때마다 api 스펙 수저 --> 프론트엔드 코드까지 수정...)
            ==> api 스펙에 맞춰서 웹계층에서 사용할 별도의 dto 를 만들어줘야함 !!
            ==> api를 만들 땐 무조건 entity를 파라미터로 받지도, 노출해서도 ㄴㄴ
    */

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberv1(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member() ;
        // member.setUserName(request.getName());// 여기 코드만 바뀌면 api 스펙이 바뀌지 않아도 됨
        member.setName(request.getName());

        Long id = memberService.join(member) ;
        return new CreateMemberResponse(id);
    }
    /*
    장점 : dto 를 받으면 api 스펙을 직관적으로 알 수 있음
    --> 아~ dto 값 중 이름과 주소만 받는 회원가입 방식이구나 !
    ==> api를 만들땐 웹계층에선 dto로 요청, 응답하는게 정석
     */


    @Data
    static class CreateMemberResponse{
        private Long id ;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest{
        @NotEmpty // 웹 계층 제약조건은 dto를 사용
        private String name ;
        public CreateMemberRequest(String name) {
            this.name = name;
        }
        // 에러 : Cannot construct instance of `jpabook
        /*
        원인 : ObjectMapper가 @RequestBody를 바인딩할 때 기본 생성자를 사용
        => AllArgs 생성자가 있어서 기본생성자가 없으니 'Mapper Constructor가 없다' 라고 뜬 것

        해결 : 기본 생성자 생성
         */
        public CreateMemberRequest() {
        }
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2( // 수정은 보통 등록과 api 스펙이 다르다
           @PathVariable("id") Long id,
           @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName()) ;
        // update - command와 쿼리는 같이 있는 것이 좋지 않음 (변경은 변경으로 끝냄)
        Member findMember = memberService.findOne(id) ;
        // 따로 조회하지 않고
        return new UpdateMemberResponse(findMember.getId(), findMember.getName()) ;
    }
    // restAPI (RestController)의 특징 : view 페이지를 반환하는게 아닌 json 형태로 객체만을 반환



    // entity는 lombok 사용 주의 (db와 직접적으로 연결되는 core value 이니 접근 제한)
    // ==> dto는 비교적 자유롭게 막씀
    @Data
    static class UpdateMemberRequest {
    /*
     에러 : non-static inner classes like this can only by instantiated using default, no-argument constructo
     원인 :
     내부 클래스 - static 클래스 , non-static 클래스로 나눌 수 있다
     => non-static 클래스 : inner클래스가 들어있는 외부 클래스, 즉 MemberApiController를 직접 new 생성자로 메모리 할당 받아야 UpdateMemberRequest 생성 가능
     ==> 근데 @RequestBody 애노테이션은 직접 UpdateMemberRequest를 생성해야함 (즉 inner 클래스에 직접 접근)
     ==> static 클래스로 inner 클래스를 만들면 Outer Class를 인스턴스화 할 필요 없이 직접 접근 가능

     해결 : 클래스에 static 키워드를 붙여준다
     */

        private String name ;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id ;
        private String name ;
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){ // List도 객체로서 응답 body에 담긴다
        return memberService.findMembers() ;
    }
    /*
    [
      {
        "id": 5,
        "name": "mem5",
        "address": {
            "city": "판교",
            "street": "11",
            "zipcode": "111111"
        },
        "orders": []
    }
    ] 
    => 문제점 
    1. Entity 객체를 그대로 반환하니 Orders 같은 필요 이상의 정보가 노출이됨
        ㄴ Member Entity에 @JsonIgnore --> 이 정보를 필요로하는 다른 화면, api 스펙에선 어캄 ?
        => 이게 화면 로직을 entity에 포함했을 때 생기는 문제 ! (entity에서 의존관계가 나가버리는 것)
    2. array (배열) 형태로 넘어옴 :
    [
      count = "", --> 이런 형식이면 json 법칙 위반
      { },
    ]
    => 스펙을 확장(변경)할 수 없음 : 유연성이 확 굳어버림

    {
    "count" : 4
    "data" : [ ]
    }
    => 요런식으로 되야함 !
     */

    @GetMapping("/api/v2/members")
    public Result memberV2(){

        List<Member> findMembers = memberService.findMembers() ;
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName())) // Stream - map : Stream에 속한 각 요소들을 특정 타입으로 변환하는 중간 연산
                .collect(Collectors.toList());// 스트림 자체를 List로 반환하는 최종 연산 (Stream --> List )

        return new Result(collect.size() , collect) ; // 이렇게 감싸서 반환해줘야 배열 컬렉션 자체가 응답 데이터가 되는 일 방지 - "result" : [] 가 반환 됨

        /*
    {
    "추가 스펙 : ~ api 스펙 바뀌어도 다른 것들도 들어갈 수 있다 ~
    "data": [
        {
            "name": "mem1" --> 필요한 스펙만 노출 가능 (유지보수도 훨씬 편함 ! 보기도...)
         },
        {
            "name": "mem2"
         }
        ]
    }
    => 웹 계층에서 무조건 dto로 바꿔서 받고 보내기 !
    */

    }

    @Data
    @AllArgsConstructor
    static class Result<T>{ // 래퍼 클래스와 유사 (T 객체만 담을 수 있음) --> 이때 T에는 List 같은 컬렉션도 담을 수 있다
        private int count ;
        private T data ;
        //Result<Member> r = new Result<>(new Category()) ;
        /*
         지네릭 클래스
         : no instance(s) of type variable(s) exist so that Category conforms to Member inference variable T has incompatible bounds
         : equality constraints: Member lower bounds: Category
         Required type: Result<Member>
         Provided: Result<Category>
         */
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name ;
    }
}
