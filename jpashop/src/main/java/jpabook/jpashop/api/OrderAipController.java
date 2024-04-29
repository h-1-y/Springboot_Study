package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
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
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderAipController {

	private final OrderRepository orderRepository;
	
	private final OrderQueryRepository orderQueryRepository;
	
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
	
	@GetMapping("/api/v4/orders")
	public List<OrderQueryDto> ordersV4() {
		
		return orderQueryRepository.findOrderQueryDtos();
		
	}
	
	@GetMapping("/api/v5/orders")
	public List<OrderQueryDto> ordersV5() {
		
		return orderQueryRepository.findAllByDto_Optimization();
		
	}
	
	@GetMapping("/api/v6/orders")
	public List<OrderQueryDto> ordersV6() {
		
		List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
		
		List<OrderQueryDto> result = flats.stream()
				.filter(distinctByKey(o -> o.getOrderId()))
				.map(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()))
				.collect(Collectors.toList());
		
		List<OrderItemQueryDto> orderItems = flats.stream()
				.map(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getOrderPrice())).collect(Collectors.toList());
		
		Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
				.collect(Collectors.groupingBy(OrderItemQueryDto -> OrderItemQueryDto.getOrderId()));
		
		result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
		
//		List<OrderQueryDto> result = flats.stream()
//	            .collect(Collectors.groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
//	            		Collectors.mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())))
//	            .entrySet()
//	            .stream()
//	            .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
//	            .collect(Collectors.toList());
		
		 return result;
		 
	}
	
	/**
	 * 특정 키로 중복제거
	 *
	 * @param keyExtractor
	 * @param <T>
	 * @return
	 */
	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new HashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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
