package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); // Query 1개 -> N개

        result.forEach(order -> {
            List<orderItemQueryDto> orderItems = findOrderItems(order.getOrderId()); // Query 2개 -> N + 1개
            order.setOrderItems(orderItems);
        });

        return result;
    }

    // ManyToOne > row 수가 증가하지 않기 때문에 한번에 쿼리 (OrderItem, Item)
    private List<orderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.orderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        "  from OrderItem oi" +
                        "  join oi.item i" +
                        " where oi.order.id = :orderId", orderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    // orderItemQueryDto > 컬렉션을 한번에 flat 하게 가져올 수 없기 때문에 별도의 메소드(findOrderItems) 를 통해 직접 셋팅
    // ManyToOne > row 수가 증가하지 않기 때문에 한번에 쿼리 (Member, Delivery)
    // OneToMany > row 수가 증가되므로 별도 메소드를 통해 데이터 셋팅 (OrderItem)
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        "  from Order o" +
                        "  join o.member m" +
                        "  join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }
}
