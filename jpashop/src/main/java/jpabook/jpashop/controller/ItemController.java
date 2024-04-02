package jpabook.jpashop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class ItemController { 

	private final ItemService itemService;
	
	@GetMapping("/items/new")
	public String createForm(Model model) {
		
		model.addAttribute("form", new BookForm());
		
		return "items/createItemForm";
		
	}
	
	@PostMapping("/items/new")
	public String create(BookForm form) {
		
		Book book = new Book();
		
		book.setName(form.getName());
		book.setPrice(form.getPrice());
		book.setStockQuantity(form.getStockQuantity());
		book.setAuthor(form.getAuthor());
		book.setIsbn(form.getIsbn());
		
		itemService.saveItem(book);
		
		return "redirect:/";
		
	}
	
	@GetMapping("/items")
	public String list(Model model) {
		
		List<Item> items = itemService.findItems();
		
		model.addAttribute("items", items);
		
		return "items/itemList";
		
	}
	
	@GetMapping("/items/{itemId}/edit")
	public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
		
		Book item = (Book) itemService.findOne(itemId);
		
		BookForm form = new BookForm();
		
		form.setId(item.getId());
		form.setName(item.getName());
		form.setPrice(item.getPrice());
		form.setStockQuantity(item.getStockQuantity());
		form.setAuthor(item.getAuthor());
		form.setIsbn(item.getIsbn());
		
		model.addAttribute("form", form);
		
		return "items/updateItemForm";
		
	}
	
	@PostMapping("/items/{itemId}/edit")
	public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form) {
		
		Book book = new Book();
		
		book.setId(form.getId());
		book.setName(form.getName());
		book.setPrice(form.getPrice());
		book.setStockQuantity(form.getStockQuantity());
		book.setAuthor(form.getAuthor());
		book.setIsbn(form.getIsbn());
		
		// merge 방식은 최대한 지양하고 수정시엔 번거롭더라도 변경감지 방식을 사용하는 것을 추천  
		
		// merge 방식 : merge는 set 해주지 않은 필드는 없는것(null)으로 감지하여 null로 update 되어버린다.
		// 수정이 필요없는 필드까지 기존값 유지가 되지않고 null로 업데이트 됨 
//		itemService.saveItem(book);
		
		// 변경감지 방식 : 변경감지의 경우 영속성 엔티티로 JPA에서 변경을 감지하고 감지된 부분만 UPDATE를 자동으로 처리해줌 
		itemService.updateItem(itemId, form);
		
		return "redirect:/items";
		
	}
	
}
