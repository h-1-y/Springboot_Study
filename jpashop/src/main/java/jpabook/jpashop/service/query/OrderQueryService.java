package jpabook.jpashop.service.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

	private final OrderRepository orderRepository;
	
	public List<OrderDto> ordersV3() {
		
		List<Order> orders = orderRepository.findAllWithItem();
		
		List<OrderDto> result = orders.stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
		
		return result;
		
	}
	
}
