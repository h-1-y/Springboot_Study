package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.controller.BookForm;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	
	// merge
	@Transactional
	public void saveItem(Item item) {
		itemRepository.save(item);
	}
	
	// 변경감지 
	@Transactional
	public Item updateItem(Long itemId, BookForm param) {
		
		Item findItem = itemRepository.findOne(itemId);
		
		findItem.setName(param.getName());
		findItem.setPrice(param.getPrice());
		findItem.setStockQuantity(param.getStockQuantity());
		
		return findItem;
		
	}
	
	public List<Item> findItems() {
		return itemRepository.findAll();
	}
	
	public Item findOne(Long id) {
		return itemRepository.findOne(id);
	}
	
}
