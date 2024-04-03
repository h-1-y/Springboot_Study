package jpabook.jpashop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

	// @NoArgsConstructor(access = AccessLevel.PROTECTED) <- 이게 에러 나서 일단 프로텍티드 생성자 생성
	// 다른곳에서 생성하는 것을 막기 위함 
	protected Order() {}
	
	@Id
	@GeneratedValue
	@Column(name = "order_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 맵핑
	@JoinColumn(name = "member_id") // 조인을 어떤 컬럼으로 사용할 것인지 
	private Member member;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "delivery_id")
	private Delivery delivery;
	
	private LocalDateTime orderDate; // 주문시간
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status; // 주문상태 ORDER CANCEL
	
	
	// 연관관계 메서드 
	public void setMember(Member member) {
		
		this.member = member;
		member.getOrders().add(this);
		
	}
	
	public void addOrderItem(OrderItem orderItem) {
		
		orderItems.add(orderItem);
		orderItem.setOrder(this);
		
	}
	
	public void setDelivery(Delivery delivery) {
		
		this.delivery = delivery;
		delivery.setOrder(this);
		
	}
	
	// 생성 메서드 
	public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
		
		Order order = new Order();
		
		order.setMember(member);
		order.setDelivery(delivery);
		
		for ( OrderItem orderItem : orderItems ) {
			
			order.addOrderItem(orderItem);
			
		}
		
		order.setStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		
		return order;
		
	}
	
	// 비지니스 로직
	/**
	 * 주문 취소
	 */
	public void cancel() {
		
		// 배송이 이미 출발한 경우 
		if ( delivery.getStatus() == DeliveryStatus.COMP ) {
			throw new IllegalStateException("이미 배송완료된 상품은 취소 불가능");
		}
		
		this.setStatus(OrderStatus.CANCEL);
		
		for ( OrderItem orderItem : this.orderItems ) {
			
			orderItem.cancel();
			
		}
		
	}
	
	// 조회 로직
	/**
	 * 전체 주문 가격 조회
	 */
	public int getTotalPrice() {
		
		int totalPrice = 0;
		
		for ( OrderItem orderItem : this.orderItems ) {
			
			totalPrice += orderItem.getTotalPrice();
			
		}
		
		return totalPrice;
		
	}
	
}
