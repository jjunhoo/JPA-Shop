package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor // EntityManager 생성자 주입
public class MemberRepository {

    // @PersistenceContext // JPA 의 EntityManager 에 Spring 에서 생성한 EntityManager 를 주입 받음
    private final EntityManager em;

    // 영속성 컨텍스트에 일단 Member 객체를 넣어둔 후 트랜잭션이 Commit 시점에 DB 에 반영 (INSERT)
    // 저장
    public void save(Member member) {
        em.persist(member);
    }

    // 단건 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 다건 조회
    // JPQL
    public List<Member> findAll() {
        // Entity 객체를 대상으로 쿼리
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
