package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne - 최적화 (ManyToOne / OneToOne)
 *
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * 아래와 같이 사용할 경우, 무한 루프 이슈 발생
     *
     * Order - Entity 안에 Member 필드가 있고, Member - Entity 안에 List<Order> orders 필드 컬렉션이 있으므로 계속해서 양방향 호출 발생
     *      => 아래와 같이 Entity 를 Response 로 사용하고자 한다면 둘 중 하나는 @JsonIgnore 처리 해주어야 함
     *      => 하지만, Entity 를 Response 로 사용하는 것은 API 스펙에 종속적이게 되므로 별도의 Response 를 만드는 것이 좋음
     *      => 이외 양방향 매핑으로 인하여 무한 루프를 발생시키는 필드들은 한쪽은 @JsonIgnore 처리를 통해 무한 루프 발생을 해결해야 함
     *              => Hibernate5Module
     */

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch()); // 모든 주문 내역 조회

        return all;
    }

}
