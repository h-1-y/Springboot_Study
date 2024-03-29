package jpabook.jpashop;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional // test case 에서는 자동으로 rollback 옵션이 붙어서 테스트 후 rollback을 날림 그래서 삽입로그 남지 않음
public class OrderServiceTest {

	@Autowired
	private EntityManager em;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Test
	public void 상품주문() throws Exception {
		
		// given
		Member member = createMember("member1", "경기", "하남", "123-123");
		
		Book book = createBook("JPA BOOK", 10000, 10);
		
		int orderCount = 2;
		
		// when 
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		
		// then 
		Order getOrder = orderRepository.findOne(orderId);
		
		// 상품 주문시 상태는 ORDER 인지 테스트 
		assertEquals(OrderStatus.ORDER, getOrder.getStatus());
		// 주문한 상품 종류의 수 테스트 
		assertEquals(1, getOrder.getOrderItems().size());
		// 주문한 상품의 총 가격이 일치하는지 테스트 
		assertEquals(book.getPrice() * orderCount, getOrder.getTotalPrice());
		// 주문 수량 만큼 재고가 줄어드는지 
		assertEquals(10 - orderCount, book.getStockQuantity());
		
	}
	
	@Test
	public void 상품주문_재고수량초과() throws Exception {
		
		// given
		Member member = createMember("member1", "경기", "하남", "123-123");
		Item item = createBook("JPA BOOK", 10000, 10);
		
		int orderCount = 11;
		
		// when 
		assertThrows(NotEnoughStockException.class, () -> {
			orderService.order(member.getId(), item.getId(), orderCount);
		});
//		orderService.order(member.getId(), item.getId(), orderCount);
		
		// then 
//		fail("재고 수량 부족 예외가 발생 해야한다.");
		
		
	}
	
	@Test
	public void 주문취소() throws Exception {
		
		// given
		Member member = createMember("member1", "aaa", "bbb", "123-123");
		Item item = createBook("BOOK2", 10000, 10);
		
		int orderCount = 2;
		
		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
		
		System.out.println("item.getStockQuantity() ========== " + item.getStockQuantity());
		
		// when 
		orderService.cancelOrder(orderId);
		
		// then 
		Order getOrder = orderRepository.findOne(orderId);
		
		// 주문 취소 상태로 변경 됐는지 
		assertEquals(OrderStatus.CANCEL, getOrder.getStatus());
		// 주문 취소 후 재고수량이 원복 됐는지 
		assertEquals(10, item.getStockQuantity());
		
	}
	
	private Member createMember(String name, String city, String street, String zipcode) {
		
		Member member = new Member();
		
		member.setName(name);
		member.setAddress(new Address(city, street, zipcode));
		
		em.persist(member);
		
		return member;
		
	}
	
	private Book createBook(String name, int price, int stockQuantity) {
		
		Book book = new Book();
		
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		
		em.persist(book);
		
		return book;
		
	}
	
}
