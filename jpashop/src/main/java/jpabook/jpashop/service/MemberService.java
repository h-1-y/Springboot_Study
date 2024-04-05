package jpabook.jpashop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
// readOnly = true 조회의 경우 해당 옵션을 선언 해줄 경우 읽기 최적화를 함 읽기의 경우 가급적 추가해주는게 좋음 
// 조회가 아닌 메서드에는 따로 어노테이션 추가!!!
@Transactional(readOnly = true)
// final 이 있는 필드에 한해서 생성자를 만들어줌!!!
@RequiredArgsConstructor
public class MemberService {

	// 구식  
//	@Autowired
//	private MemberRepository memberRepository;
	
	// 요즘 권장하는 방식 ? 
	private final MemberRepository memberRepository;
	
	// 회원 가입
	@Transactional
	public Long join(Member member) {
		
		// 중복 회원 검증
		validateDuplicateMember(member);
		memberRepository.save(member);
		
		return member.getId();
		
	}

	// 회원 데이터 검증 
	private void validateDuplicateMember(Member member) {
		
		List<Member> findMembers = memberRepository.findByName(member.getName());
		
		if ( !findMembers.isEmpty() ) 
			// EXCEPTION
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		
	}
	
	// 회원 전체 조회
	public List<Member> findMembers() {
		
		return memberRepository.findAll();
		
	}
	
	// 회원 한건 조회
	public Member findOne(Long memberId) {
		
		return memberRepository.findOne(memberId);
		
	}
	
	@Transactional
	public void update(long id, String name) {
		Member member = memberRepository.findOne(id);
		member.setName(name);
	}
	
}
