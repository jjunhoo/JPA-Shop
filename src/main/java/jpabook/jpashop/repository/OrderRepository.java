package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // TODO : QueryDSL
    public List<Order> findAll(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();
    }

    /**
     * fetch join
     * JPQL
     *
     * Order - Entity 안의 Member / Delivery 필드를 fetch join 을 통하여 한번에 쿼리로 조회
     *  => 재사용성 높음 (Order / Member / Delivery 안의 필드들을 자유롭게 사용 가능)
     *  => 가독성 높음
     *  => 아래 보다 성능이 조금 더 안 좋음
     *
     */
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    /**
     * fetch join
     * JPQL
     *
     * Response DTO 로 내려줄 API 스펙에서 사용할 특정 필드만 매핑하여 조회
     *  => 재사용성 낮음
     *  => 가독성 낮음
     *  => 위 보다 성능이 조금 더 나음
     *
     *  => API 스펙에 종속적
     */
    /*
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                // OrderSimpleQueryDto Construct 에 조회 및 Response DTO 로 사용할 데이터 매핑
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
    }
    */

    public List<Order> findAllWithItem() {
        return em.createQuery(
                // Order 가 Inner Join 으로 2개가 아닌 4개가 나오게 되는데, distinct 를 통해 Order 데이터를 중복 제거 가능
                // DB Query 에서는 Distinct 를 날리지만 중복 제거가 되지 않는데, JPA 에서 Entity 중복 제거

                // * 1:N 컬렉션 fetch join 에서는 페이징 처리 X (Memory 에 올려두고 Sorting 을 하게 되는데, 최악의 경우 OOM 발생)
                "select distinct o from Order o" +
                        "  join fetch o.member m" +
                        "  join fetch o.delivery d" +
                        "  join fetch o.orderItem oi" +
                        "  join fetch oi.item i", Order.class)
                .getResultList();
    }
}
