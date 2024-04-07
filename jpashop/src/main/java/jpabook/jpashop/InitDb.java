package jpabook.jpashop;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;

/**
 *  총 주문 2건
 *   
 * * userA
 *   * JPA1 BOOK
 *   * JPA2 BOOK
 *   
 * * userB
 *   * SPRING1 BOOK
 *   * SPRING2 BOOK
 *   
 */
@Component // component 스캔의 대상이 됨
@RequiredArgsConstructor
public class InitDb {

	private final InitService initService;
	
	@PostConstruct // spring bin이 다 올라오고 난 후 호출을 해줌 
	public void init() {
		
		initService.dbInit1();
		
	}
	
	@Component
	@Transactional
	@RequiredArgsConstructor
	static class InitService {
		
		private final EntityManager em;
		
		public void dbInit1() {
			
			Member member = new Member();
			
			member.setName("userA");
			member.setAddress(new Address("서울", "천호", "123-123"));
			
			em.persist(member);
			
			Book book1 = new Book();
			
			book1.setName("JPA1 BOOK");
			book1.setPrice(10000);
			book1.setStockQuantity(100);
			
			em.persist(book1);
			
			Book book2 = new Book();
			
			book2.setName("JPA2 BOOK");
			book2.setPrice(20000);
			book2.setStockQuantity(100);
			
			em.persist(book2);
			
			OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
			OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
			
			Delivery delivery = new Delivery();
			
			delivery.setAddress(member.getAddress());
			
			Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
			
			em.persist(order);
			
		}
		
	}
	
}