package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장 타입을 포함했다는 의미
    private Address addrees;

    @OneToMany(mappedBy = "member") // 연관 관계 (order 테이블의 member 테이블과 매핑) > 양방향 매핑
    private List<Order> ordersList = new ArrayList<>();
}
