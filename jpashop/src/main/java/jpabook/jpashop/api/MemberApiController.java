package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;
	
	@GetMapping("/api/v1/members")
	public List<Member> membersV1() {
		
		// ver 1 방식 <- 안좋은 방식 
		// 해당 방식의 경우 entity의 모든 필드가 노출이 된다. 
		// entity가 변경되면 기존의 api 요구 스팩이 달라진다. 
		
		return memberService.findMembers();
		
	}
	
	@GetMapping("/api/v2/members")
	public ResultMembers membersV2() {
		
		// ver 2 entity 자체는 절대 외부로 반환하지 말것 !! 
		// 다른 타입의 객체를 만들어 그것을 밖으로 반환하면 entity가 변해도 api의 스팩이 변할일이 없다... 
		List<Member> findMembers = memberService.findMembers();
		
		List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName())).collect(Collectors.toList());
		
		return new ResultMembers(collect.size(), collect);
		
	}
	
	@Data
	@AllArgsConstructor
	static class ResultMembers<T> {
		private int count;
		private T data;
	}
	
	@Data
	@AllArgsConstructor
	static class MemberDto {
		
		private String name;
		
	}
 	
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		
		Long id = memberService.join(member);
		
		return new CreateMemberResponse(id);
		
	}
	
	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		
		Member member = new Member();
		
		member.setName(request.getName());
		
		Long id = memberService.join(member);
		
		return new CreateMemberResponse(id);
		
	}
	
	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
		
		memberService.update(id, request.getName());
		
		Member findMember = memberService.findOne(id);
		
		return new UpdateMemberResponse(findMember.getId(), findMember.getName());
		
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
	
	// api 만들때 별도의 api 스팩에 맞춰 생성 후 바인딩
	// entity와 api의 스팩을 별도로 분리 
	@Data
	static class CreateMemberRequest {
		private String name;
	}
	
	@Data
	static class CreateMemberResponse {
		
		private Long id;
		
		public CreateMemberResponse(Long id) {
			
			this.id = id;
			
		}
		
	}
	
}
