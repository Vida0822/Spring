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

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    } // saveItem

    @Transactional
    public Item updateItem(Long itemId, String name , int price, int stockQuantity){
        Item findItem = itemRepository.fineOne(itemId); // 실제 db에 있는 영속 상태 entity를 찾아옴
        findItem.setName(name); // 그 영속성 entity 값을 변경 --> 자동으로 감지 (commit --> flush : 변경 감지)
        findItem.setPrice(price);
        findItem.setStockQuantity( stockQuantity);
     // itemRepository.save(findItem); --> 할 필요 없음 !

//      Item findItem = findItem.change(price, name, stockQuentity ) ;  -> set 사용 안하고 이런걸 사용해야 역추적 (log) 가능
        return findItem ;
    }

    public List<Item> findItems(){
        return itemRepository.findAll() ;
    }

    public Item findOne(Long itemId){
        return itemRepository.fineOne(itemId) ;
    } // findOne

    // 위임만 하는 서비스 ... 사용해야할지 고민할 필요 있음



} // ItemService
