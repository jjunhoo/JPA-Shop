package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * 아래와 같이 사용할 경우, 무한 루프 이슈 발생
     *
     * Order - Entity 안에 Member 필드가 있고, Member - Entity 안에 List<Order> orders 필드 컬렉션이 있으므로 계속해서 양방향 호출 발생
     *      => 아래와 같이 Entity 를 Response 로 사용하고자 한다면 둘 중 하나는 @JsonIgnore 처리 해주어야 함
     *      => 하지만, Entity 를 Response 로 사용하는 것은 API 스펙에 종속적이게 되므로 별도의 Response 를 만드는 것이 좋음
     *      => 이외 양방향 매핑으로 인하여 무한 루프를 발생시키는 필드들은 한쪽은 @JsonIgnore 처리를 통해 무한 루프 발생을 해결해야 함
     *              => 1. Hibernate5Module (하지만, Response - DTO 를 생성하여 관리하는 것이 더 나은 설계)
     */

    /* V1. Entity 직접 노출 방법 */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // Entity 지연 로딩을 위한 Hibernate5Module - Dependency 추가
        List<Order> all = orderRepository.findAll(new OrderSearch()); // 모든 주문 내역 조회 (양방향 매핑 이슈)

        // 필요한 필드만 LAZY 로 셋팅하여 조회
        for (Order order : all) {
            order.getMember().getName();        // Lazy 강제 초기화
            order.getDelivery().getAddress();   // Lazy 강제 초기화
        }

        return all;
    }

    /**
     * N + 1 이슈 발생
     *
     * Order 의 개수만큼 SimpleOrderDto 를 생성하면서 회원 / 배송을 각각 N번씩 지연 로딩 조회
     *      => fetch join 을 통하여 해결 가능
     *      => Order    - 1회 조회
     *      => Member   - 2회 조회
     *      => Delivery - 2회 조회
     *      => 총 5회 조회
     */

    /* V2. Entity -> Response DTO 노출 방법 */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // Order Data - 2개
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        return orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Order 조회 시 Order 클래스 안의 Member, Delivery 까지 fetch join 을 통하여 1번 조회
     *      => Member, Delivery 클래스는 fetch = FetchType.LAZY
     *      => 하지만, Order 를 통하여 Member, Delivery 를 조회할 때, 지연 로딩되지 않고 inner join 으로 함께 조회
     *
     *      => 조회 쿼리 1회로 셋팅하여 어느 정도의 최적화는 되었지만, 모든 필드에 대해 select 하는 이슈가 남아있음
     */

    /* V3. fetch join 을 통한 성농 최적화 (Response DTO 적용) */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        return orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
    }

    /**
     * V3 <-> V4
     * Trade Off
     *
     */

    /* V4. fetch join > 사용할 특정 필드만 조회 (DB Network 사용량 감소 효과) */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // Lazy 초기화 (Member - 1번씩 2번 (Order 개수만큼) SELECT)
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress(); // Lazy 초기화 (Delivery - 1번씩 2번 (Order 개수만큼) SELECT)
        }
    }

}
