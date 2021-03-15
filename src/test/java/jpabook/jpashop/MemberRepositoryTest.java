package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {
    // @Autowired MemberRepository memberRepository;

    @Test
    @Transactional   // Spring 에서 제공하는 Transactional 사용 / JPA 는 트랜잭션 안에 있어야 하기 때문에 추가 / DB에 저장 되지 않은 이유는, 테스트 코드에서는 테스트 코드 실행 후 롤백하기 때문
    @Rollback(false) // 테스트 코드 종료 후에도 데이터를 보존하고자 한다면, Rollback 옵션 false
    public void testMember() throws Exception {
        /*// given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId()); // 생성한 Member 의 Id 와 저장된 findMember 의 Id 가 같은지 확인
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());*/
    }
}