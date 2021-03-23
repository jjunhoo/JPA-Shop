package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

// 조회 성능 최적화 Practice 를 위한 Dummy Data 생성 > Spring 실행 시 자동 생성
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    // 주문 데이터 생성용
    @Component
    @RequiredArgsConstructor
    @Transactional
    static class InitService {
        private final EntityManager entityManager;
        public void dbInit1() {

            Member member = createMember();
            setMember(member, "userA", "서울", "111");

            // 상품 생성
            Book book = createBook("JPA BOOK", 10000, 100);
            entityManager.persist(book);

            // 상품 생성
            Book book2 = createBook("JPA BOOK2", 20000, 200);
            entityManager.persist(book2);

            // 주문 상품 생성
            OrderItem orderItem1 = OrderItem.createOrderItem(book, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            // 배송 주소 생성
            Delivery delivery = createDelivery(member);

            // 주문 생성
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            entityManager.persist(order);
        }

        public void dbInit2() {

            Member member = createMember();
            setMember(member, "userB", "부산", "222");

            // 상품 생성
            Book book = createBook("JPA BOOK", 20000, 200);
            entityManager.persist(book);

            // 상품 생성
            Book book2 = createBook("JPA BOOK2", 40000, 300);
            entityManager.persist(book2);

            // 주문 상품 생성
            OrderItem orderItem1 = OrderItem.createOrderItem(book, 10000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            // 배송 주소 생성
            Delivery delivery = createDelivery(member);

            // 주문 생성
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            entityManager.persist(order);
        }


        private Member createMember() {
            // 회원 데이터 생성
            Member member = new Member();
            setMember(member, "userA", "서울", "1");
            entityManager.persist(member);
            return member;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private void setMember(Member member, String name, String city, String street) {
            member.setName(name);
            member.setAddress(new Address(city, street, "12345"));
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }
    }
}
