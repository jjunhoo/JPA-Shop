package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class) // junit 실행 시 Spring Boot 를 함께 연동시켜 실행
@SpringBootTest // Spring Boot 를 실행한 상태에서 테스트 하기 위해서 필요 (없는 상태에서 DI 실패)
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    // @Rollback(false) // 테스트 코드 종료 후에도 데이터를 보존하고자 한다면, Rollback 옵션 false
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kimjunho");

        // when
        Long savedId = memberService.join(member);

        // then
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test()
    public void 중복_회원_가입_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kimjunho");

        Member member2 = new Member();
        member2.setName("kimjunho");

        // when
        memberRepository.save(member1);
        memberRepository.save(member2);

        // then
        assertThrows(IllegalStateException.class, () -> {});
        fail("예외가 발생해야 함");
    }
}