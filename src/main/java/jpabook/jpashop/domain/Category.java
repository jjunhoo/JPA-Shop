package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
        joinColumns = @JoinColumn(name = "category_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    ) // 일대다 - 다대일 관계를 풀어낼 category_item 중간 테이블 매핑 (실무에서 사용 X)
    private List<Item> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_id") // 자기 자신 매핑
    private Category parent;

    @OneToMany(mappedBy = "parent") // 자기 자신 매핑
    private List<Category> child = new ArrayList<>();
}
