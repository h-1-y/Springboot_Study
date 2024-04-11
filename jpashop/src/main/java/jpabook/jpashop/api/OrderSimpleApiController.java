package jpabook.jpashop.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;

/**
 * xToOne ( ManyToOne, OneToOne ) 관계에서 성능 최적화를 어떻게 할것 인가... 
 * 
 * Order
 * Order -> Member
 * Order -> Delivery
 * 
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

	private final OrderRepository orderRepository;
	
	// entity 직접 노출 
	// entity 직접 노출 하면 무슨 문제가 있는지 체험
	// Hibernate5Module 사용
	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1() {
		
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		
		// Lazy 강제 초기화 
		// loop를 통해 프록시 객체가 아닌 실제 entity 객체에 접근하므로써 강제로 
		// 필요한 데이터를 초기화 
		for ( Order order : all ) {
			
			order.getMember().getName();
			order.getDelivery().getAddress();
			
		}
		
		return all;
		
	}
	
}
