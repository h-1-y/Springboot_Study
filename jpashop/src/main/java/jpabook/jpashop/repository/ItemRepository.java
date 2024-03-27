package jpabook.jpashop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor // final 이 있는 필드에 한해서 생성자를 만들어줌!!!
public class ItemRepository {

	private final EntityManager em;
	
	public void save(Item item) {
		
		if ( item.getId() == null ) em.persist(item);
		else em.merge(item);
		
	}
	
	public Item findOne(Long id) {
		
		return em.find(Item.class, id);
		
	}
	
	public List<Item> findAll() {
		
		return em.createQuery("select i from Item i", Item.class).getResultList();
		
	}
	
}
