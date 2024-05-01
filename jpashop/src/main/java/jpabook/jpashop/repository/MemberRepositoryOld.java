package jpabook.jpashop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor // final 이 있는 필드에 한해서 생성자를 만들어줌!!!
public class MemberRepositoryOld {

//	@PersistenceContext
//	private EntityManager em;
	
	private final EntityManager em;
	
	public void save(Member member) {
		
		em.persist(member);
		
	}
	
	public Member findOne(Long id) {
		
		return em.find(Member.class, id);
		
	}
	
	public List<Member> findAll() {
		
//		List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList();
//		return result;
		
		return em.createQuery("select m from Member m", Member.class)
				.getResultList();
		
	}
	
	public List<Member> findByName(String name) {
		
		// :name <- 매게변수 바인딩    : 을 쓰면 바인딩 가능!! setParameter 추가 !!
		return em.createQuery("select m from Member m where m.name = :name", Member.class)
				.setParameter("name", name)
				.getResultList();
		
	}
	
}
