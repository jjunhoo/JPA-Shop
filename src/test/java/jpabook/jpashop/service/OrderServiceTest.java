package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        // given
        Member member = getMember();
        Book book = getBook("JPA 표준", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus()); // 상품 주문 시 상태 > ORDER
        assertEquals(1, getOrder.getOrderItems().size()); // 주문한 상품 종류 수가 정확해야 함
        assertEquals(10000 * orderCount, getOrder.getTotalPrice()); // 주문 가격 > 가격 * 수량
        assertEquals(8, book.getStockQuantity()); // 주문 수량만큼 재고 수량이 차감되어야 함
    }

    // TODO - AssertThrows
    @Test
    public void 상품주문_재고수량초과() throws Exception {
        // given
        Member member = getMember();
        Item item = getBook("JPA 표준", 10000, 10);

        int orderCount = 11;

        // when
        Throwable exception = assertThrows(NotEnoughStockException.class, () ->  {
            orderService.order(member.getId(), item.getId(), orderCount); // 11번째 차감을 하는 시점 throw
        });

        // then
        assertEquals(exception.getMessage(), "need more stock");
    }

    @Test
    public void 주문취소() throws Exception {
        // given
        Member member = getMember();
        Book item = getBook("JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus()); // 주문 취소 시 상태는 CANCEL
        assertEquals(10, item.getStockQuantity()); // 주문이 취소된 상품은 재고가 다시 원복되어야 함
    }

    private Book getBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name); // 책 이름
        book.setPrice(price); // 가격
        book.setStockQuantity(stockQuantity); // 재고
        em.persist(book);
        return book;
    }

    private Member getMember() {
        Member member = new Member();
        member.setName("KJH");
        member.setAddrees(new Address("서울", "상암", "12345"));
        em.persist(member);
        return member;
    }
}