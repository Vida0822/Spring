package jpabook.jpashop.controller;

import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.naming.Binding;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService ;

    @GetMapping("/members/new") // 회원가입 폼 페이지를 띄워주는 컨트롤러
    public String createForm(Model model){

        log.info("member controller - get");

        model.addAttribute("memberForm", new MemberForm()) ;
        // Model에 붙여 넘겼기 때문에 화면에선 이 객체에 접근할 수 있다
        return "members/createMemberForm" ;
    }

    @PostMapping("/members/new")
    public String createMember(@Valid MemberForm memberForm, BindingResult result){
        // @Valid --> notEmpty, NotFull 등 validation 기능 편하게 사용 가능
        // 이거땜에 입력 안하고 넘기면
//        Field error in object 'memberForm' on field 'name': rejected value []; codes [NotEmpty.memberForm.name,NotEmpty.name,NotEmpty.java.lang.String,NotEmpty]; arguments
//        [org.springframework.context.support.DefaultMessageSourceResolvable: codes [memberForm.name,name]; arguments []; default message [name]]; default message [회원 이름은 필수 입니다]

        log.info("member controller - post");

        // @valid에 걸려 에러가 나면 원래 controller에서 튕김 , but BingingResult 있으면 그 에러를 result에 담고 아래 코드 실행
        if(result.hasErrors()){
            // timeleaf/spring
            return "members/createMemberForm" ; // devtools --> 자바코드도 재컴파일(저장)해도 실행됨
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member) ; // 바보... 이걸 안해주니까 당연히 db처리 안됨 : join (service) --> save (repository) --> persist

        return "redirect:/" ; // 저장이 되고 나면 재로딩하면 안좋음 --> redirect 첫번째 페이지로
    } // members/new (post)

    @GetMapping("/members")
    public String list(Model model){
        model.addAttribute("members", memberService.findMembers()) ; // refactor - inline variable
        // 얜 entity를 정말 그대로 쓰기때문에 member 그냥 반환 (원래는 dto를 만들어 전달해야함 , api를 만들땐 특히 절대 entity 를 웹으로 반환하면 안됨 , 얜 서버에서 돌아가는 template engine이니까 괜찮다? ? 뭔말)
        return "members/memberList" ;
    }



}
