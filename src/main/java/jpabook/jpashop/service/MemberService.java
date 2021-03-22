package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // JPA 최적화 (성능 향상)
@RequiredArgsConstructor  // final 이 있는 필드들만 가지고 생성자를 생성 (MemberRepository 주입)
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional // (readOnly = false) - JPA 에서 데이터 저장, 변경과 같은 작업을 하는 경우 반드시 Transactional 안에서 사용해야 함
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 valid
        memberRepository.save(member);
        return member.getId();
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단건 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    // 중복 회원 valid
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    public void update(Long id, String name) {
        // 준영속 상태
        Member member = memberRepository.findOne(id); // 변경 감지 > 시작
        member.setName(name);
        // Transaction > commit 순간 flush 를 통해 name 이 update
    }
}
