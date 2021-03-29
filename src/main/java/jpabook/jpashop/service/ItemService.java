package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 우선권은 readOnly
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void setItem(Item item) {
        itemRepository.save(item);
    }

    // 1. Dirty Checking - 변경 감지에 의한 데이터 변경 방법
    // 2. merge - Dirty Checking 코드를 merge 한줄로 적용 가능하지만, 값이 없는 field 를 null 로 모두 update 하기 때문에 아래 코드와 같이
    //            Dirty Checking 을 통하여 Entity 레벨에서 변경 감지를 사용하는 방법이 더 나은 방법
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        // * 준영속 상태 (JPA 를 통해 조회해 온 객체) > 데이터 변경 후 Transactional 에 의하여 commit 시점에 flush 되어서 update
        // * Transaction 안에서 조회 해야 영속 상태로 조회되기 때문에 Dirty Checking (변경 감지) 가능
        Item findItem = itemRepository.findOne(itemId);

        // TODO : 아래와 같이 Entity 레벨에 별도의 수정 method 를 생성하여 수정이 필요한 필드만 수정 가능하도록 하는 것이 명시적이고 장애 유발 방지를 위해 좋음
        //          - 아래와 같은 setter 를 통한 변경은 Anti Pattern
        // findItem.change(price, name, stockQuantity);

        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
    }


    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
