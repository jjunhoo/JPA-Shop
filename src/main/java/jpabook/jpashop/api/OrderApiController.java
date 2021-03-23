package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    // V1 : Entity 직접 조회 방법
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

    // V2
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        return orders.stream()
                    .map(order -> new OrderDto(order))
                    .collect(Collectors.toList());
    }

    // TODO
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        // List<Order> orders = orderRepository.findAll(new OrderSearch());
        return null;
    }

    @Data
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
                        .collect(Collectors.toList());
        }
    }

    // OrderItemDto > Wrapping
    // OrderItem - API 스펙 정의
    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
