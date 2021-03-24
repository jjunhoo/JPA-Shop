package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // order 키워드 때문에 테이블 명시 필요
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 해당 엔티티의 createOrder() 메소드를 사용하여 Order 를 생성하는 것을 강제하기 위하여 생성자 접근 제어자 protected 설정 (Service 레이어에서 Order 객체 생성 불가)
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 양방향 연관 관계 주인으로 설정 (FK)
    private Member member;

    // * orderItems 와 delivery 는 Order 에서만 참조 하고 있기 때문에 CascadeType.ALL 옵션 사용
    // @BatchSize(size = 1000)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // OrderItem 클래스의 order 필드와 매핑
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // 일대일 관계에서는 양쪽 어디든 FK를 가져도 됨 (가급적 자주 사용되는 자주 조회되는 곳에 주인을 가져가면 편함)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간 > java8

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL] > ENUM

    // 해당 엔티티의 createOrder() 메소드를 사용하여 Order 를 생성하는 것을 강제하기 위하여 생성자 접근 제어자 protected 설정 (Service 레이어에서 Order 객체 생성 불가)
    /*
    protected Order() {

    }
    */

    // == 연관 관계 메서드 == //
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // == 생성 메서드 == //
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    // == 비즈니스 로직 == //
    // 주문 취소
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);

        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    // == 조회 로직 == //
    // 전체 주문 가격 조회
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
