package jpabook.jpashop;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.service.MemberService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional // test case 에서는 자동으로 rollback 옵션이 붙어서 테스트 후 rollback을 날림 그래서 삽입로그 남지 않음
public class MemberServiceTest {

	@Autowired
	MemberService memberService;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Test
//	@Rollback(false)
	public void memberJoin() throws Exception {
		
		// given
		Member member = new Member();
		member.setName("han");
		
		Address address = new Address("서울시", "천호동", "123456");
		member.setAddress(address);
		
		// when
		Long saveId = memberService.join(member);
		
		// then
		assertEquals(member, memberService.findOne(saveId));
		
	}
	
//	@Test(expected = IllegalStateException.class) // Junit4 의 방식 
	@Test
	public void memberCheck() throws Exception {
		
		// given
		Member member1 = new Member();
		member1.setName("han");
		
		Member member2 = new Member();
		member2.setName("han");
		
		// when
		memberService.join(member1);
		
		// Junit5의 방식 	
		assertThrows(IllegalStateException.class, () -> {
			memberService.join(member2); // 해당 부분에서 예외 발생 해야함!
		});
		
		// then
//		fail("예외발생!!!!!");
		
	}
	
}
