package jpabook.jpashop.Service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository ;

    /*
    기능 1. 상품 등록
     */
    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    /*
    기능 2. 상품 목록 조회
     */
    public List<Item> findItems(){
        return itemRepository.findAll() ;
    }

    public Item findOne(Long itemId){
        return itemRepository.fineOne(itemId) ;
    } // findOne


    /*
    기능 3. 상품 수정
     */
    @Transactional
    public Item updateItem(Long itemId, String name , int price, int stockQuantity){

        Item findItem = itemRepository.fineOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity( stockQuantity);

        return findItem ;
    }



} // ItemService
