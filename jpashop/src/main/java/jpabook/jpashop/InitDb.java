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
 * 
 *   * JPA1 BOOK
 *   * JPA2 BOOK
 *   
 * * userB
 * 
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
		initService.dbInit2();
		
	}
	
	@Component
	@Transactional
	@RequiredArgsConstructor
	static class InitService {
		
		private final EntityManager em;
		
		public void dbInit1() {
			
			Member member = createMember("김아진", "서울", "천호", "123-123");
			
			em.persist(member);
			
			Book book1 = createBook("JPA1 BOOK", 10000, 100);
			
			em.persist(book1);
			
			Book book2 = createBook("JPA2 BOOK", 20000, 100);
			
			em.persist(book2);
			
			Book book3 = createBook("JPA3 BOOK", 30000, 300);
			
			em.persist(book3);
			
			OrderItem orderItem1 = OrderItem.createOrderItem(book1, book1.getPrice(), 1);
			OrderItem orderItem2 = OrderItem.createOrderItem(book2, book2.getPrice(), 2);
			OrderItem orderItem3 = OrderItem.createOrderItem(book3, book3.getPrice(), 3);
			
			Delivery delivery = createDelivery(member.getAddress());
			
			Order order = Order.createOrder(member, delivery, orderItem1, orderItem2, orderItem3);
			
			em.persist(order);
			
		}
		
		public void dbInit2() {
			
			Member member = createMember("한원용", "경기", "하남", "123-123");
			
			em.persist(member);
			
			Book book1 = createBook("SPRING1 BOOK", 10000, 300);
			
			em.persist(book1);
			
			Book book2 = createBook("SPRING2 BOOK", 40000, 600);
			
			em.persist(book2);
			
			OrderItem orderItem1 = OrderItem.createOrderItem(book1, book1.getPrice(), 10);
			OrderItem orderItem2 = OrderItem.createOrderItem(book2, book2.getPrice(), 23);
			
			Delivery delivery = createDelivery(member.getAddress());
			
			Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
			
			em.persist(order);
			
		}
		
		private Member createMember(String name, String city, String street, String zipcode) {
			
			Member member = new Member();
			
			member.setName(name);
			member.setAddress(new Address(city, street, zipcode));
			
			return member;
			
		}
		
		private Book createBook(String name, int price, int stockQuantity) {
			
			Book book = new Book();
			
			book.setName(name);
			book.setPrice(price);
			book.setStockQuantity(stockQuantity);
			
			return book;
			
		}
		
		private Delivery createDelivery(Address address) {
			
			Delivery delivery = new Delivery();
			
			delivery.setAddress(address);
			
			return delivery;
			
		}
		
	}
	
}