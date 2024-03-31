package jpabook.jpashop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	
	private final String THIS_VIEW = "members/";
	
	@GetMapping("/members/new")
	public String createForm(Model model) {
		
		model.addAttribute("memberForm", new MemberForm());
		
		return THIS_VIEW + "createMemberForm";
		
	}
	
	@PostMapping("/members/new")
	public String create(@Valid MemberForm form, BindingResult result) {
		
		// 유효성 검사에 대한 처리... 굿!
		if ( result.hasErrors() ) return "members/createMemberForm";
			
		Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
		
		Member createMember = new Member();
		
		createMember.setName(form.getName());
		createMember.setAddress(address);
		
		memberService.join(createMember);
		
		return "redirect:/";
		
	}
	
	@GetMapping("/members")
	public String list(Model model) {
		
		List<Member> members = memberService.findMembers();
		
		model.addAttribute("members", members);
		
		return THIS_VIEW + "memberList";
		
	}
	
}