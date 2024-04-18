package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplerepository.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplerepository.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


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
	private final OrderSimpleQueryRepository orderSimpleQueryRepository;
	
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
	
	// entity 노출이 아닌 entity를 DTO로 변환하여 노출 방식 
	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDto> ordersV2() {
		
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		
		List<SimpleOrderDto> result = orders.stream()
									  .map(order -> new SimpleOrderDto(order))
									  .collect(Collectors.toList());
		
		return result;
		
	}
	
	// 패치조인 !! fetch join
	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDto> ordersV3() {
		
		List<Order> orders = orderRepository.findAllWithMemberDelivery();
		
		List<SimpleOrderDto> result = orders.stream()
				  .map(order -> new SimpleOrderDto(order))
				  .collect(Collectors.toList());
		
		return result;
		
	}
	
	// JPA를 통해 바로 DTO로 변환 
	@GetMapping("/api/v4/simple-orders")
	public List<OrderSimpleQueryDto> ordersV4() {
		
		return orderSimpleQueryRepository.findOrderDto();
		
	}
	
	@Data
	static class SimpleOrderDto {
		
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		
		public SimpleOrderDto(Order order) {
			
			this.orderId = order.getId();
			this.name = order.getMember().getName();
			this.orderDate = order.getOrderDate();
			this.orderStatus = order.getStatus();
			this.address = order.getDelivery().getAddress();
			
		}
		
	}
	
}
