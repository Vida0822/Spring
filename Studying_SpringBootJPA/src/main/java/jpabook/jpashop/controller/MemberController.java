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

    @GetMapping("/members/new")
    public String createForm(Model model){

        log.info("member controller - get");
        model.addAttribute("memberForm", new MemberForm()) ;
        return "members/createMemberForm" ;
    }

    @PostMapping("/members/new")
    public String createMember(@Valid MemberForm memberForm, BindingResult result){

        log.info("member controller - post");

        if(result.hasErrors()){
            // timeleaf/spring
            return "members/createMemberForm" ;
        }
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member) ;

        return "redirect:/" ;
    } // members/new (post)

    @GetMapping("/members")
    public String list(Model model){
        model.addAttribute("members", memberService.findMembers()) ;
        return "members/memberList" ;
    }
}
