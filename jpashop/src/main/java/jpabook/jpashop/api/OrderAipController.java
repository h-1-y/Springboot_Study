package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
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
	
	@GetMapping("/api/v2/orders")
	public List<OrderDto> ordersV2() {
		
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		
		List<OrderDto> result = orders.stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
		
		return result;
		
	}
	
	@GetMapping("/api/v3/orders")
	public List<OrderDto> ordersV3() {
		
		List<Order> orders = orderRepository.findAllWithItem();
		
		List<OrderDto> result = orders.stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
		
		return result;
		
	}
	
	@GetMapping("/api/v3.1/orders")
	public List<OrderDto> ordersV3_page(
			  @RequestParam(value = "offset", defaultValue = "0") int offset
			, @RequestParam(value = "limit", defaultValue = "100") int limit
			) {
		
		List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
		
		List<OrderDto> result = orders.stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
		
		return result;
		
	}
	
	@Getter
	static class OrderDto {
		
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		private List<OrderItemDto> orderItems;
		
		public OrderDto(Order order) {
			
			orderId = order.getId();
			name = order.getMember().getName();
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress();
			
//			order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//			orderItems = order.getOrderItems();
			
			orderItems = order.getOrderItems().stream()
					.map(orderItem -> new OrderItemDto(orderItem))
					.collect(Collectors.toList());
			
		}
		
	}
	
	@Getter
	static class OrderItemDto {
		
		private String itemName;
		private int orderPrice;
		private int count;
		
		public OrderItemDto(OrderItem orderItem) {
			
			itemName = orderItem.getItem().getName();
			orderPrice = orderItem.getOrderPrice();
			count = orderItem.getCount();
			
		}
		
	}
	
}