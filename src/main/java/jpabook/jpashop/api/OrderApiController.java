package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.OrderDto;
import jpabook.jpashop.service.query.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderQueryService orderQueryService;

    /**
     * Entity 직접 조회
     */
    // V1
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        // Hibernate5Module 사용하지 않으면 Lazy 강제 초기화 필드들이 null 로 반환
        for (Order order : orders) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();

            orderItems.stream().forEach(orderItem -> orderItem.getItem().getName()); // Lazy 강제 초기화
        }

        return orders;
    }

    /**
     * Entity -> DTO 변환
     */
    // V2
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        return orders.stream()
                    .map(order -> new OrderDto(order))
                    .collect(toList());
    }

    /**
     * Entity -> DTO 변환 - fetch join 최적화
     *
     * OSIV 적용
     */
    // V3
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        // open-session-in-view 옵션을 false 로 끄더라도 별도의 조회용 Service 를 @Transactional(readOnly = true) 로 만들어두었기 때문에 서비스 가능
        return orderQueryService.ordersV3();
    }

    /**
     * 1 : N : M 의 관계의 쿼리 최적화를 위해 application.yml > default_batch_fetch_size 셋팅
     * OrderItem, Item 을 각각 쿼리하는게 아니라 IN Keyword 를 통하여 한번에 조회
     *
     * 페이징을 사용하면서 쿼리를 최적화 해야하는 경우 (선호 방법)
     */
    // V3.1
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        return orders.stream()
                .map(order -> new OrderDto(order))
                .collect(toList());
    }

    // V4
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    // V5
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // V6
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats;
    }

    /*@Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; // Entity 가 포함되기 때문에 좋지 않은 방법 ! -> OrderItem 조차도 DTO 로 변환하는 것이 좋음 (API 스펙)

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            // Entity 는 별도의 Response DTO 로 만들어주자
            this.orderItems = order.getOrderItems().stream()
                        .map(orderItem -> new OrderItemDto(orderItem))
                        .collect(toList());
        }
    }*/

    // OrderItemDto > Wrapping
    // OrderItem - API 스펙 정의
    /*@Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }*/
}
