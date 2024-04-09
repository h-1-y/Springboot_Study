package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;
	
	@NotEmpty
	private String name;
	
	@Embedded
	private Address address;
	
//	@JsonIgnore // <- @JsonIgnore 어노테이션 추가 시 JSON 데이터에서 제외 
	@OneToMany(mappedBy = "member") // 일대다 관계 맵핑 
	private List<Order> orders = new ArrayList<>();
	
}
