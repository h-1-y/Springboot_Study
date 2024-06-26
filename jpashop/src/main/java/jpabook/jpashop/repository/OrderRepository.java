package jpabook.jpashop.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import lombok.RequiredArgsConstructor;

@Repository
//@RequiredArgsConstructor
public class OrderRepository {

	private final EntityManager em;
	private final JPAQueryFactory query;
	
	public OrderRepository(EntityManager em) {
		
		this.em = em;
		this.query = new JPAQueryFactory(em);
		
	}
	
	public void save(Order order) {
		em.persist(order);
	}
	
	public Order findOne(Long id) {
		return em.find(Order.class, id);
	}
	
	// JPQL 동적 쿼리 방식... 미뛴...
	public List<Order> findAllByString(OrderSearch orderSearch) {
		
		String jpql = "select o from Order o join o.member m";
		boolean isFirstCondition = true;
		
		// 주문 상태 검색
		if ( orderSearch.getOrderStatus() != null ) {
			
			if ( isFirstCondition ) {
				
				jpql += " where";
				isFirstCondition = false;
				
			} else jpql += " and";
			
			jpql += " o.status = :status";
			
		}
		
		// 회원 이름 검색 
		if ( StringUtils.hasText(orderSearch.getMemberName()) ) {
			
			if ( isFirstCondition ) {
				
				jpql += " where";
				isFirstCondition = false;
				
			} else jpql += " and";
			
			jpql += " m.name like : name";
			
		}
		
		TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000);
		
		if ( orderSearch.getOrderStatus() != null )
			query = query.setParameter("status", orderSearch.getOrderStatus());
		
		if ( StringUtils.hasText(orderSearch.getMemberName()) )
			query = query.setParameter("name", orderSearch.getMemberName());
		
		return query.getResultList();
		
	}
	
	// JPA Criteria
	public List<Order> findAllByCriteria(OrderSearch orderSearch) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Order> cq = cb.createQuery(Order.class);
		
		Root<Order> o = cq.from(Order.class);
		Join<Object, Object> m = o.join("member", JoinType.INNER);
		
		List<Predicate> criteria = new ArrayList<>();
		
		// 주문 상태 검색 
		if ( orderSearch.getOrderStatus() != null ) {
			
			Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
			criteria.add(status);
			
		}
		
		// 회원 이름 검색
		if ( StringUtils.hasText(orderSearch.getMemberName()) ) {
			
			Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
			criteria.add(name);
			
		}
		
		cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
		
		TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
		
		return query.getResultList();
		
	}
	
	public List<Order> findAllQuerydsl(OrderSearch orderSearch) {
		
		return query
			.select(QOrder.order)
			.from(QOrder.order)
			.join(QOrder.order.member, QMember.member)
//			.where(order.status.eq(orderSearch.getOrderStatus())) 정적 
			.where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName())) // 동적 
			.limit(1000)
			.fetch();
		
	}
	
	private BooleanExpression nameLike(String memberName) {
		
		if ( !StringUtils.hasText(memberName) ) return null;
		
		return QMember.member.name.contains(memberName);
		
	}
	
	private BooleanExpression statusEq(OrderStatus status) {
		
		if ( status == null ) return null;
		
		return QOrder.order.status.eq(status);
		
	}
	
	// 정적 쿼리... jpa 
	public List<Order> findAll(OrderSearch orderSearch) {
		
		List<Order> resultList = em.createQuery( "select o from Order o join o.member m" +
												 "where o.status = :status" +
												 "and m.name like :name" , Order.class)
												.setParameter("status", orderSearch.getOrderStatus()) // 파라미터 바인딩 
												.setParameter("name", orderSearch.getMemberName()) // 파라미터 바인딩 
												.setMaxResults(1000) // 검색 결과 갯수 제한 
												.getResultList();
		
		return resultList;
		
	}

	// fetch join
	public List<Order> findAllWithMemberDelivery() {

		List<Order> orders = em.createQuery(
												"select o from Order o" +
												" join fetch o.member m" +
												" join fetch o.delivery d", Order.class
//												" left join fetch o.member m" +
//												" leff join fetch o.delivery d", Order.class
											).getResultList();
		
		
		return orders;
	}
	
	public List<Order> findAllWithItem() {
		
		return em.createQuery(
						"select distinct o from Order o " +
						"join fetch o.member m " +
						"join fetch o.delivery d " +
						"join fetch o.orderItems oi " +
						"join fetch oi.item i", Order.class
					).getResultList();
		
	}
	
	public List<Order> findAllWithMemberDelivery(int offset, int limit) {

		List<Order> orders = em.createQuery(
												"select o from Order o" +
												" join fetch o.member m" +
												" join fetch o.delivery d", Order.class
//												" left join fetch o.member m" +
//												" leff join fetch o.delivery d", Order.class
											).setFirstResult(offset).setMaxResults(limit).getResultList();
		
		return orders;
	}

}
