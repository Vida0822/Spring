package jpabook.jpashop.Service;

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

    public List<Item> findItems(){
        return itemRepository.findAll() ;
    }

    public Item findOne(Long itemId){
        return itemRepository.fineOne(itemId) ;
    } // findOne

    // 위임만 하는 서비스 ... 사용해야할지 고민할 필요 있음



} // ItemService
