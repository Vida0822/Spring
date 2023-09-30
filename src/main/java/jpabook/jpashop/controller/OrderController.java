package jpabook.jpashop.controller;

import jpabook.jpashop.Service.ItemService;
import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.Service.OrderService;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService ;
    private final MemberService memberService ;
    private final ItemService itemService ;

    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm" ; // 모든 멤버 & 모든 아이템

    } // createForm

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId
            , @RequestParam Long itemId
            , @RequestParam("count") int count){

        orderService.order(memberId,itemId, count) ;
        /*
        밖에서 조회해 Member, Item 넘겨주면 안되나요 ?
          ㄴ 바깥에선 식별자만 넘겨주고 식별자로 jpa를 활용해 조회하는것 부터 비즈니스 로직에 넘겨주는게 나음
            : Transaction은 서비스부터 적용되기 때문에 밖에서 찾는건 jpa랑 관계가 없어짐 !
         */

        return "redirect:/orders";
    } // order

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch, Model model ) {
        // OrderSearch : 검색 위한 조건 객체

        List<Order> orders = orderService.findOrders(orderSearch);

        model.addAttribute("orders", orders) ;
        // model.addAttribute("orderSearch", orderSearch) ; -- @ModelAttribute --> 자동으로 Model 박스에 담김 !

        return "order/orderList" ;

    } // orderList

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders" ;
    }



} // OrderController
