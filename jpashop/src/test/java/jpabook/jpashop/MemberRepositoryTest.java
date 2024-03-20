package jpabook.jpashop;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Test
	@Transactional
	@Rollback(false)
	public void testMember() throws Exception {
		
		Member member = new Member();
		
		member.setUsername("memberHan");
		
		// when
		Long saveId = memberRepository.save(member);
		Member findMember = memberRepository.find(saveId);
		
		// then
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		
		// 생성한 엔티티와 조회한 엔티티는 같은가?
		Assertions.assertThat(findMember).isEqualTo(member);
		
		System.out.println("findMember == member ========== " + (findMember == member));
		
	}
	
}
