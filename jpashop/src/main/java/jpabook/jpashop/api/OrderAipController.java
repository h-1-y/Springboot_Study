package jpabook.jpashop.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderAipController {

	private final OrderRepository orderRepository;
	
	@GetMapping("/api/v1/orders")
	public List<Order> ordersV1() {
		
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		
		for ( Order order : all ) {
			
			order.getMember().getName(); // member LAZY init
			order.getDelivery().getAddress(); // delivery LAZY init
			
			List<OrderItem> orderItems = order.getOrderItems();
			
//			for ( OrderItem item : orderItems )
//				item.getItem().getName(); // orderItem LAZY init
			
			// 위의 반복 문을 람다로 처리
			orderItems.stream().forEach(orderItem -> orderItem.getItem().getName()); 
			
		}
		
		return all;
		
	}
	
}
