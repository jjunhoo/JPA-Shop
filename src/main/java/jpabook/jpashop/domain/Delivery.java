package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // ORDINAL - 숫자로 사용되며 운영에서 사용할 경우, 문제 발생 여지가 많음
    private DeliveryStatus status; // [READY, COMP]
}
