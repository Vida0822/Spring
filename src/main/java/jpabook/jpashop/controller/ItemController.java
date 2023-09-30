package jpabook.jpashop.controller;

import jpabook.jpashop.Service.ItemService;
import jpabook.jpashop.Service.UpdateItemDto;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService ;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm" ;

    } // createForm

    @PostMapping("/items/new")
    public String createBook(BookForm form){
        Book book = new Book();

        // 이렇게 setset하는건 좋지 않은 설계 !  --> create method (생성 메서드)로 비즈니스 로직 넘겨서 setter 없애주는게 좋은 설계
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/" ;

    } // createBook

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items) ;
        return "items/itemList" ;
    } // list

    @GetMapping("/items/{itemId}/edit") // pathVariable
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId) ;

        BookForm form = new BookForm() ;
        form.setId(item.getId()); ;
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());
        // multiline selector

        model.addAttribute("form", form) ;
        return "items/updateItemForm" ;
    } // updateItemForm

    @PostMapping("items/{itemId}/edit") // pathVariable
    public String updateItem(@PathVariable Long itemId , @ModelAttribute("form") BookForm form){
        // @PathVariable로 넘어온 값을 String itemId 로 받는다
        /*
        주의 ! url에서 itemId 를 맘대로 바꿔서 넘길 수 있음
        --> backend던 front건 이 아이템에 대한 수정 권한이 있는지 '권한 검사' 수행해줘야함 (session )
         */

        /*
        Book book = new Book();
        book.setId(form.getId()) ;

        [변경 감지와 병합 (merge)]
        Book: 새로 생긴 객체긴 하지만 기존의 식별자를 사용하기 때문에 이 객체는 이미 jpa 로 인해 db에 저장되어있는 data
         --> jpa : 관리 x (변경 감지 x) -- 객체의 값이 바뀌어도 실제 db에는 적용 x

         ㄴ 준영속 엔티티를 수정하는 두가지 방법
         1) 변경감지기능 적용 **
            영속 entity를 가져와 그 값을 변경 (기존의 영속 엔티티를 활용)

         2) 병합 ; 머지 (실무에선 거의 x)
            ㄴ ItemRepository --> !item==null --> merge ()
             : 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용
             ==> 위에거랑 거의 똑같음
                (그 pk로 1차캐시 -> db에서 영속 entity 찾아서 값을 바꾸고 반환! )
                ㄴ 이때 ! param으로 넣어준 애 자체는 준영속임 (안바뀜) !  반환된 애들이 바ㅟㄴ거임

            * 병합은 선택을 할 수없어서 주의 ex) isbn -> 변경 x --> null 로 값 바꿈
           // book.setPrice()  --> price 가 null 이 됨

           ㄴ 조금 귀찮더라도 직접 변경감지 사용 : 내가 설정한 필드만 변경 되게끔 (한땀한땀...)

         */
  //      book.setName(form.getName()) ;
  //      book.setPrice(form.getPrice());
  //      book.setStockQuantity(form.getStockQuantity());
  //      book.setAuthor (form.getAuthor()) ;
  //      book.setIsbn (form.getIsbn()) ;
  //      itemService.saveItem(book);
  //     > 어설프게 entity 안만들고 필요한 parameter만 딱딱 받음 : 명확하게 전달해야할 것만 전달

        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity()) ;
       // itemService.updateItem(itemId, UpdateItemDto() ) ;
        return "redirect:/items" ;

    } // updateItemForm

}
