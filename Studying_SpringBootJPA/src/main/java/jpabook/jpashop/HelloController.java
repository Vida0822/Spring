package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){ // Model : view로 넘길 수 있는 데이타
        model.addAttribute("data", "hello" ); // <data , hello> --> Model 에 붙여서
        return "hello" ; // forwarding 할 view 페이지 return --> 자동으로 html 붙음
    }
}
