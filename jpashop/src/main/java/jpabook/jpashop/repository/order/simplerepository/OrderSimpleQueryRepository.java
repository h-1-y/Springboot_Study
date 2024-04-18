package jpabook.jpashop.repository.order.simplerepository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

	private final EntityManager em;
	
	//JPA를 통해 바로 DTO로 변환 
	public List<OrderSimpleQueryDto> findOrderDto() {
		
		List<OrderSimpleQueryDto> orders = 
				em.createQuery(
								"select " +
								"new jpabook.jpashop.repository.order.simplerepository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
								"from Order o " +
								"join o.member m " +
								"join o.delivery d", OrderSimpleQueryDto.class
							  ).getResultList();
		
		return orders;
		
	}
	
}
