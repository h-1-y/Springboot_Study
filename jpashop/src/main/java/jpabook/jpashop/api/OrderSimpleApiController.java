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
	
	// entity 를 직접 노출 하면 무슨 문제가 있는지 체험 
	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1() {
		
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		
		return all;
		
	}
	
}
