package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

//    Logger log = LoggerFactory.getLogger(getClass()) ;

    @RequestMapping("/")
    public String home(){
      log.info("home controller"); // 이 로그 찍힌거 확인하니 화면에서 문제 발생한거 알 수 있음
      return "home" ; // home.html
    }
}
