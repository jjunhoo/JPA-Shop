package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// @Controller + @ResponseBody > response 를 json / xml 으로 convert
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 올바르지 못한 예 > 회원 생성
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // json 포맷으로 member 데이터 셋팅
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    // 올바른 예 > 회원 생성
    // V1 과 같이 Entity 를 request 로 받은게 아닌 별도의 request 용 DTO 를 만들어 사용 (Entity 변경 시 API 스펙이 자동으로 변경되기 때문에 수정 포인트들이 많아짐)
    // Entity 와 API 스펙은 분리하는 것이 유지보수성이 좋음
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.name);

        Long saveMemberId = memberService.join(member);

        return new CreateMemberResponse(saveMemberId);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        System.out.println("[request] : " + request.getName());

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
