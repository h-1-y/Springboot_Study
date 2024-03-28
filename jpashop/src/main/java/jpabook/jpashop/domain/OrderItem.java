package jpabook.jpashop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {

	@Id
	@GeneratedValue
	@Column(name = "order_item_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY) // FetchType은 ManyToOne 사용 시 설정 타입은 EAGER , LAZY 가 있다 
	@JoinColumn(name = "item_id")
	private Item item;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
	private int orderPirce;
	
	private int count;

	// 생성 메서드 
	public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
		
		OrderItem orderItem = new OrderItem();
		
		orderItem.setItem(item);
		orderItem.setOrderPirce(orderPrice);
		orderItem.setCount(count);
		
		item.removeStock(count);
		
		return orderItem;
		
	}
	
	// 비즈니스 로직 
	public void cancel() {
		// 재고 수량 원복
		getItem().addStock(count);
	}

	// 주문 상품 전체 가격 조회 로직 
	public int getTotalPirce() {
		return getOrderPirce() * getCount();
	}
	
}
