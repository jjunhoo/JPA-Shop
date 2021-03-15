package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // order 키워드 때문에 테이블 명시 필요
@Getter @Setter
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id") // 양방향 연관 관계 주인으로 설정 (FK)
    private Member member;

    @OneToMany(mappedBy = "order") // OrderItem 클래스의 order 필드와 매핑
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne // 일대일 관계에서는 양쪽 어디든 FK를 가져도 됨 (가급적 자주 사용되는 자주 조회되는 곳에 주인을 가져가면 편함)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간 > java8

    private OrderStatus status; // 주문상태 [ORDER, CANCEL] > ENUM
}
